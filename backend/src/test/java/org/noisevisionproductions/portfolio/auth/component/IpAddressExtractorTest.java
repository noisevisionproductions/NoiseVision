package org.noisevisionproductions.portfolio.auth.component;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IpAddressExtractorTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private IpAddressExtractor ipAddressExtractor;

    @BeforeEach
    void setUp() {
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Test
    void shouldExtractIpFromXForwardedFor() {
        String expectedIp = "82.10.20.30";
        when(request.getHeader("X-Forwarded-For")).thenReturn(expectedIp);

        String result = ipAddressExtractor.getClientIpAddress(request);

        assertThat(result).isEqualTo(expectedIp);
    }

    @Test
    void shouldExtractFirstIpFromXForwardedForChain() {
        String expectedIp = "82.10.20.30";
        when(request.getHeader("X-Forwarded-For"))
                .thenReturn("82.10.20.30, 172.10.0.1, 192.168.1.1");

        String result = ipAddressExtractor.getClientIpAddress(request);

        assertThat(result).isEqualTo(expectedIp);
    }

    @Test
    void shouldExtractIpFromXRealIpWhenXForwardedForIsInvalid() {
        String expectedIp = "82.10.20.30";
        when(request.getHeader("X-Forwarded-For")).thenReturn("unknown");
        when(request.getHeader("X-Real-IP")).thenReturn(expectedIp);

        String result = ipAddressExtractor.getClientIpAddress(request);

        assertThat(result).isEqualTo(expectedIp);
    }

    @Test
    void shouldRejectDockerNetworkIp() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("172.18.0.1");
        when(request.getHeader("X-Real-IP")).thenReturn("192.168.1.1");
        when(request.getRemoteAddr()).thenReturn("82.10.20.30");

        String result = ipAddressExtractor.getClientIpAddress(request);

        assertThat(result).isEqualTo("82.10.20.30");
    }

    @Test
    void shouldRejectInvalidIpFormat() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("invalid-ip");
        when(request.getHeader("X-Real-IP")).thenReturn("82.10.20.30");

        String result = ipAddressExtractor.getClientIpAddress(request);

        assertThat(result).isEqualTo("82.10.20.30");
    }

    @Test
    void shouldHandleAllInvalidIpsAndReturnRemoteAddr() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("unknown");
        when(request.getHeader("X-Real-IP")).thenReturn("172.18.0.1");
        when(request.getRemoteAddr()).thenReturn("82.10.20.30");

        String result = ipAddressExtractor.getClientIpAddress(request);

        assertThat(result).isEqualTo("82.10.20.30");
    }

    @Test
    void shouldHandleNullHeaders() {
        String expectedIp = "82.10.20.30";
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(expectedIp);

        String result = ipAddressExtractor.getClientIpAddress(request);

        assertThat(result).isEqualTo(expectedIp);
    }

    @Test
    void shouldIgnoreLocalhost() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("127.0.0.1");
        when(request.getHeader("X-Real-IP")).thenReturn("0:0:0:0:0:0:0:1");
        when(request.getRemoteAddr()).thenReturn("82.10.20.30");

        String result = ipAddressExtractor.getClientIpAddress(request);

        assertThat(result).isEqualTo("82.10.20.30");
    }
}