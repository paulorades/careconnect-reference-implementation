package uk.nhs.careconnect.ri.jpatest;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hl7.fhir.dstu3.model.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import uk.nhs.careconnect.ri.daointerface.*;
import uk.nhs.careconnect.ri.entity.Terminology.CodeSystemEntity;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptParentChildLink;
import uk.org.hl7.fhir.core.Stu3.CareConnectSystem;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;


public class JPAStepsDef {

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    ValueSetRepository valueSetRepository;

    @Autowired
    CodeSystemRepository codeSystemDao;

    @Autowired
    ConceptRepository conceptDao;

    @Autowired
    OrganisationRepository organisationRepository;

    @Autowired
    PractitionerRepository practitionerRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    ObservationRepository observationRepository;

    @Autowired
    EncounterRepository encounterRepository;

    @Autowired
    PractitionerRoleRepository practitionerRoleRepository;

    @Autowired
    TerminologyLoader myTermSvc;

    Patient patient;

    Location location;
    Resource resource;

    Observation observation;
    PractitionerRole practitionerRole;

    CodeSystemEntity cs;

    List<Patient> patientList = null;

    List<Organization> organizationList = null;

    List<Encounter> encounterList = null;

    List<Practitioner> practitionerList = null;
    List<Location> locationList = null;

    List<Observation> observationList = null;

    Transaction tx;

    static Boolean initialized = false;

    public  static FhirContext ctx = FhirContext.forDstu3();
    private static final FhirContext ourCtx = FhirContext.forDstu3();

    private static final String CS_URL = "http://example.com/my_code_system";
    



    private static FhirContext ctxFHIR = FhirContext.forDstu3();
    private static FhirValidator validator = ctxFHIR.newValidator();


    @Given("^I add a Patient with an Id of (\\d+)$")
    public void i_add_a_Patient_with_an_Id_of(Integer id) throws Throwable {
        patient = patientRepository.read(ctx,new IdType().setValue(id.toString()));
    }

    @Given("^I search for a Patient with a family name of (\\w+)$")
    public void i_search_for_a_Patient_with_a_family_name_of(String name) throws Throwable {
        patientList = patientRepository.searchPatient(ctx,null,null,null, new StringParam(name), null, null,null, null,null);
    }

    @Given("^I search for a Patient with a given name of (\\w+)$")
    public void i_search_for_a_Patient_with_a_given_name_of(String name) throws Throwable {
        patientList = patientRepository.searchPatient(ctx,null,null,null, null, null, new StringParam(name),null, null,null);
    }



    @Given("^I search for a Patient with a gender of (\\w+)$")
    public void i_search_for_a_Patient_with_a_gender_of(String gender) throws Throwable {
        patientList = patientRepository.searchPatient(ctx,null,null,null,null,new StringParam(gender), null,null,null, null);
    }

    @Given("^I search for a Patient with a NHSNumber of (\\d+)$")
    public void i_search_for_a_Patient_with_a_NHSNumber_of(String NHSNumber) throws Throwable {
        patientList = patientRepository.searchPatient(ctx,null,null,null,null,null,null,new TokenParam().setSystem(CareConnectSystem.NHSNumber).setValue(NHSNumber),null,null);
    }

    @Given("^I search for a Patient with a name of \"([^\"]*)\"$")
    public void i_search_for_a_Patient_with_a_name_of(String name) throws Throwable {
        patientList = patientRepository.searchPatient(ctx,null,null,null,null,null,null, null,new StringParam(name),null);
    }

    @Given("^I search for a Patient with a birthdate of '(\\w+)-(\\w+)-(\\w+)'$")
    public void i_search_for_a_Patient_with_a_birthdate_of(String year, String month, String day) throws Throwable {
        patientList = patientRepository.searchPatient(ctx,null, new DateRangeParam(new DateParam(ParamPrefixEnum.EQUAL,year+"-"+month+"-"+day)),null,null,null,null, null,null,null);

    }


    @Given("^I search for a Patient with a email of \"([^\"]*)\"$")
    public void i_search_for_a_Patient_with_a_email_of(String email) throws Throwable {
        patientList = patientRepository.searchPatient(ctx,null,null,new StringParam(email),null,null,null, null,null,null);

    }

