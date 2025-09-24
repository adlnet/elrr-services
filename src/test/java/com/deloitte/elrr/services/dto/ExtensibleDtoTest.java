package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

/**
 * Test class for ExtensibleDto
 */
class ExtensibleDtoTest {

    private TestExtensibleDto testExtensibleDto;

    /**
     * Concrete implementation of ExtensibleDto for testing purposes
     */
    private static class TestExtensibleDto extends ExtensibleDto {
        // Simple concrete implementation for testing
    }

    @BeforeEach
    void setUp() {
        testExtensibleDto = new TestExtensibleDto();
    }

    /**
     * Test accessor methods using ValueObjectTestUtility
     */
    @Test
    void testAccessors() {
        ValueObjectTestUtility.validateAccessors(TestExtensibleDto.class);
    }

    /**
     * Test extensions field initialization
     */
    @Test
    void testExtensionsInitialization() {
        assertNull(testExtensibleDto.getExtensions());
    }

    /**
     * Test setting and getting extensions with IRI keys using utility method
     */
    @Test
    void testExtensionsSetterGetter() {
        ValueObjectTestUtility.validateExtensions(TestExtensibleDto.class);
    }

    /**
     * Test toString method
     */
    @Test
    void testToString() {
        assertNotNull(testExtensibleDto.toString());
    }

    /**
     * Test that validateExtensions throws exception for non-ExtensibleDto classes
     */
    @Test
    void testValidateExtensionsThrowsExceptionForNonExtensibleDto() {
        // Test with a class that doesn't extend ExtensibleDto
        class NonExtensibleDto {
            // This class doesn't extend ExtensibleDto
        }

        try {
            ValueObjectTestUtility.validateExtensions(NonExtensibleDto.class);
            // Should not reach this point
            throw new AssertionError("Expected IllegalArgumentException but none was thrown");
        } catch (IllegalArgumentException e) {
            // Expected exception
            assert e.getMessage().contains("does not extend ExtensibleDto");
        }
    }
}
