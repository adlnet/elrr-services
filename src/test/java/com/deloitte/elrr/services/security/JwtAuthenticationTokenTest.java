package com.deloitte.elrr.services.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.deloitte.elrr.services.dto.PermissionDto;
import com.deloitte.elrr.entity.types.ActionType;

import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

class JwtAuthenticationTokenTest {

    private JwtAuthenticationToken jwtAuthToken;
    private List<SystemAuthority> authorities;
    private String testToken;
    private DecodedJWT jwt;
    private List<PermissionDto> permissions = List.of(
            new PermissionDto("resource1", null,
                    List.of(ActionType.READ, ActionType.UPDATE)),
            new PermissionDto("resource2", null,
                    List.of(ActionType.CREATE, ActionType.DELETE)));

    @BeforeEach
    void setUp() {
        // Mock Authentication and SecurityContext
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("api-user");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Create a JWT token with admin role
        JwtUtil jwtUtil = new JwtUtil("test-secret");
        ReflectionTestUtils.setField(jwtUtil, "apiUserIdKey",
                "token-creator");
        UUID testTokenId = UUID.randomUUID();
        testToken = jwtUtil.createToken(testTokenId, permissions);
        jwt = jwtUtil.verify(testToken);

        // Set up authentication token
        SystemAuthority authority = new SystemAuthority(
                SystemAuthority.SystemRole.ROLE_ADMIN);
        authorities = Collections.singletonList(authority);
        jwtAuthToken = new JwtAuthenticationToken(authorities, jwt,
                "token-creator");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCredentials() {
        // Act
        String credentials = (String) jwtAuthToken.getCredentials();

        // Assert
        assertNotNull(credentials);
        assertEquals(testToken, credentials);
    }

    @Test
    void testGetPrincipal() {
        // Act
        Object principal = jwtAuthToken.getPrincipal();

        // Assert
        assertNotNull(principal);
        assertEquals("api-user", principal);
    }

    @Test
    void testGetPermissionsClaim() {
        // Act
        Claim permissionsClaim = (Claim) jwtAuthToken
                .getClaim("elrr_permissions");
        List<PermissionDto> tokenPermissions = permissionsClaim
                .asList(PermissionDto.class);

        // Assert
        assertNotNull(tokenPermissions);
        assertEquals(permissions.size(), tokenPermissions.size());
        assertTrue(tokenPermissions.containsAll(permissions));
    }

    @Test
    void testGetPermission() {
        // Act
        List<PermissionDto> tokenPermissions = jwtAuthToken.getPermissions();

        // Assert
        assertNotNull(tokenPermissions);
        assertEquals(permissions.size(), tokenPermissions.size());
        assertTrue(tokenPermissions.containsAll(permissions));
    }

    @Test
    void testIsAuthenticated() {
        // Act & Assert
        assertTrue(jwtAuthToken.isAuthenticated());
    }

    @Test
    void testGetJwtId() {
        // Act
        UUID jwtId = jwtAuthToken.getJwtId();

        // Assert
        assertNotNull(jwtId);
        // The JWT ID should match what's in the token
        assertEquals(jwt.getId(), jwtId.toString());
    }
}
