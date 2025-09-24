package com.deloitte.elrr.services.security;

import org.springframework.security.core.GrantedAuthority;

public class SystemAuthority implements GrantedAuthority {

    private SystemRole systemRole;

    /**
     *
     * @param systemRole
     */
    public SystemAuthority(SystemRole systemRole) {
        this.systemRole = systemRole;
    }

    @Override
    public String getAuthority() {
        return systemRole.name();
    }

    public enum SystemRole {
        ROLE_ADMIN, ROLE_API
    }

}
