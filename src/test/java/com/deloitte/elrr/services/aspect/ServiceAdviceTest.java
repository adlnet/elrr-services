package com.deloitte.elrr.services.aspect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.deloitte.elrr.entity.AuditLog;
import com.deloitte.elrr.entity.Auditable;
import com.deloitte.elrr.entity.Entity;
import com.deloitte.elrr.entity.types.ActionType;
import com.deloitte.elrr.jpa.svc.AuditLogSvc;
import com.deloitte.elrr.services.security.SecurityActionContext;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServiceAdviceTest {

    @InjectMocks
    private ServiceAdvice serviceAdvice;

    @Mock
    private SecurityActionContext securityActionContext;

    @Mock
    private AuditLogSvc auditLogSvc;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @BeforeEach
    void setUp() {
        securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext)
                .thenReturn(securityContext);
        
        // Setup default mock behavior for SecurityActionContext
        Mockito.lenient().when(securityActionContext.getCurrentAction()).thenReturn(ActionType.ADMIN);
        Mockito.lenient().when(securityActionContext.getCurrentResource()).thenReturn("test_resource");
        Mockito.lenient().when(securityActionContext.getRequestId()).thenReturn(UUID.randomUUID());

        // Setup default mock behavior for AuditLogSvc to prevent NPE
        AuditLog mockAuditLog = new AuditLog();
        mockAuditLog.setId(UUID.randomUUID());
        Mockito.lenient().when(auditLogSvc.save(Mockito.any(AuditLog.class))).thenReturn(mockAuditLog);
    }

    @AfterEach
    void tearDown() {
        if (securityContextHolderMock != null) {
            securityContextHolderMock.close();
        }
    }

    @Test
    void aroundSave_ShouldLogEntity() throws Throwable {
        // Setup authentication for logging
        Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.lenient().when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.lenient().when(authentication.getPrincipal()).thenReturn("testuser");
        
        // Setup security action context
        Mockito.lenient().when(securityActionContext.getCurrentAction()).thenReturn(ActionType.CREATE);
        Mockito.lenient().when(securityActionContext.getCurrentResource()).thenReturn("person");
        
        // Arrange
        TestAuditableEntity inputEntity = new TestAuditableEntity();
        inputEntity.setId(UUID.randomUUID());
        
        TestEntity outputEntity = new TestEntity();
        outputEntity.setId(UUID.randomUUID());

        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{inputEntity});
        when(proceedingJoinPoint.proceed()).thenReturn(outputEntity);

        // Act
        Entity result = serviceAdvice.aroundSave(proceedingJoinPoint);

        // Assert
        assertNotNull(result);
        assertEquals(outputEntity, result);
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void aroundSaveAll_ShouldLogAllEntities() throws Throwable {
        // Setup authentication for logging
        Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.lenient().when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.lenient().when(authentication.getPrincipal()).thenReturn("testuser");
        
        // Arrange
        TestAuditableEntity inputEntity1 = new TestAuditableEntity();
        inputEntity1.setId(UUID.randomUUID());
        TestAuditableEntity inputEntity2 = new TestAuditableEntity();
        inputEntity2.setId(UUID.randomUUID());
        Collection<Auditable> inputEntities = List.of(inputEntity1, inputEntity2);

        TestEntity outputEntity1 = new TestEntity();
        outputEntity1.setId(UUID.randomUUID());
        TestEntity outputEntity2 = new TestEntity();
        outputEntity2.setId(UUID.randomUUID());
        Collection<Entity> outputEntities = List.of(outputEntity1, outputEntity2);

        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{inputEntities});
        when(proceedingJoinPoint.proceed()).thenReturn(outputEntities);

        // Act
        Collection<Entity> result = serviceAdvice.aroundSaveAll(proceedingJoinPoint);

        // Assert
        assertNotNull(result);
        assertEquals(outputEntities, result);
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void aroundSaveAll_ShouldHandleExceptionInLogging() throws Throwable {
        // Setup authentication for logging (even though it may fail)
        Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.lenient().when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.lenient().when(authentication.getPrincipal()).thenReturn("testuser");
        
        // Arrange
        TestAuditableEntity inputEntity = new TestAuditableEntity();
        inputEntity.setId(UUID.randomUUID());
        Collection<Auditable> inputEntities = List.of(inputEntity);

        // Create a problematic entity that will cause logging to fail
        ProblematicEntity problematicEntity = new ProblematicEntity();
        problematicEntity.setId(UUID.randomUUID());
        Collection<Entity> outputEntities = List.of(problematicEntity);

        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{inputEntities});
        when(proceedingJoinPoint.proceed()).thenReturn(outputEntities);

        // Act
        Collection<Entity> result = serviceAdvice.aroundSaveAll(proceedingJoinPoint);

        // Assert
        assertNotNull(result);
        assertEquals(outputEntities, result);
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void aroundSave_ShouldLogEvenWhenNoAuthentication() throws Throwable {
        // Setup no authentication scenario
        Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(null);
        
        // Setup security action context
        Mockito.lenient().when(securityActionContext.getCurrentAction()).thenReturn(ActionType.UPDATE);
        Mockito.lenient().when(securityActionContext.getCurrentResource()).thenReturn("organization");
        
        TestAuditableEntity inputEntity = new TestAuditableEntity();
        inputEntity.setId(UUID.randomUUID());
        
        TestEntity outputEntity = new TestEntity();
        outputEntity.setId(UUID.randomUUID());

        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{inputEntity});
        when(proceedingJoinPoint.proceed()).thenReturn(outputEntity);

        // Act
        Entity result = serviceAdvice.aroundSave(proceedingJoinPoint);

        // Assert
        assertNotNull(result);
        assertEquals(outputEntity, result);
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void aroundSaveAll_ShouldLogEvenWhenNoAuthentication() throws Throwable {
        // Setup no authentication scenario
        Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(null);
        
        TestAuditableEntity inputEntity = new TestAuditableEntity();
        inputEntity.setId(UUID.randomUUID());
        Collection<Auditable> inputEntities = List.of(inputEntity);
        
        TestEntity outputEntity = new TestEntity();
        outputEntity.setId(UUID.randomUUID());
        Collection<Entity> outputEntities = List.of(outputEntity);

        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{inputEntities});
        when(proceedingJoinPoint.proceed()).thenReturn(outputEntities);

        // Act
        Collection<Entity> result = serviceAdvice.aroundSaveAll(proceedingJoinPoint);

        // Assert
        assertNotNull(result);
        assertEquals(outputEntities, result);
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void aroundSave_ShouldPropagateExceptionFromProceed() throws Throwable {
        // Arrange - no authentication setup needed since exception is thrown before logging
        TestAuditableEntity inputEntity = new TestAuditableEntity();
        inputEntity.setId(UUID.randomUUID());
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{inputEntity});
        
        RuntimeException expectedException = new RuntimeException("Test exception");
        when(proceedingJoinPoint.proceed()).thenThrow(expectedException);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> serviceAdvice.aroundSave(proceedingJoinPoint));
        
        assertEquals("Test exception", exception.getMessage());
    }

    @Test
    void aroundSaveAll_ShouldPropagateExceptionFromProceed() throws Throwable {
        // Arrange - no authentication setup needed since exception is thrown before logging
        TestAuditableEntity inputEntity = new TestAuditableEntity();
        inputEntity.setId(UUID.randomUUID());
        Collection<Auditable> inputEntities = List.of(inputEntity);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{inputEntities});
        
        RuntimeException expectedException = new RuntimeException("Test exception");
        when(proceedingJoinPoint.proceed()).thenThrow(expectedException);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> serviceAdvice.aroundSaveAll(proceedingJoinPoint));
        
        assertEquals("Test exception", exception.getMessage());
    }

    @Test
    void aroundDelete_ShouldLogDeletion() throws Throwable {
        // Setup authentication for logging
        Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.lenient().when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.lenient().when(authentication.getPrincipal()).thenReturn("testuser");
        
        // Setup security action context
        Mockito.lenient().when(securityActionContext.getCurrentAction()).thenReturn(ActionType.DELETE);
        Mockito.lenient().when(securityActionContext.getCurrentResource()).thenReturn("person");
        
        // Arrange
        UUID entityId = UUID.randomUUID();
        Object mockTarget = new Object(); // Simple mock target for the service
        
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{entityId});
        when(proceedingJoinPoint.getTarget()).thenReturn(mockTarget);
        when(proceedingJoinPoint.proceed()).thenReturn(null); // void method

        // Act
        serviceAdvice.aroundDelete(proceedingJoinPoint);

        // Assert
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void aroundDelete_ShouldHandleNullArgs() throws Throwable {
        // Setup authentication for logging
        Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.lenient().when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.lenient().when(authentication.getPrincipal()).thenReturn("testuser");
        
        // Arrange
        when(proceedingJoinPoint.getArgs()).thenReturn(null);
        when(proceedingJoinPoint.proceed()).thenReturn(null); // void method

        // Act
        serviceAdvice.aroundDelete(proceedingJoinPoint);

        // Assert
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void aroundDelete_ShouldPropagateExceptionFromProceed() throws Throwable {
        // Arrange
        UUID entityId = UUID.randomUUID();
        Object mockTarget = new Object();
        
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{entityId});
        when(proceedingJoinPoint.getTarget()).thenReturn(mockTarget);
        
        RuntimeException expectedException = new RuntimeException("Delete exception");
        when(proceedingJoinPoint.proceed()).thenThrow(expectedException);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> serviceAdvice.aroundDelete(proceedingJoinPoint));
        
        assertEquals("Delete exception", exception.getMessage());
    }

    // Test helper classes
    private static class TestAuditableEntity extends Auditable {
        // Test implementation of Auditable
    }

    private static class TestEntity extends Entity {
        // Test implementation of Entity
    }

    private static class ProblematicEntity extends Entity {
        // This entity will cause issues during JSON serialization
        
        @Override
        public String toString() {
            throw new RuntimeException("Serialization error");
        }
    }
}
