package org.noisevisionproductions.noisevision.geoIp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geoip")
public class GeoIpController {

    @Autowired
    private GeoIpService geoIpService;

    @GetMapping("/{ip}")
    public ResponseEntity<GeoIpResponse> getLocation(@PathVariable String ip) {
        GeoIpResponse response = geoIpService.getLocation(ip);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
