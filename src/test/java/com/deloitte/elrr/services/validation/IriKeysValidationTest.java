package com.deloitte.elrr.services.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.deloitte.elrr.services.dto.ExtensibleDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Test class to validate the {@link ValidIriKeys} annotation and 
 * {@link IriKeysValidator} implementation.
 */
class IriKeysValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidIriKeys() {
        TestExtensibleDto dto = new TestExtensibleDto();
        Map<URI, Serializable> extensions = new HashMap<>();
        
        // Add valid IRIs (all absolute with schemes)
        extensions.put(URI.create("https://example.com/property1"), "value1");
        extensions.put(URI.create("http://schema.org/name"), "value2");
        extensions.put(URI.create("urn:isbn:1234567890"), "value3");
        extensions.put(URI.create("mailto:user@example.com"), "value4");
        
        dto.setExtensions(extensions);
        
        Set<ConstraintViolation<TestExtensibleDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid IRIs should not produce violations");
    }

    @Test
    void testInvalidIriKey_SimpleWord() {
        TestExtensibleDto dto = new TestExtensibleDto();
        Map<URI, Serializable> extensions = new HashMap<>();
        
        try {
            // "foo" gets through URI.create() but should be rejected by our validator
            extensions.put(URI.create("foo"), "value1");
            dto.setExtensions(extensions);
            
            Set<ConstraintViolation<TestExtensibleDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty(), "Simple word 'foo' should be rejected as invalid IRI");
            assertEquals(1, violations.size());
            assertTrue(violations.iterator().next().getMessage().contains("Invalid IRI key"));
        } catch (IllegalArgumentException e) {
            // If URI.create() fails, that's also acceptable validation
            assertTrue(true, "URI.create() rejected the invalid string");
        }
    }

    @Test
    void testInvalidIriKey_EmptyString() {
        TestExtensibleDto dto = new TestExtensibleDto();
        Map<URI, Serializable> extensions = new HashMap<>();
        
        try {
            extensions.put(URI.create(""), "value1");
            dto.setExtensions(extensions);
            
            Set<ConstraintViolation<TestExtensibleDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty(), "Empty string should be rejected as invalid IRI");
        } catch (IllegalArgumentException e) {
            // If URI.create() fails, that's also acceptable validation
            assertTrue(true, "URI.create() rejected the empty string");
        }
    }

    @Test
    void testNullExtensions() {
        TestExtensibleDto dto = new TestExtensibleDto();
        dto.setExtensions(null);
        
        Set<ConstraintViolation<TestExtensibleDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Null extensions should be considered valid");
    }

    @Test
    void testEmptyExtensions() {
        TestExtensibleDto dto = new TestExtensibleDto();
        dto.setExtensions(new HashMap<>());
        
        Set<ConstraintViolation<TestExtensibleDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Empty extensions map should be valid");
    }

    /**
     * Test implementation of ExtensibleDto for testing purposes.
     */
    private static class TestExtensibleDto extends ExtensibleDto {
        // Test implementation - inherits the validation from ExtensibleDto
    }
}
