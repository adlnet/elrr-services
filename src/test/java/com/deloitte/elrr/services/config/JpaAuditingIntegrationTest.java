package com.deloitte.elrr.services.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Unit test to verify AuditorAware implementation is working properly.
 */
public class JpaAuditingIntegrationTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAuditorAwareImplReturnsCurrentUser() {
        // Setup security context with a test user (using constructor with authorities)
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken("testuser", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Create and test the auditor aware implementation
        AuditorAwareImpl auditorAware = new AuditorAwareImpl();
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

        // Verify the auditor is returned correctly
        assertTrue(currentAuditor.isPresent(), "Current auditor should be present");
        assertEquals("testuser", currentAuditor.get(), "Current auditor should match authenticated user");
    }

    @Test
    void testAuditorAwareImplReturnsEmptyWhenNoAuthentication() {
        // Ensure no security context is set
        SecurityContextHolder.clearContext();

        // Create and test the auditor aware implementation
        AuditorAwareImpl auditorAware = new AuditorAwareImpl();
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

        // Verify no auditor is returned when not authenticated
        assertTrue(currentAuditor.isEmpty(), "Current auditor should be empty when not authenticated");
    }
}
