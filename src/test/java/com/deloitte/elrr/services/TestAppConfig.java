package com.deloitte.elrr.services;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.deloitte.elrr.services.security.CustomPermissionEvaluator;
import com.deloitte.elrr.services.security.JwtUtil;

@TestConfiguration
public class TestAppConfig {

    public static final String TEST_SECRET = "secretsecretsecretshhh";

    /**
     * Creates test JwtUtil Bean for use in tests.
     * @return JwtUtil for tests
     */
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(TEST_SECRET);
    }

    /**
     * Creates test CustomPermissionEvaluator Bean for use in tests.
     * @return CustomPermissionEvaluator for tests
     */
    @Bean
    public CustomPermissionEvaluator permissionEvaluator() {
        return new CustomPermissionEvaluator();
    }

}
