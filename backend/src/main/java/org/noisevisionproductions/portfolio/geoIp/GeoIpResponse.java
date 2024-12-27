package org.noisevisionproductions.portfolio.geoIp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeoIpResponse {
    private String country;
    private String city;
    private String region;
    private Double latitude;
    private Double longitude;
}
