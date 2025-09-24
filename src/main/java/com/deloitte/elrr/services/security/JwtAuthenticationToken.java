package com.deloitte.elrr.services.security;

import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import com.auth0.jwt.interfaces.DecodedJWT;

import com.deloitte.elrr.services.dto.PermissionDto;

/**
 * Implementation of AbstractAuthenticationToken Specifically for carrying JWT
 * details with security context.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String userIdKey;
    private DecodedJWT jwt;

    /**
     * Construct Auth Token. Will set authenticated to true.
     *
     * @param auths Granted Authorities
     * @param jwt Decoded JWT
     * @param userIdKey Key to retrieve user ID from JWT
     */
    public JwtAuthenticationToken(List<SystemAuthority> auths, DecodedJWT jwt,
            String userIdKey) {
        super(auths);
        super.setAuthenticated(true);
        this.jwt = jwt;
        this.userIdKey = userIdKey;
    }

    @Override
    public Object getCredentials() {
        return jwt.getToken();
    }

    @Override
    public Object getPrincipal() {
        return jwt.getClaim(userIdKey).asString();
    }

    /**
     * @param key
     * @return Claim value if key exists in token
     */
    public Object getClaim(String key) {
        return jwt.getClaim(key);
    }

    /**
     * @return List<PermissionDto> permissions
     */
    public List<PermissionDto> getPermissions() {
        return jwt.getClaim("elrr_permissions").asList(PermissionDto.class);
    }
}
