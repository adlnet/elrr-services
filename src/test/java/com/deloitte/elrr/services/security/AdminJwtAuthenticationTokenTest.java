package com.deloitte.elrr.services.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
class AdminJwtAuthenticationTokenTest {

    private AdminJwtAuthenticationToken adminJwtAuthToken;
    private List<SystemAuthority> authorities;
    private String testToken;
    private DecodedJWT jwt;

    @BeforeEach
    void setUp() {
        // Create a JWT token with admin role
        JwtUtil jwtUtil = new JwtUtil("test-secret");
        // Set the admin user ID key for the JWT
        // This is necessary because the @Value annotation in
        // AdminJwtAuthenticationToken expects a property that
        // is not set properly.
        // TODO remove this when no longer needed
        ReflectionTestUtils.setField(jwtUtil, "adminUserIdKey",
            "preferred_username");
        testToken = jwtUtil.createAdminToken("external-secret");
        // decode rather than verify
        jwt = jwtUtil.decodeToken(testToken);

        // Set up authentication token
        SystemAuthority authority = new SystemAuthority(
                SystemAuthority.SystemRole.ROLE_ADMIN);
        authorities = Collections.singletonList(authority);
        adminJwtAuthToken = new AdminJwtAuthenticationToken(authorities, jwt,
                "preferred_username");
    }

    @Test
    void testGetCredentials() {
        // Act
        String credentials = (String) adminJwtAuthToken.getCredentials();

        // Assert
        assertNotNull(credentials);
        assertEquals(testToken, credentials);
    }

    @Test
    void testGetPrincipal() {
        // Act
        Object principal = adminJwtAuthToken.getPrincipal();

        // Assert
        assertNotNull(principal);
        assertEquals("admin-user", principal);
    }

    @Test
    void testGetClaim() {
        // Act
        Object claim = adminJwtAuthToken.getClaim("elrr_permissions");

        // Assert
        assertNotNull(claim);
    }

    @Test
    void testIsAuthenticated() {
        // Act & Assert
        assertTrue(adminJwtAuthToken.isAuthenticated());
    }
}
