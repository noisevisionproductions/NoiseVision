package org.noisevisionproductions.portfolio.auth.component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Component
public class IpAddressExtractor {
    private static final Pattern IP_PATTERN =
            Pattern.compile("^([0-9]{1,3}\\.){3}[0-9]{1,3}$");

    private static final Set<String> INVALID_IPS = Set.of(
            "127.0.0.1",
            "0:0:0:0:0:0:0:1",
            "unknown"
    );

    public String getClientIpAddress(HttpServletRequest request) {
        log.debug("Extracting client IP address");

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (isValidIp(xForwardedFor)) {
            String clientIp = xForwardedFor.split(",")[0].trim();
            if (isRealExternalIp(clientIp)) {
                log.debug("IP from X-Forwarded-For: {}", clientIp);
                return clientIp;
            }
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (isValidIp(xRealIp) && isRealExternalIp(xRealIp)) {
            log.debug("IP from X-Real-IP: {}", xRealIp);
            return xRealIp;
        }

        String remoteAddr = request.getRemoteAddr();
        if (isRealExternalIp(remoteAddr)) {
            log.debug("IP from remote address: {}", remoteAddr);
            return remoteAddr;
        }

        log.warn("Nie udało się znaleźć prawidłowego IP. Używam adresu zdalnego: {}", remoteAddr);
        return remoteAddr;
    }

    private boolean isValidIp(String ip) {
        boolean valid = ip != null && !ip.isEmpty() && !INVALID_IPS.contains(ip.toLowerCase());
        if (valid) {
            log.debug("Znaleziono potencjalnie prawidłowe IP: {}", ip);
        } else {
            log.debug("Nieprawidłowe IP: {}", ip);
        }
        return valid;
    }

    private boolean isRealExternalIp(String ip) {
        if (!isValidIp(ip)) {
            return false;
        }

        if (!IP_PATTERN.matcher(ip).matches()) {
            log.debug("IP nie spełnia wzorca IPv4: {}", ip);
            return false;
        }

        if (ip.startsWith("172.") || ip.startsWith("192.168.") || ip.startsWith("10.")) {
            log.debug("Wykryto wewnętrzne IP: {}", ip);
            return false;
        }

        return true;
    }
}