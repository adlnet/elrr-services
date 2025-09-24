package com.deloitte.elrr.services.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Validation annotation to ensure that all keys in a Map are valid IRIs.
 * This validator checks that each key in the map represents a valid
 * Internationalized Resource Identifier (IRI).
 */
@Documented
@Constraint(validatedBy = IriKeysValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIriKeys {

    /**
     * The validation error message.
     *
     * @return the error message template
     */
    String message() default "Map contains invalid IRI keys";

    /**
     * The validation groups.
     *
     * @return the groups the constraint belongs to
     */
    Class<?>[] groups() default {};

    /**
     * The constraint payload.
     *
     * @return the payload associated to the constraint
     */
    Class<? extends Payload>[] payload() default {};
}
