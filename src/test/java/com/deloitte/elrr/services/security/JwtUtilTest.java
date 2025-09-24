package com.deloitte.elrr.services.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.deloitte.elrr.services.dto.PermissionDto;
import com.deloitte.elrr.services.model.Action;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("test-secret");
        // set the apiUserIdKey for the JWT
        ReflectionTestUtils.setField(jwtUtil, "apiUserIdKey",
                "token-creator");
    }

    @Test
    void testPermissionsSerializationAndDeserialization() {
        // Arrange
        PermissionDto permission1 = new PermissionDto("resource1", null,
                Arrays.asList(Action.CREATE, Action.READ));
        PermissionDto permission2 = new PermissionDto("resource2", null,
                Arrays.asList(Action.UPDATE, Action.DELETE));
        List<PermissionDto> permissions = Arrays.asList(permission1,
                permission2);
        UUID testTokenId = UUID.randomUUID();

        // Act
        String token = jwtUtil.createToken(testTokenId, permissions);
        DecodedJWT decodedJWT = jwtUtil.decodeToken(token);
        List<?> permissionsClaim = decodedJWT.getClaim("elrr_permissions")
                .asList(Object.class);

        // Assert
        assertNotNull(token);
        assertNotNull(decodedJWT);
        assertNotNull(permissionsClaim);
        
        // Verify that the UUID is present in the JWT payload as the 'jti' claim
        assertEquals(testTokenId.toString(), decodedJWT.getId());
        
        assertTrue(permissionsClaim.stream().anyMatch(
                map -> "resource1".equals(((Map<?, ?>) map).get("resource"))));
        assertTrue(permissionsClaim.stream()
                .anyMatch(map -> ((List<?>) ((Map<?, ?>) map).get("actions"))
                        .contains("CREATE")));
        assertTrue(permissionsClaim.stream().anyMatch(
                map -> "resource2".equals(((Map<?, ?>) map).get("resource"))));
        assertTrue(permissionsClaim.stream()
                .anyMatch(map -> ((List<?>) ((Map<?, ?>) map).get("actions"))
                        .contains("DELETE")));
    }
}
