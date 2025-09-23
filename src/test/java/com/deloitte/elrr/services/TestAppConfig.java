package com.deloitte.elrr.services;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.deloitte.elrr.services.config.AuditorAwareImpl;
import com.deloitte.elrr.services.security.CustomPermissionEvaluator;
import com.deloitte.elrr.services.security.JwtUtil;
import com.deloitte.elrr.services.security.SecurityActionContext;

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
     * Creates test SecurityActionContext Bean for use in tests.
     * @return Mocked SecurityActionContext for tests
     */
    @Bean
    public SecurityActionContext securityActionContext() {
        return Mockito.mock(SecurityActionContext.class);
    }

    /**
     * Creates test CustomPermissionEvaluator Bean for use in tests.
     * @return CustomPermissionEvaluator for tests
     */
    @Bean
    public CustomPermissionEvaluator permissionEvaluator() {
        return new CustomPermissionEvaluator();
    }

    /**
     * Creates test AuditorAware Bean for use in tests.
     * This is needed for JPA auditing in WebMvcTest contexts.
     * @return AuditorAwareImpl for tests
     */
    @Bean
    public AuditorAwareImpl auditorAwareImpl() {
        return new AuditorAwareImpl();
    }

}
