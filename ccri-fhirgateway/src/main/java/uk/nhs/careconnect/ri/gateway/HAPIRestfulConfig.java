package uk.nhs.careconnect.ri.gateway;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import ca.uhn.fhir.util.VersionUtil;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import uk.nhs.careconnect.ri.gateway.provider.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class HAPIRestfulConfig extends RestfulServer {

	private static final long serialVersionUID = 1L;

	private WebApplicationContext myAppCtx;

	@Value("${fhir.resource.serverBase}")
	private String serverBase;

    @Value("${fhir.resource.serverName}")
    private String serverName;

    @Value("${fhir.resource.serverVersion}")
    private String serverVersion;

    @Override
	public void addHeadersToResponse(HttpServletResponse theHttpResponse) {
		theHttpResponse.addHeader("X-Powered-By", "HAPI FHIR " + VersionUtil.getVersion() + " RESTful Server (NHS Care Connect STU3)");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initialize() throws ServletException {
		super.initialize();


		FhirVersionEnum fhirVersion = FhirVersionEnum.DSTU3;
		setFhirContext(new FhirContext(fhirVersion));

		// Get the spring context from the web container (it's declared in web.xml)
		myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();

        if (serverBase != null && !serverBase.isEmpty()) {
            setServerAddressStrategy(new HardcodedServerAddressStrategy(serverBase));
        }


		setResourceProviders(Arrays.asList(
				 myAppCtx.getBean(PatientResourceProvider.class)
               	,myAppCtx.getBean(OrganisationResourceProvider.class)
                ,myAppCtx.getBean(PractitionerResourceProvider.class)
                ,myAppCtx.getBean(LocationResourceProvider.class)
               	,myAppCtx.getBean(PractitionerRoleResourceProvider.class)
      			,myAppCtx.getBean(ObservationResourceProvider.class) // Spring 4 addition KGM
		));

        // Replace built in conformance provider (CapabilityStatement)
        setServerConformanceProvider(new CareConnectConformanceProvider());

        setServerName(serverName);
        setServerVersion(serverVersion);

		LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
		registerInterceptor(loggingInterceptor);

		loggingInterceptor.setLoggerName("ccri.FHIRGateway");

		// This is the format for each line. A number of substitution variables may
		// be used here. See the JavaDoc for LoggingInterceptor for information on
		// what is available.
		loggingInterceptor.setMessageFormat("Source[${remoteAddr}] Operation[${operationType} ${idOrResourceName}] UA[${requestHeader.user-agent}] Params[${requestParameters}]");


		setDefaultPrettyPrint(true);
		setDefaultResponseEncoding(EncodingEnum.JSON);

		FhirContext ctx = getFhirContext();
		// Remove as believe due to issues on docker ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());
	}

	/**
	 * This interceptor adds some pretty syntax highlighting in responses when a browser is detected
	 */
	@Bean(autowire = Autowire.BY_TYPE)
	public IServerInterceptor responseHighlighterInterceptor() {
		ResponseHighlighterInterceptor retVal = new ResponseHighlighterInterceptor();
		return retVal;
	}



}
