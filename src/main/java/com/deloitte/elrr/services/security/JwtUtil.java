package com.deloitte.elrr.services.security;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.deloitte.elrr.services.dto.PermissionDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

    @Value("${client.jwt.secret}")
    private String secret;

    @Value("${admin.jwt.role}")
    private String adminRole;

    @Value("${admin.jwt.role-key}")
    private String adminRoleKey;

    @Value("${admin.jwt.issuer-whitelist}")
    private String adminIssuerWhitelist;

    @Value("${admin.jwt.user-id-key}")
    private String adminUserIdKey;

    @Value("${api.jwt.user-id-key}")
    private String apiUserIdKey;

    @Value("${api.jwt.issuer}")
    private String apiIssuer;

    private Algorithm algorithm;

    /**
     * No-arg constructor for JwtUtil.
     */
    public JwtUtil() {
    }

    /**
     * Manual Constructor for unit tests compatibility.
     * @param secret injected secret param
     */
    public JwtUtil(String secret) {
        this.secret = secret;
    }

    /**
     * Get the role configured for admin users.
     * @return the role configured for admin users
     */
    public String getAdminRole() {
        return adminRole;
    }
    /**
     * Get the role key configured for admin users.
     * @return the role key configured for admin users
     */
    public String getAdminRoleKey() {
        return adminRoleKey;
    }
    /**
     * Get the issuer whitelist configured for admin users.
     * @return the issuer whitelist configured for admin users
     */
    public List<String> getAdminIssuerWhitelist() {
        return List.of(adminIssuerWhitelist.split(","));
    }

    /**
     * Get the admin user ID key configured for admin users.
     * @return the admin user ID key configured for admin users
     */
    public String getAdminUserIdKey() {
        return adminUserIdKey;
    }

    /**
     * Get the API user ID key configured for API users.
     * @return the API user ID key configured for API users
     */
    public String getApiUserIdKey() {
        return apiUserIdKey;
    }

    /**
     * Get the API issuer configured for API users.
     * @return the API issuer configured for API users
     */
    public String getApiIssuer() {
        return apiIssuer;
    }

    /**
     *
     * @param jwt
     * @return If it is a jwt token or not
     */
    public DecodedJWT decodeToken(String jwt) {
        DecodedJWT djwt = JWT.decode(jwt);
        return djwt;
    }

    /**
     * @param jwt
     * @return If it is valid or not based on local secret
     */
    public DecodedJWT verify(String jwt)
            throws AlgorithmMismatchException, SignatureVerificationException {
        JWTVerifier verifier = JWT.require(getAlgorithm())
            .withIssuer(apiIssuer)
            .build();
        return verifier.verify(jwt);
    }

    /**
     * Create a new admin token for testing.
     * @param seekrit secret to use for signing
     * @return JWT Token String with ROLE_ADMIN claim
     */
    public String createAdminToken(String seekrit) {
        return JWT.create()
            .withIssuer("http://example.com")
            .withIssuedAt(new Date())
            .withClaim(adminUserIdKey, "admin-user")
            .withClaim("group-simple", Collections.singletonList("elrr-admin"))
            .sign(Algorithm.HMAC512(seekrit));
    }

    /**
     * Create a new Client Token with permissions.
     * @param tokenId Entity identifier for the token
     * @param permissions List of permissions to be added as a claim in the
     *   token
     * @return JWT Token String
     */
    public String createToken(UUID tokenId, List<PermissionDto> permissions) {
        String creatorUname = "";
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            creatorUname = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        }
        List<Map<String, Object>> permissionsAsMap = permissions.stream()
            .map(PermissionDto::toMap)
            .collect(Collectors.toList());

        return JWT.create()
            .withIssuer(apiIssuer)
            .withIssuedAt(new Date())
            .withJWTId(tokenId.toString())
            .withClaim(apiUserIdKey, creatorUname)
            .withClaim("elrr_permissions", permissionsAsMap)
            .sign(getAlgorithm());
    }

    private Algorithm getAlgorithm() {
        if (algorithm == null) {
            algorithm = Algorithm.HMAC512(secret);
        }
        return algorithm;
    }
}
