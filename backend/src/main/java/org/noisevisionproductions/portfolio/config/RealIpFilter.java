package org.noisevisionproductions.portfolio.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RealIpFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String forwardedFor = httpServletRequest.getHeader("X-Forwarded-For");

        if (forwardedFor != null) {
            HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpServletRequest) {
                @Override
                public String getRemoteAddr() {
                    String[] ips = forwardedFor.split(",");
                    return ips[0].trim();
                }
            };
            filterChain.doFilter(wrapper, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }


}
