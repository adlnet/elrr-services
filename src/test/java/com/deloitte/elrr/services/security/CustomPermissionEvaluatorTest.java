package com.deloitte.elrr.services.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.services.dto.PermissionDto;
import com.deloitte.elrr.services.model.Action;

@ExtendWith(MockitoExtension.class)
class CustomPermissionEvaluatorTest {

    private CustomPermissionEvaluator permissionEvaluator;

    @Mock
    private JwtAuthenticationToken mockToken;

    @Mock
    private AdminJwtAuthenticationToken mockAdminToken;

    @BeforeEach
    void setUp() {
        permissionEvaluator = new CustomPermissionEvaluator();
    }

    @Test
    void hasPermission_WithValidPermission_ReturnsTrue() {
        // Arrange
        PermissionDto permission = new PermissionDto("users", null, Arrays.asList(Action.READ));
        when(mockToken.getPermissions()).thenReturn(Arrays.asList(permission));

        // Act
        boolean result = permissionEvaluator.hasPermission(
            mockToken, "users", "READ");

        // Assert
        assertTrue(result);
    }

    @Test
    void hasPermission_WithInvalidResource_ReturnsFalse() {
        // Arrange
        PermissionDto permission = new PermissionDto("users", null, Arrays.asList(Action.READ));
        when(mockToken.getPermissions()).thenReturn(Arrays.asList(permission));

        // Act
        boolean result = permissionEvaluator.hasPermission(
            mockToken, "courses", "READ");

        // Assert
        assertFalse(result);
    }

    @Test
    void hasPermission_WithNullPermissions_ReturnsFalse() {
        // Arrange
        when(mockToken.getPermissions()).thenReturn(null);

        // Act
        boolean result = permissionEvaluator.hasPermission(
            mockToken, "users", "READ");

        // Assert
        assertFalse(result);
    }

    @Test
    void hasPermission_WithEmptyPermissions_ReturnsFalse() {
        // Arrange
        when(mockToken.getPermissions()).thenReturn(Collections.emptyList());

        // Act
        boolean result = permissionEvaluator.hasPermission(
            mockToken, "users", "READ");

        // Assert
        assertFalse(result);
    }

    @Test
    void hasPermission_WithWildcardResource_ReturnsTrue() {
        // Arrange
        PermissionDto permission = new PermissionDto("*", null, Arrays.asList(Action.READ));
        when(mockToken.getPermissions()).thenReturn(Arrays.asList(permission));

        // Act
        boolean result = permissionEvaluator.hasPermission(
            mockToken, "anyResource", "READ");

        // Assert
        assertTrue(result);
    }

    @Test
    void adminToken_WithOverrideFalse_ReturnsFalse() {

        // Act
        boolean result = permissionEvaluator.hasPermission(
            mockAdminToken, "anyResource", "READ");

        // Assert
        assertFalse(result);
    }

    @Test
    void adminToken_WithOverrideTrue_ReturnsTrue()
        throws NoSuchFieldException, SecurityException,
        IllegalArgumentException, IllegalAccessException {

        Field field = CustomPermissionEvaluator.class
            .getDeclaredField("adminApiOverride");
        field.setAccessible(true);
        field.set(permissionEvaluator, true);

        // Act
        boolean result = permissionEvaluator.hasPermission(
            mockAdminToken, "anyResource", "READ");

        // Assert
        assertTrue(result);
    }
}
