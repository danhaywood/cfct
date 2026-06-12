package com.danhaywood.cfct.webapp.automation;

import com.danhaywood.cfct.webapp.config.WebappComparisonProperties;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class AutomationBasicAuthFilter extends OncePerRequestFilter {

    static final String AUTOMATION_PATH_PREFIX = "/api/automation/";
    private static final String BASIC_PREFIX = "Basic ";
    private static final String AUTHENTICATE_HEADER = "Basic realm=\"CFCT Automation\"";

    private final WebappComparisonProperties comparisonProperties;

    public AutomationBasicAuthFilter(final WebappComparisonProperties comparisonProperties) {
        this.comparisonProperties = comparisonProperties;
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return !request.getRequestURI().startsWith(AUTOMATION_PATH_PREFIX);
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {
        if (!hasValidCredentials(request.getHeader("Authorization"))) {
            response.setHeader("WWW-Authenticate", AUTHENTICATE_HEADER);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean hasValidCredentials(final String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BASIC_PREFIX)) {
            return false;
        }
        final String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(authorizationHeader.substring(BASIC_PREFIX.length())), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        final int separator = decoded.indexOf(':');
        if (separator < 0) {
            return false;
        }
        final String username = decoded.substring(0, separator);
        final String password = decoded.substring(separator + 1);
        final WebappComparisonProperties.Automation automation = comparisonProperties.getAutomation();
        return constantTimeEquals(username, automation.getUsername())
                && constantTimeEquals(password, automation.getPassword())
                && automation.getUsername() != null
                && !automation.getUsername().isBlank();
    }

    private static boolean constantTimeEquals(final String candidate, final String expected) {
        if (candidate == null || expected == null) {
            return false;
        }
        return MessageDigest.isEqual(
                candidate.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8));
    }
}