    @Given("^I search for a Patient with a address-postcode of \"([^\"]*)\"$")
    public void i_search_for_a_Patient_with_a_address_postcode_of(String postcode) throws Throwable {
        patientList = patientRepository.searchPatient(ctx,new StringParam(postcode),null,null,null,null,null, null,null,null);
    }
    @Given("^I search for a Patient with a phone of \"([^\"]*)\"$")
    public void i_search_for_a_Patient_with_a_phone_of(String phone) throws Throwable {
        patientList = patientRepository.searchPatient(ctx,null,null,null,null,null,null, null,null,new StringParam(phone));
    }


    @Given("^I add a ValueSet with an Id of ([^\"]*)$")
    public void i_add_a_ValueSet_with_an_Id_of(String valueSetId) throws Throwable {
        resource = (Resource) valueSetRepository.read(new IdType().setValue("ValueSet/"+valueSetId));
    }

    @Then("^the result should be a FHIR ValueSet$")
    public void the_result_should_be_a_FHIR_ValueSet() throws Throwable {
        Assert.assertThat(resource,instanceOf(ValueSet.class));
    }


    // ORGANISATION


    @Given("^I search for Organisations by name (\\w+)$")
    public void i_search_for_Organisations_by_name(String name) throws Throwable {
        organizationList = organisationRepository.searchOrganization(null,new StringParam(name),null);
    }

    @Then("^the results should be a list of CareConnect Organisations$")
    public void the_results_should_be_a_list_of_CareConnect_Organisations() throws Throwable {
        for (Organization organization : organizationList) {
            validateResource(organization);
        }
    }

    @Given("^I search for Organisations by SDSCode (\\w+)$")
    public void i_have_search_for_these_Organisations_by_SDSCode(String SDSCode) throws Throwable {
        organizationList = organisationRepository.searchOrganization(new TokenParam().setSystem(CareConnectSystem.ODSOrganisationCode).setValue(SDSCode),null,null);
    }

    @Then("^the result should be a organisation list with (\\d+) entry$")
    public void the_result_should_be_a_organisation_list_with_entry(int count) throws Throwable {
        Assert.assertNotNull(organizationList);
        Assert.assertEquals(count,organizationList.size());
    }
    @Then("^the result should be a list with (\\d+) entry$")
    public void the_result_should_be_a_valid_FHIR_Bundle_with_entry(int count) throws Throwable {
        Assert.assertNotNull(patientList);
        Assert.assertEquals(count,patientList.size());

    }

