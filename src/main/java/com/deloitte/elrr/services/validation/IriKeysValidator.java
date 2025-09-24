package com.deloitte.elrr.services.validation;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the {@link ValidIriKeys} annotation. Validates
 * that all keys in a Map are valid IRIs (Internationalized Resource
 * Identifiers).
 */
public class IriKeysValidator
        implements ConstraintValidator<ValidIriKeys, Map<URI, Serializable>> {

    @Override
    public void initialize(ValidIriKeys constraintAnnotation) {
        // No configuration needed - IRIs are always absolute with scheme
    }

    @Override
    public boolean isValid(Map<URI, Serializable> map,
            ConstraintValidatorContext context) {
        if (map == null) {
            return true; // null values are considered valid
        }

        context.disableDefaultConstraintViolation();

        for (URI key : map.keySet()) {
            if (!isValidUri(key)) {
                context.buildConstraintViolationWithTemplate(
                        "Invalid IRI key: '" + key + "'")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }

    private boolean isValidUri(URI uri) {
        if (uri == null) {
            return false;
        }

        // IRIs must have a scheme (protocol)
        if (uri.getScheme() == null) {
            return false;
        }

        // IRIs must be absolute
        if (!uri.isAbsolute()) {
            return false;
        }

        return true; // If all checks pass, it's a valid IRI

    }

}
