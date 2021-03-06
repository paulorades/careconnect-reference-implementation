package uk.nhs.careconnect.cli;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.fusesource.jansi.Ansi.Color;
import org.hl7.fhir.dstu3.hapi.validation.DefaultProfileValidationSupport;
import org.hl7.fhir.dstu3.hapi.validation.FhirInstanceValidator;
import org.hl7.fhir.dstu3.hapi.validation.ValidationSupportChain;
import org.hl7.fhir.dstu3.model.StructureDefinition;
import org.hl7.fhir.instance.model.api.IBaseResource;
import uk.org.hl7.fhir.core.Dstu2.CareConnectSystem;
import uk.org.hl7.fhir.validation.stu3.CareConnectProfileValidationSupport;
import uk.org.hl7.fhir.validation.stu3.SNOMEDUKMockValidationSupport;

import java.io.*;

import static org.apache.commons.lang3.StringUtils.*;
import static org.fusesource.jansi.Ansi.ansi;


public class ValidateCommand extends BaseCommand {

	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(ValidateCommand.class);

	@Override
	public String getCommandDescription() {
		return "Validate a resource using the FHIR validation tools";
	}

	@Override
	public String getCommandName() {
		return "validate";
	}

	@Override
	public Options getOptions() {
		Options retVal = new Options();
		addFhirVersionOption(retVal);

		OptionGroup source = new OptionGroup();
		source.addOption(new Option("n", "file", true, "The name of the file to validate"));
		source.addOption(new Option("d", "data", true, "The text to validate"));
		retVal.addOptionGroup(source);

		retVal.addOption("x", "xsd", false, "Validate using Schemas");
		retVal.addOption("s", "sch", false, "Validate using Schematrons");
		retVal.addOption("p", "profile", false, "Validate using Profiles (StructureDefinition / ValueSet)");
		retVal.addOption("r", "fetch-remote", false,
				"Allow fetching remote resources (in other words, if a resource being validated refers to an external StructureDefinition, Questionnaire, etc. this flag allows the validator to access the internet to try and fetch this resource)");
		retVal.addOption(new Option("l", "fetch-local", true, "Fetch a profile locally and use it if referenced"));
		retVal.addOption("e", "encoding", false, "File encoding (default is UTF-8)");

		return retVal;
	}

	@Override
	public void run(CommandLine theCommandLine) throws ParseException, Exception {
		String fileName = theCommandLine.getOptionValue("n");
		String contents = theCommandLine.getOptionValue("c");
		if (isNotBlank(fileName) && isNotBlank(contents)) {
			throw new ParseException("Can not supply both a file (-n) and data (-d)");
		}
		if (isBlank(fileName) && isBlank(contents)) {
			throw new ParseException("Must supply either a file (-n) or data (-d)");
		}

		if (isNotBlank(fileName)) {
			String encoding = theCommandLine.getOptionValue("e", "UTF-8");
			ourLog.info("Reading file '{}' using encoding {}", fileName, encoding);

			contents = IOUtils.toString(new InputStreamReader(new FileInputStream(fileName), encoding));
			//ourLog.info("Fully read - Size is {}", FileUtils.getFileSizeDisplay(contents.length()));
		}

		ca.uhn.fhir.rest.api.EncodingEnum enc = ca.uhn.fhir.rest.api.EncodingEnum.detectEncodingNoDefault(defaultString(contents));
		if (enc == null) {
			throw new ParseException("Could not detect encoding (json/xml) of contents");
		}

		FhirContext ctx = getSpecVersionContext(theCommandLine);
		FhirValidator val = ctx.newValidator();

		IBaseResource localProfileResource = null;
		if (theCommandLine.hasOption("l")) {
			String localProfile = theCommandLine.getOptionValue("l");
			ourLog.info("Loading profile: {}", localProfile);
			String input;
			try {
				input = IOUtils.toString(new FileReader(new File(localProfile)));
			} catch (IOException e) {
				throw new ParseException("Failed to load file '" + localProfile + "' - Error: " + e.toString());
			}

			localProfileResource = ca.uhn.fhir.rest.api.EncodingEnum.detectEncodingNoDefault(input).newParser(ctx).parseResource(input);
		}

		if (theCommandLine.hasOption("p")) {
			switch (ctx.getVersion().getVersion()) {

			case DSTU3: {
				FhirInstanceValidator instanceValidator = new FhirInstanceValidator();
				val.registerValidatorModule(instanceValidator);
				ValidationSupportChain validationSupport = new ValidationSupportChain(
				        new DefaultProfileValidationSupport()
                        ,new CareConnectProfileValidationSupport()
						,new SNOMEDUKMockValidationSupport() // This is to disable SNOMED CT Warnings. Mock validation to return ok for SNOMED Concepts
                );
				if (localProfileResource != null) {
					instanceValidator.setStructureDefintion((StructureDefinition) localProfileResource);
				}

				if (theCommandLine.hasOption("r")) {
					validationSupport.addValidationSupport(new LoadingValidationSupportDstu3());
				}
                val.setValidateAgainstStandardSchema(true);

				instanceValidator.setValidationSupport(validationSupport);
				break;
			}
			default:
				throw new ParseException("Profile validation (-p) is not supported for this FHIR version");
			}
		}

		val.setValidateAgainstStandardSchema(theCommandLine.hasOption("x"));
		val.setValidateAgainstStandardSchematron(theCommandLine.hasOption("s"));

		ValidationResult results = val.validateWithResult(contents);

		StringBuilder b = new StringBuilder("Validation results:" + ansi().boldOff());
		int count = 0;
		for (SingleValidationMessage next : results.getMessages()) {

			if (next.getMessage().contains("and a code from this value set is required") && next.getMessage().contains(CareConnectSystem.SNOMEDCT)) {
			//	System.out.println("match **");
			} else if (next.getMessage().contains("a code is required from this value set") && next.getMessage().contains(CareConnectSystem.SNOMEDCT)) {
			//	System.out.println("match ** ** ");
			} else if (next.getMessage().contains("and a code is recommended to come from this value set") && next.getMessage().contains(CareConnectSystem.SNOMEDCT)) {
			//	System.out.println("match ** ** **" );
			}else {

				count++;
				b.append(App.LINESEP);
				String leftString = "Issue " + count + ": ";
				int leftWidth = leftString.length();
				b.append(ansi().fg(Color.GREEN)).append(leftString);
				if (next.getSeverity() != null) {
					b.append(next.getSeverity()).append(ansi().fg(Color.WHITE)).append(" - ");
				}
				if (isNotBlank(next.getLocationString())) {
					b.append(ansi().fg(Color.WHITE)).append(next.getLocationString());
				}
				String[] message = WordUtils.wrap(next.getMessage(), 80 - leftWidth, "\n", true).split("\\n");
				for (String line : message) {
					b.append(App.LINESEP);
					b.append(ansi().fg(Color.WHITE));
					b.append(leftPad("", leftWidth)).append(line);
				}
				ourLog.info(message.toString());
			}

		}
		b.append(App.LINESEP);

		if (count > 0) {
			ourLog.info(b.toString());
            System.out.println(b.toString());
		}

		if (results.isSuccessful()) {
			ourLog.info("Validation successful!");
		} else {
			ourLog.warn("Validation FAILED");
		}
	}
}
