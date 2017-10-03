package uk.nhs.careconnect.ri.daointerface.Transforms;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Location;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.careconnect.ri.daointerface.Transforms.builder.LocationEntityBuilder;
import uk.nhs.careconnect.ri.entity.location.LocationEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.equalTo;

@RunWith(MockitoJUnitRunner.class)
public class LocationEntityToFHIRLoactionTransformerTest {

    LocationEntityToFHIRLocationTransformer transformer = new LocationEntityToFHIRLocationTransformer();

    @Test
    public void testTransformLocationEntity(){

        LocationEntity locationEntity = new LocationEntityBuilder()
                .setName("Example Location")
                .addAddress("20 High Street", "Holmfirth", null,
                        "Halifax", "West Yorkshire", "HX1 2TT")
                .addHomePhone("0113240998")
                .build();

        Location location = transformer.transform(locationEntity);

        // Check that the Name has been populated
        assertThat(location, not(nullValue()));
        assertThat(location.getName(), equalTo("Example Location"));

        // Check that the Address has been populated
        Address address = location.getAddress();
        assertThat(address, not(nullValue()));
        assertThat(address.getLine().get(0).getValue(), equalTo("20 High Street"));
        assertThat(address.getLine().get(1).getValue(), equalTo("Holmfirth"));
        assertThat(address.getLine().get(2).getValue(), nullValue());
        assertThat(address.getLine().get(3).getValue(), nullValue());
        assertThat(address.getDistrict(), equalTo("West Yorkshire"));
        assertThat(address.getCity(), equalTo("Halifax"));
        assertThat(address.getPostalCode(), equalTo("HX1 2TT"));

        // Check that the Telephone Number has been populated
        assertThat(location.getTelecom(), not(nullValue()));
        assertThat(location.getTelecom().size(), equalTo(1));
        ContactPoint phoneNumber = location.getTelecom().get(0);
        assertThat(phoneNumber.getValue(), equalTo("0113240998"));
        assertThat(phoneNumber.getUse().getDisplay(), equalTo("Home"));
    }


}
