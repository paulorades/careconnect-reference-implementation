package uk.nhs.careconnect.ri.daointerface.Transforms;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.junit.Test;
import uk.nhs.careconnect.ri.daointerface.Transforms.builder.PractitionerEntityBuilder;
import uk.nhs.careconnect.ri.entity.practitioner.PractitionerEntity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.equalTo;

public class PractitionerEntityToFHIRPractitionerTransformerTest {

    PractitionerEntityToFHIRPractitionerTransformer transformer = new PractitionerEntityToFHIRPractitionerTransformer();

    @Test
    public void testTransformer(){

        PractitionerEntity practitionerEntity = new PractitionerEntityBuilder()
                .addName("Dr", "Jenny", "Jones")
                .addAddress("Church Lane Surgery", "Holmfirth", null,
                        "Halifax", "West Yorkshire", "HX1 2TT")
                .build();

        Practitioner practitioner = transformer.transform(practitionerEntity);
        assertThat(practitioner, not(nullValue()));
        assertThat(practitioner.getId(), not(nullValue()));
        assertThat(practitioner.getId(), equalTo((new Long(PractitionerEntityBuilder.DEFAULT_ID)).toString()));
        assertThat(practitioner.getActive(), equalTo(true));

        List<HumanName> practitionerNames = practitioner.getName();
        assertThat(practitionerNames, not(nullValue()));
        assertThat(practitionerNames.size(), equalTo(1));
        //assertThat(practitionerNames.get(0).getUse(), equalTo(HumanName.NameUse.USUAL));
        HumanName name = practitionerNames.get(0);
        assertThat(name.getPrefixAsSingleString(), equalTo("Dr"));
        assertThat(name.getGivenAsSingleString(), equalTo("Jenny"));
        assertThat(name.getFamily(), equalTo("Jones"));

        assertThat(practitioner.getAddress(), not(nullValue()));
        List<Address> addresses = practitioner.getAddress();
        assertThat(addresses.size(), equalTo(1));
        Address address = addresses.get(0);
        assertThat(address.getLine().get(0).getValue(), equalTo("Church Lane Surgery"));
        assertThat(address.getLine().get(1).getValue(), equalTo("Holmfirth"));
        assertThat(address.getLine().get(2).getValue(), nullValue());
        assertThat(address.getDistrict(), equalTo("West Yorkshire"));
        assertThat(address.getCity(), equalTo("Halifax"));
        assertThat(address.getPostalCode(), equalTo("HX1 2TT"));
        assertThat(address.getUse(), equalTo(Address.AddressUse.WORK));

    }

}
