package org.noisevisionproductions.noisevision.geoIp;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Subdivision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoIpServiceTest {

    @InjectMocks
    private GeoIpService geoIpService;

    @Mock
    private DatabaseReader reader;

    @BeforeEach
    void setUp() throws Exception {
        Field readerField = GeoIpService.class.getDeclaredField("reader");
        readerField.setAccessible(true);
        readerField.set(geoIpService, reader);
    }

    @Test
    void getLocation_ShouldReturnGeoIpResponse_WhenValidIp() throws Exception {
        String ipAddress = "8.8.8.8";
        CityResponse cityResponse = mock(CityResponse.class);
        Country country = mock(Country.class);
        City city = mock(City.class);
        Location location = mock(Location.class);
        Subdivision subdivision = mock(Subdivision.class);

        when(reader.city(InetAddress.getByName(ipAddress))).thenReturn(cityResponse);
        when(cityResponse.getCountry()).thenReturn(country);
        when(cityResponse.getCity()).thenReturn(city);
        when(cityResponse.getLocation()).thenReturn(location);
        when(cityResponse.getMostSpecificSubdivision()).thenReturn(subdivision);

        when(country.getName()).thenReturn("United States");
        when(city.getName()).thenReturn("Mountain View");
        when(location.getLatitude()).thenReturn(37.386);
        when(location.getLongitude()).thenReturn(-122.0838);
        when(subdivision.getName()).thenReturn("California");

        GeoIpResponse response = geoIpService.getLocation(ipAddress);

        assertNotNull(response);
        assertEquals("United States", response.getCountry());
        assertEquals("Mountain View", response.getCity());
        assertEquals("California", response.getRegion());
        assertEquals(37.386, response.getLatitude());
        assertEquals(-122.0838, response.getLongitude());
    }

    @Test
    void getLocation_ShouldReturnNull_WhenNullIpAddress() {
        GeoIpResponse response = geoIpService.getLocation(null);

        assertNull(response);
    }

    @Test
    void getLocation_ShouldReturnNull_WhenExceptionOccurs() throws Exception{
        String ipAddress = "8.8.8.8";
        when(reader.city(InetAddress.getByName(ipAddress))).thenThrow(new IOException());

        GeoIpResponse response = geoIpService.getLocation(ipAddress);

        assertNull(response);
    }
}