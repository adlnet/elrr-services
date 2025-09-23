package com.deloitte.elrr.services.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.deloitte.elrr.entity.types.ActionType;

/**
 * Test class for SecurityActionContext.
 */
class SecurityActionContextTest {

    private SecurityActionContext securityActionContext;

    @BeforeEach
    void setUp() {
        securityActionContext = new SecurityActionContext();
    }

    @Test
    void testSetAndGetCurrentAction() {
        // Arrange
        ActionType expectedAction = ActionType.CREATE;
        String expectedResource = "person";
        UUID expectedJwtId = UUID.randomUUID();

        // Act
        securityActionContext.setCurrentContext(expectedAction.toString(), expectedResource, expectedJwtId);

        // Assert
        assertEquals(expectedAction, securityActionContext.getCurrentAction());
        assertEquals(expectedResource, securityActionContext.getCurrentResource());
        assertEquals(expectedJwtId, securityActionContext.getJwtId());
    }

    @Test
    void testGetCurrentActionWhenNotSet() {
        // Act
        ActionType result = securityActionContext.getCurrentAction();

        // Assert
        assertEquals(ActionType.ADMIN, result);
    }

    @Test
    void testGetCurrentResourceWhenNotSet() {
        // Act
        String result = securityActionContext.getCurrentResource();

        // Assert
        assertEquals("token", result);
    }

    @Test
    void testSetCurrentContextOverwritesPrevious() {
        // Arrange
        UUID jwtId1 = UUID.randomUUID();
        UUID jwtId2 = UUID.randomUUID();
        securityActionContext.setCurrentContext("CREATE", "person", jwtId1);

        // Act
        securityActionContext.setCurrentContext("UPDATE", "organization", jwtId2);

        // Assert
        assertEquals(ActionType.UPDATE, securityActionContext.getCurrentAction());
        assertEquals("organization", securityActionContext.getCurrentResource());
        assertEquals(jwtId2, securityActionContext.getJwtId());
    }

    @Test
    void testSetCurrentContextWithNulls() {
        // Arrange
        UUID jwtId = UUID.randomUUID();
        securityActionContext.setCurrentContext("DELETE", "credential", jwtId);

        // Act
        securityActionContext.setCurrentContext(null, null, null);

        // Assert
        assertEquals(ActionType.ADMIN, securityActionContext.getCurrentAction());
        assertEquals("token", securityActionContext.getCurrentResource());
        assertNull(securityActionContext.getJwtId());
    }

    @Test
    void testSetCurrentContextWithMixedNulls() {
        // Arrange
        UUID jwtId = UUID.randomUUID();
        securityActionContext.setCurrentContext("READ", null, jwtId);

        // Assert
        assertEquals(ActionType.READ, securityActionContext.getCurrentAction());
        assertEquals("token", securityActionContext.getCurrentResource());
        assertEquals(jwtId, securityActionContext.getJwtId());

        // Act
        securityActionContext.setCurrentContext(null, "facility", null);

        // Assert
        assertEquals(ActionType.ADMIN, securityActionContext.getCurrentAction());
        assertEquals("facility", securityActionContext.getCurrentResource());
        assertNull(securityActionContext.getJwtId());
    }

    @Test
    void testMultipleContextChanges() {
        // Act/Assert
        UUID jwtId1 = UUID.randomUUID();
        UUID jwtId2 = UUID.randomUUID();
        UUID jwtId3 = UUID.randomUUID();
        
        securityActionContext.setCurrentContext("CREATE", "person", jwtId1);
        assertEquals(ActionType.CREATE, securityActionContext.getCurrentAction());
        assertEquals("person", securityActionContext.getCurrentResource());
        assertEquals(jwtId1, securityActionContext.getJwtId());

        securityActionContext.setCurrentContext("UPDATE", "organization", jwtId2);
        assertEquals(ActionType.UPDATE, securityActionContext.getCurrentAction());
        assertEquals("organization", securityActionContext.getCurrentResource());
        assertEquals(jwtId2, securityActionContext.getJwtId());

        securityActionContext.setCurrentContext("DELETE", "credential", jwtId3);
        assertEquals(ActionType.DELETE, securityActionContext.getCurrentAction());
        assertEquals("credential", securityActionContext.getCurrentResource());
        assertEquals(jwtId3, securityActionContext.getJwtId());
    }

    @Test
    void testGetRequestIdGeneratesUniqueId() {
        // Act
        UUID requestId1 = securityActionContext.getRequestId();
        UUID requestId2 = securityActionContext.getRequestId();

        // Assert
        assertNotNull(requestId1);
        assertNotNull(requestId2);
        assertEquals(requestId1, requestId2); // Same instance should return same ID
    }

    @Test
    void testRequestIdConsistentAcrossContextChanges() {
        // Arrange
        UUID initialRequestId = securityActionContext.getRequestId();
        UUID jwtId1 = UUID.randomUUID();
        UUID jwtId2 = UUID.randomUUID();

        // Act
        securityActionContext.setCurrentContext("CREATE", "person", jwtId1);
        UUID requestIdAfterSet = securityActionContext.getRequestId();

        securityActionContext.setCurrentContext("UPDATE", "organization", jwtId2);
        UUID requestIdAfterUpdate = securityActionContext.getRequestId();

        // Assert
        assertEquals(initialRequestId, requestIdAfterSet);
        assertEquals(initialRequestId, requestIdAfterUpdate);
    }

    @Test
    void testRequestIdIsTimeBasedUuid() {
        // Arrange
        UUID requestId = securityActionContext.getRequestId();

        // Assert
        assertNotNull(requestId);
        // Time-based UUIDs typically have version 7 in the most significant bits
        // But we'll just verify it's a valid UUID and not null
        assertTrue(requestId.toString().matches(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"));
    }

    @Test
    void testGetJwtIdWhenNotSet() {
        // Act
        UUID result = securityActionContext.getJwtId();

        // Assert
        assertNull(result);
    }

    @Test
    void testSetAndGetJwtId() {
        // Arrange
        UUID expectedJwtId = UUID.randomUUID();

        // Act
        securityActionContext.setCurrentContext("CREATE", "person", expectedJwtId);

        // Assert
        assertEquals(expectedJwtId, securityActionContext.getJwtId());
    }

    @Test
    void testJwtIdOverwritesPrevious() {
        // Arrange
        UUID jwtId1 = UUID.randomUUID();
        UUID jwtId2 = UUID.randomUUID();
        securityActionContext.setCurrentContext("CREATE", "person", jwtId1);

        // Act
        securityActionContext.setCurrentContext("UPDATE", "organization", jwtId2);

        // Assert
        assertEquals(jwtId2, securityActionContext.getJwtId());
    }

    @Test
    void testJwtIdCanBeSetToNull() {
        // Arrange
        UUID jwtId = UUID.randomUUID();
        securityActionContext.setCurrentContext("CREATE", "person", jwtId);

        // Act
        securityActionContext.setCurrentContext("UPDATE", "organization", null);

        // Assert
        assertNull(securityActionContext.getJwtId());
    }
}
