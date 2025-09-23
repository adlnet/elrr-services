package com.deloitte.elrr.services.security;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.deloitte.elrr.services.dto.PermissionDto;
import com.deloitte.elrr.entity.types.ActionType;

@Slf4j
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Value("${client.admin-api-override}")
    private boolean adminApiOverride;

    @Autowired
    private SecurityActionContext securityActionContext;

    @Override
    public boolean hasPermission(Authentication authentication, Object resource,
            Object action) {
        UUID jwtId;
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken token =
                (JwtAuthenticationToken) authentication;
            jwtId = token.getJwtId();
        } else {
            jwtId = null;
        }
        // Store data in request-scoped context for aspects
        securityActionContext.setCurrentContext((String) action,
                (String) resource, jwtId);

        if (authentication instanceof AdminJwtAuthenticationToken)
            return adminApiOverride;

        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;

        // get permissions from token
        List<PermissionDto> permissions = token.getPermissions();
        // if at least one permission matches the resource and its actions
        // include the action, return true, otherwise return false
        if (permissions != null && !permissions.isEmpty()) {
            return permissions.stream()
                    .anyMatch(permission -> (permission.getResource()
                            .equals((String) resource)
                            || permission.getResource().equals("*"))
                            && permission.getActions()
                                .contains(ActionType.valueOf((String) action)));
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication,
            Serializable targetId, String targetType, Object permission) {
        throw new UnsupportedOperationException(
                "hasPermission with targetId and targetType is not supported");
    }
}
