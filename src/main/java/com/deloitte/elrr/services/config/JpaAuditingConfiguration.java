package com.deloitte.elrr.services.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration for JPA auditing.
 * This enables automatic auditing for all JPA entities.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaAuditingConfiguration {
}
