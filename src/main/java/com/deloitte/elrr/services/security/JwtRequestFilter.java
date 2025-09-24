package com.deloitte.elrr.services.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.deloitte.elrr.services.security.SystemAuthority.SystemRole;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.deloitte.elrr.jpa.svc.ClientTokenSvc;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private ClientTokenSvc clientTokenSvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String jwtStr = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7)
                : null;

        if (SecurityContextHolder.getContext().getAuthentication() == null
                && jwtStr != null) {

            DecodedJWT jwt;
            List<SystemAuthority> authList = new ArrayList<SystemAuthority>();
            try {
                // First decode the token to check the issuer
                jwt = jwtUtil.decodeToken(jwtStr);

                // Check if the issuer is on the admin whitelist
                if (jwtUtil.getAdminIssuerWhitelist()
                        .contains(jwt.getIssuer())) {
                    // Admin user JWT - skip verification
                    handleAdminUserJwt(jwt);

                } else {
                    // Not on whitelist - verify the token
                    jwt = jwtUtil.verify(jwtStr);
                    // internal issuer verification
                    if (!jwt.getIssuer().equals(jwtUtil.getApiIssuer())) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid Token Issuer");
                        return;
                    }
                    // verify that the token exists in the database
                    if (!clientTokenSvc
                            .existsByJwtId(UUID.fromString(jwt.getId()))) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                "Invalid Token");
                        return;
                    }
                    authList.add(new SystemAuthority(SystemRole.ROLE_API));
                    SecurityContextHolder.getContext().setAuthentication(
                            new JwtAuthenticationToken(authList, jwt,
                                    jwtUtil.getApiUserIdKey()));
                }
            } catch (AlgorithmMismatchException
                    | SignatureVerificationException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid Token");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void handleAdminUserJwt(DecodedJWT jwt) {
        List<String> roles = jwt.getClaim(jwtUtil.getAdminRoleKey())
                .asList(String.class);
        if (roles.contains(jwtUtil.getAdminRole())) {
            List<SystemAuthority> authList = new ArrayList<SystemAuthority>();
            authList.add(
                    new SystemAuthority(SystemRole.ROLE_ADMIN));
            SecurityContextHolder.getContext().setAuthentication(
                    new AdminJwtAuthenticationToken(authList, jwt,
                            jwtUtil.getAdminUserIdKey()));
        }
    }
}
