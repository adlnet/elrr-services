package com.deloitte.elrr.services.security;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.deloitte.elrr.entity.types.ActionType;
import com.fasterxml.uuid.Generators;

/**
 * Request-scoped bean for storing the current security action and resource
 * being evaluated. This allows aspects and other components to access
 * the action and resource that triggered a permission check within the same
 * request. Also provides a unique request ID for grouping audit log entries.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST,
        proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SecurityActionContext {

    private ActionType currentAction;
    private String currentResource;
    private UUID requestId;
    private UUID jwtId;

    /**
     * Set the current action and resource for this request.
     *
     * @param action the action being performed
     * @param resource the resource being accessed
     * @param tokenId the JWT ID for this request
     */
    public void setCurrentContext(
        String action,
        String resource,
        UUID tokenId) {
        if (action != null) {
            this.currentAction = ActionType.valueOf(action);
        } else {
            this.currentAction = ActionType.ADMIN;
        }
        this.currentResource = resource;
        this.jwtId = tokenId;
    }

    /**
     * Get the current action for this request.
     *
     * @return the current action, or null if none is set
     */
    public ActionType getCurrentAction() {
        if (this.currentAction == null) {
            this.currentAction = ActionType.ADMIN;
        }
        return this.currentAction;
    }

    /**
     * Get the current resource for this request.
     *
     * @return the current resource, or null if none is set
     */
    public String getCurrentResource() {
        if (this.currentResource == null) {
            // token retrieval is the only resource not specified
            this.currentResource = "token";
        }
        return this.currentResource;
    }

    /**
     * Get the unique request ID for this request. This ID is generated
     * the first time it's accessed and remains consistent for the duration
     * of the request.
     *
     * @return the unique request ID
     */
    public UUID getRequestId() {
        if (this.requestId == null) {
            this.requestId = Generators.timeBasedEpochRandomGenerator()
                    .generate();
        }
        return this.requestId;
    }
    /**
     * Get the JWT ID for this request.
     *
     * @return the JWT ID
     */
    public UUID getJwtId() {
        return this.jwtId;
    }

}
