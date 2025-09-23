package com.deloitte.elrr.services.aspect;

import java.util.UUID;
import java.util.Collection;

import java.time.ZonedDateTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.entity.Entity;
import com.deloitte.elrr.services.security.JwtAuthenticationToken;
import com.deloitte.elrr.services.security.SecurityActionContext;
import com.deloitte.elrr.entity.AuditLog;
import com.deloitte.elrr.entity.types.SvcMethod;
import com.deloitte.elrr.jpa.svc.AuditLogSvc;
import com.deloitte.elrr.entity.types.ActionType;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class ServiceAdvice {

    @Autowired
    private SecurityActionContext securityActionContext;

    @Autowired
    private AuditLogSvc auditLogSvc;

    /**
     * Intercept Service Save calls and log the change.
     * Excludes AuditLogSvc to prevent infinite loops.
     *
     * @param pjp
     * @return
     * @throws Throwable
     * @return Object being returned
     */
    @Transactional
    @Around(value = "execution(* com.deloitte.elrr.jpa.svc.*.save(..))"
            + " && !execution(* com.deloitte.elrr.jpa.svc.AuditLogSvc.*(..))")
    public Entity aroundSave(ProceedingJoinPoint pjp) throws Throwable {
        // perform operation
        Entity output = (Entity) pjp.proceed();
        // write audit log
        writeAuditLog(
            output.getId(),
            output.getClass().getSimpleName(),
            SvcMethod.SAVE
            );

        return output;
    }

    /**
     * Intercept Service SaveAll calls and log the changes.
     * Excludes AuditLogSvc to prevent infinite loops.
     *
     * @param pjp
     * @return
     * @throws Throwable
     * @return Collection of entities being returned
     */
    @SuppressWarnings("unchecked")
    @Transactional
    @Around(value = "execution(* com.deloitte.elrr.jpa.svc.*.saveAll(..))"
            + " && !execution(* com.deloitte.elrr.jpa.svc.AuditLogSvc.*(..))")
    public Collection<Entity> aroundSaveAll(ProceedingJoinPoint pjp)
            throws Throwable {
        // perform operation
        Collection<Entity> outputs = (Collection<Entity>) pjp.proceed();
        // write audit log
        outputs.forEach(output -> {
            writeAuditLog(
                output.getId(),
                output.getClass().getSimpleName(),
                SvcMethod.SAVE
            );
        });

        return outputs;
    }

    /**
     * Intercept Service Delete calls and log the deletion.
     * Excludes AuditLogSvc to prevent infinite loops.
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Transactional
    @Around(value = "execution(* com.deloitte.elrr.jpa.svc.*.delete(..))"
            + " && !execution(* com.deloitte.elrr.jpa.svc.AuditLogSvc.*(..))")
    public void aroundDelete(ProceedingJoinPoint pjp) throws Throwable {
        // get the ID being deleted before the operation
        Object[] args = pjp.getArgs();
        if (args != null && args.length > 0) {
            Object id = args[0];
            String serviceClassName = pjp.getTarget().getClass()
                    .getSimpleName();
            // write audit log
            writeAuditLog(
                (UUID) id,
                serviceClassName,
                SvcMethod.DELETE
            );
        }

        // perform operation
        pjp.proceed();
    }

    /**
     * Get username from SecurityContext, returning "unknown" if not available.
     *
     * @return the username or "unknown"
     */
    private static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "unknown";
        }
        return authentication.getPrincipal().toString();
    }

    /**
     * Determine whether or not the current authentication comes from the API.
     *
     * @return true if the current authentication is from the API,
     *         false otherwise
     */
    private static boolean isApiUser() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication instanceof JwtAuthenticationToken;
    }

    /**
     * Write entity information to the audit log.
     *
     * @param entityId the ID of the entity being logged
     * @param entityType the type of the entity being logged
     * @param svcMethod the service method being logged
     */
    private void writeAuditLog(
        UUID entityId,
        String entityType,
        SvcMethod svcMethod
        ) {
        String username = getCurrentUsername();
        ActionType action = securityActionContext.getCurrentAction();
        String resource = securityActionContext.getCurrentResource();
        UUID requestId = securityActionContext.getRequestId();
        UUID jwtId = securityActionContext.getJwtId();
        Boolean isApiUser = isApiUser();

        AuditLog auditLog = new AuditLog();
        auditLog.setTimestamp(ZonedDateTime.now());
        auditLog.setEntityId(entityId);
        auditLog.setEntityType(entityType);
        auditLog.setUsername(username);
        auditLog.setAction(action);
        auditLog.setResource(resource);
        auditLog.setRequestId(requestId);
        auditLog.setIsApiUser(isApiUser);
        auditLog.setSvcMethod(svcMethod);
        auditLog.setJwtId(jwtId);

        try {
            auditLogSvc.save(auditLog);
            log.debug("Audit log entry created: {}", auditLog);
        } catch (Throwable e) {
            log.error("Error logging entity info", e);
        }
    }

}
