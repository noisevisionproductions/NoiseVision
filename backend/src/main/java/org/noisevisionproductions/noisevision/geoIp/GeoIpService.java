package org.noisevisionproductions.noisevision.geoIp;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Service
@Slf4j
public class GeoIpService {

    private DatabaseReader reader;

    @PostConstruct
    public void init() {
        try {
            Resource resource = new ClassPathResource("GeoLite2-City.mmdb");
            File database = resource.getFile();
            reader = new DatabaseReader.Builder(database).build();
        } catch (IOException e) {
            log.error("Could not initialize GeoIP database", e);
        }
    }

    public GeoIpResponse getLocation(String ipAddress) {
        try {
            CityResponse response = reader.city(InetAddress.getByName(ipAddress));
            return GeoIpResponse.builder()
                    .country(response.getCountry().getName())
                    .city(response.getCity().getName())
                    .latitude(response.getLocation().getLatitude())
                    .longitude(response.getLocation().getLongitude())
                    .region(response.getMostSpecificSubdivision().getName())
                    .build();
        } catch (Exception e) {
            log.error("Error getting location for IP: {}", ipAddress, e);
            return null;
        }
    }
}