    @Then("^the result should be a list with several entries$")
    public void the_result_should_be_a_list_with_several_entries() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        Assert.assertTrue(patientList.size() > 0);
    }

    @Then("^they shall all be FHIR Patient resources$")
    public void they_shall_all_be_FHIR_Patient_resources() throws Throwable {

        for (Patient patient : patientList) {
            Assert.assertThat(patient,instanceOf(Patient.class));
        }
    }

    @Then("^the result should be a FHIR Patient$")
    public void the_result_should_be_a_valid_FHIR_Patient() throws Throwable {
        System.out.println("Patient found = "+patient.getId()+ " "+patient.getName().get(0).getFamily());
        Assert.assertNotNull(patient);
        Assert.assertThat(patient,instanceOf(Patient.class));
    }

    @Then("^they shall all be FHIR Organization resources$")
    public void they_shall_all_be_FHIR_Organization_resources() throws Throwable {
        for (Organization organization : organizationList) {
            Assert.assertThat(organization,instanceOf(Organization.class));
        }
    }

    @Then("^the results should be a CareConnect Patient$")
    public void the_results_should_be_valid_CareConnect_Patients() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        validateResource(patient);
    }

    @Then("^the results should be a list of CareConnect Patients$")
    public void the_results_should_be_valid_CareConnect_Patient() throws Throwable {
        for (Patient patient : patientList) {
            validateResource(patient);
        }
    }

    @Given("^I add a dummy codesystem$")
    public void i_add_a_dummy_codesystem() throws Throwable {
        // Write code here that turns the phrase above into concrete actions

        Session session =  conceptDao.getSession();
         tx = conceptDao.getTransaction(session);
      //  conceptRepository.beginTransaction(tx);
        cs = codeSystemDao.findBySystem(CS_URL);


        ConceptEntity parent = new ConceptEntity();
        parent.setCodeSystem(cs);
        parent.setCode("parent");
        cs.getConcepts().add(parent);

        ConceptEntity child = new ConceptEntity();
        child.setCodeSystem(cs);
        child.setCode("child");
        parent.addChild(child, ConceptParentChildLink.RelationshipTypeEnum.ISA);
    // use something like this for circular references
     //   child.addChild(parent, ConceptParentChildLink.RelationshipTypeEnum.ISA);
    }

    @Then("^the CodeSystem should save$")
    public void the_CodeSystem_should_save() throws Throwable {
        try {
            conceptDao.storeNewCodeSystemVersion( cs,null);
           // fail();
        } catch (InvalidRequestException e) {
            assertEquals("CodeSystem contains circular reference around code parent", e.getMessage());
        }
    }


    //PRACTITIONER



    @Then("^the result should be a practitioner list with (\\d+) entry$")
    public void the_result_should_be_a_practitioner_list_with_entry(int count) throws Throwable {
        Assert.assertNotNull(practitionerList);
        Assert.assertEquals(count,practitionerList.size());
    }

    @Then("^they shall all be FHIR Practitioner resources$")
    public void they_shall_all_be_FHIR_Practitioner_resources() throws Throwable {
        for (Practitioner practitioner : practitionerList) {
            Assert.assertThat(practitioner,instanceOf(Practitioner.class));
        }
    }

    @Then("^the results should be a list of CareConnect Practitioners$")
    public void the_results_should_be_a_list_of_CareConnect_Practitioners() throws Throwable {
        for (Practitioner practitioner : practitionerList) {
            validateResource(practitioner);
        }
    }

    @Given("^I search for Practitioners by SDSId (\\w+)$")
    public void i_search_for_Practitioners_by_SDSId_S(String Id) throws Throwable {
        practitionerList = practitionerRepository.searchPractitioner(new TokenParam().setSystem(CareConnectSystem.SDSUserId).setValue(Id),null,null);
    }

    @Given("^I search for Practitioners by name (\\w+)$")
    public void i_search_for_Practitioners_by_name_Bhatia(String name) throws Throwable {
        practitionerList = practitionerRepository.searchPractitioner(null,new StringParam(name),null);
    }


    // Location

    @Given("^I search for Locations by SDSCode (\\w+)$")
    public void i_search_for_Locations_by_SDSCode(String code) throws Throwable {
        locationList = locationRepository.searchLocation(new TokenParam().setSystem(CareConnectSystem.ODSSiteCode).setValue(code),null,null);
    }

    @Then("^the result should be a Location list with (\\d+) entry$")
    public void the_result_should_be_a_Location_list_with_entry(int count) throws Throwable {
        Assert.assertNotNull(locationList);
        Assert.assertEquals(count,locationList.size());
    }

    @Then("^they shall all be FHIR Location resources$")
    public void they_shall_all_be_FHIR_Location_resources() throws Throwable {
        for (Location location : locationList) {
            Assert.assertThat(location,instanceOf(Location.class));
        }
    }

    @Then("^the results should be a list of CareConnect Locations$")
    public void the_results_should_be_a_list_of_CareConnect_Locations() throws Throwable {
        for (Location location : locationList) {
            validateResource(location);
        }
    }


    @Given("^I search for Locations by name (\\w+)$")
    public void i_search_for_Locations_by_name(String name) throws Throwable {
        locationList = locationRepository.searchLocation(null,new StringParam(name),null);
    }

    @Given("^Location resource file$")
    public void location_resource_file() throws Throwable {
        InputStream inputStream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("xml/Location.xml");
        assertNotNull(inputStream);
        Reader reader = new InputStreamReader(inputStream);

        location = ctx.newXmlParser().parseResource(Location.class,reader);
    }

    @Given("^Observation resource file$")
    public void observation_resource_file() throws Throwable {
        InputStream inputStream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("xml/Observation.xml");
        assertNotNull(inputStream);
        Reader reader = new InputStreamReader(inputStream);

        observation = ctx.newXmlParser().parseResource(Observation.class,reader);
    }

    @Given("^PractitionerRole resource file$")
    public void practitionerrole_resource_file() throws Throwable {
        InputStream inputStream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("xml/PractitionerRole.xml");
        assertNotNull(inputStream);
        Reader reader = new InputStreamReader(inputStream);

        practitionerRole = ctx.newXmlParser().parseResource(PractitionerRole.class,reader);
    }

    @Then("^save the PractitionerRole$")
    public void save_the_PractitionerRole() throws Throwable {
       practitionerRoleRepository.create(practitionerRole,null,null);
    }

    @Then("^save the Observation$")
    public void save_the_Observation() throws Throwable {
        observationRepository.save(ctx,observation);
    }

    @Then("^save the location$")
    public void save_the_location() throws Throwable {
        locationRepository.create(location,null,"Location?identifier="+location.getIdentifier().get(0).getSystem()+"%7C"+location.getIdentifier().get(0).getValue());
    }


    @Given("^I have two sample resources loaded$")
    public void i_have_two_sample_resources_loaded() throws Throwable {

    }

    @When("^I search Observations on SNOMED category (\\d+)$")
    public void i_search_on_SNOMED_category(String category) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        observationList = observationRepository.search(ctx,new TokenParam().setValue(category).setSystem(CareConnectSystem.SNOMEDCT),null,null,null);
    }

    @Then("^I should get a Bundle of Observations with (\\d+) resource$")
    public void i_should_get_a_Bundle_of_Observations_with_resource(int count) throws Throwable {

        assertEquals(count,observationList.size());
    }

    @When("^I search Observations on SNOMED code (\\d+)$")
    public void i_search_on_SNOMED_code(String code) throws Throwable {
        observationList = observationRepository.search(ctx,null, new TokenParam().setValue(code).setSystem(CareConnectSystem.SNOMEDCT),null,null);
    }

    @When("^I search on Patient ID = (\\d+)$")
    public void i_search_on_Patient_ID(String patientId) throws Throwable {
        observationList = observationRepository.search(ctx,null, null, null, new ReferenceParam("Patient/"+patientId));
    }

    @Then("^I should get a Bundle of Observations with more then (\\d+) resources$")
    public void i_should_get_a_Bundle_of_Observations_with_more_than_resource(int count) throws Throwable {
        assertTrue("Expected "+count+" and actual "+ observationList.size(),count<observationList.size());
    }

    @When("^I search on dates less than (\\d+)-(\\d+)-(\\d+)$")
    public void dates_less_than_yyyymmdd(String year, String month, String day) throws Throwable {
        observationList = observationRepository.search(ctx,null, null, new DateRangeParam(new DateParam(ParamPrefixEnum.LESSTHAN,year+"-"+month+"-"+day)), null);
    }

    @When("^I search on dates equal to (\\d+)-(\\d+)-(\\d+)$")
    public void dates_equal_yyyymmdd(String year, String month, String day) throws Throwable {
        observationList = observationRepository.search(ctx,null, null, new DateRangeParam(new DateParam(ParamPrefixEnum.EQUAL,year+"-"+month+"-"+day)), null);
    }

    @When("^I search on dates equal to (\\d+)-(\\d+)$")
    public void dates_equal_yyyymm(String year, String month) throws Throwable {
        observationList = observationRepository.search(ctx,null, null, new DateRangeParam(new DateParam(ParamPrefixEnum.EQUAL,year+"-"+month)), null);
    }

    @When("^I search on dates equal to (\\d+)$")
    public void dates_equal_yyyy(String year) throws Throwable {
        observationList = observationRepository.search(ctx,null, null, new DateRangeParam(new DateParam(ParamPrefixEnum.EQUAL,year)), null);
    }

    @Given("^I have one Encounter resource loaded$")
    public void i_have_one_Encounter_resource_loaded() throws Throwable {

    }

    @When("^I search Encounter on Patient ID = (\\d+)$")
    public void i_search_Encounter_on_Patient_ID(int patient) throws Throwable {
        encounterList = encounterRepository.search(ctx, new ReferenceParam("Patient/"+patient),null,null, null);
    }

    @Then("^I should get a Bundle of Encounter (\\d+) resource$")
    public void i_should_get_a_Bundle_of_Encounter_resource(int arg1) throws Throwable {
        assertEquals(1,encounterList.size());
    }


    private void validateResource(Resource resource) {
        ValidationResult result = validator.validateWithResult(resource);

        // Show the issues
        // Colour values https://github.com/yonchu/shell-color-pallet/blob/master/color16
        for (SingleValidationMessage next : result.getMessages()) {
            switch (next.getSeverity())
            {
                case ERROR:
                    fail("FHIR Validation ERROR - "+ next.getMessage());
                    break;
                case WARNING:
                    //fail("FHIR Validation WARNING - "+ next.getMessage());
                    System.out.println(  (char)27 + "[34mWARNING" + (char)27 + "[0m" + " - " +  next.getLocationString() + " - " + next.getMessage());
                    break;
                case INFORMATION:
                    System.out.println( (char)27 + "[34mINFORMATION" + (char)27 + "[0m" + " - " +  next.getLocationString() + " - " + next.getMessage());
                    break;
                default:
                    System.out.println(" Next issue " + next.getSeverity() + " - " + next.getLocationString() + " - " + next.getMessage());
            }
        }
    }
    @BeforeClass
    public static void beforeClass(){
        initialized = false;
    }

    @Before
    public void before() throws Exception {
		/*
		 * This runs under maven, and I'm not sure how else to figure out the target directory from code..
		 */

		if (!initialized) {

            cs = codeSystemDao.findBySystem(CareConnectSystem.SNOMEDCT);

            ConceptEntity concept = new ConceptEntity();
            concept.setCodeSystem(cs);
            concept.setCode("228272008");
            conceptDao.save(concept);

            concept = new ConceptEntity();
            concept.setCodeSystem(cs);
            concept.setCode("229819007");
            conceptDao.save(concept);

            concept = new ConceptEntity();
            concept.setCodeSystem(cs);
            concept.setCode("365605003");
            conceptDao.save(concept);

            concept = new ConceptEntity();
            concept.setCodeSystem(cs);
            concept.setCode("301331008");
            conceptDao.save(concept);

            concept = new ConceptEntity();
            concept.setCodeSystem(cs);
            concept.setCode("162864005");
            conceptDao.save(concept);

            concept = new ConceptEntity();
            concept.setCodeSystem(cs);
            concept.setCode("86290005");
            conceptDao.save(concept);

            InputStream inputStream =
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("json/Vital-Body-Mass-Example.json");
            assertNotNull(inputStream);
            Reader reader = new InputStreamReader(inputStream);

            Observation observation = ctx.newJsonParser().parseResource(Observation.class, reader);
            try {
                observation = observationRepository.save(ctx,observation);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }


            inputStream =
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("json/Social-History-SmokingStatus.json");
            assertNotNull(inputStream);
            reader = new InputStreamReader(inputStream);

            observation = ctx.newJsonParser().parseResource(Observation.class, reader);
            try {
                observation = observationRepository.save(ctx,observation);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            inputStream =
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("json/observationExample.json");
            assertNotNull(inputStream);
            reader = new InputStreamReader(inputStream);

            observation = ctx.newJsonParser().parseResource(Observation.class, reader);
            try {
                observation = observationRepository.save(ctx,observation);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            inputStream =
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("json/EncounterExample.json");
            assertNotNull(inputStream);
            reader = new InputStreamReader(inputStream);

            Encounter encounter = ctx.newJsonParser().parseResource(Encounter.class, reader);
            try {
                encounter = encounterRepository.create(ctx,encounter,null,null);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            initialized = true;

        }


        if (validator == null) {
            /* TODO STU3
            FhirInstanceValidator instanceValidator = new FhirInstanceValidator();
            validator.registerValidatorModule(instanceValidator);

            IValidationSupport valSupport = new CareConnectValidation();
            ValidationSupportChain support = new ValidationSupportChain(new DefaultProfileValidationSupport(), valSupport);
            instanceValidator.setValidationSupport(support);
            */
        }
        else {
           // ourLog.info("START - CALLED NOT Creating Server");
        }
    }

}
