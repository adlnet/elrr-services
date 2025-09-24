package com.deloitte.elrr.util;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Constructor;
import java.io.Serializable;
import com.deloitte.elrr.services.dto.ExtensibleDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author mnelakurti
 *
 */
public final class ValueObjectTestUtility {

    private ValueObjectTestUtility() {

    }

    /**
     *
     */
    private static final Validator ACCESSOR_VALIDATOR =
                         ValidatorBuilder.create()
                                .with(new GetterTester())
                                .with(new SetterTester())
                                .build();
    /**
     *
     * @param clazz
     */
    public static void validateAccessors(final Class<?> clazz) {
        // Register custom generators for LocalDate and LocalDateTime to avoid Java module access issues
        RandomFactory.addRandomGenerator(new LocalDateRandomGenerator());
        RandomFactory.addRandomGenerator(new LocalDateTimeRandomGenerator());
        ACCESSOR_VALIDATOR.validate(PojoClassFactory.getPojoClass(clazz));
    }

    /**
     * Validates that a DTO class that extends ExtensibleDto properly handles extensions.
     * This test creates an instance of the specified class, sets some sample extensions,
     * and verifies they can be retrieved correctly.
     * 
     * <p>Usage example:</p>
     * <pre>
     * {@code
     * @Test
     * void testMyDtoExtensions() {
     *     ValueObjectTestUtility.validateExtensions(MyDto.class);
     * }
     * }
     * </pre>
     *
     * @param clazz the class to test (must extend ExtensibleDto and have a default constructor)
     * @throws IllegalArgumentException if the class doesn't extend ExtensibleDto
     * @throws RuntimeException if the class can't be instantiated (e.g., no default constructor)
     */
    public static void validateExtensions(final Class<?> clazz) {
        if (!ExtensibleDto.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class " + clazz.getSimpleName() + " does not extend ExtensibleDto");
        }

        try {
            // Create an instance of the DTO
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            ExtensibleDto dto = (ExtensibleDto) constructor.newInstance();

            // Create sample extensions
            Map<URI, Serializable> extensions = new HashMap<>();
            extensions.put(URI.create("https://example.org/schema#socialSecurityNumber"), "123-45-6789");
            extensions.put(URI.create("https://example.org/schema#preferredName"), "Mike");
            extensions.put(URI.create("https://example.org/schema#customField"), "test value");

            // Set extensions on the DTO
            dto.setExtensions(extensions);

            // Validate that extensions were set correctly
            assertNotNull(dto.getExtensions(), "Extensions should not be null after setting");
            assertEquals("123-45-6789", 
                dto.getExtensions().get(URI.create("https://example.org/schema#socialSecurityNumber")),
                "Social Security Number extension should match");
            assertEquals("Mike", 
                dto.getExtensions().get(URI.create("https://example.org/schema#preferredName")),
                "Preferred Name extension should match");
            assertEquals("test value", 
                dto.getExtensions().get(URI.create("https://example.org/schema#customField")),
                "Custom field extension should match");
            assertEquals(3, dto.getExtensions().size(), "Should have exactly 3 extensions");

        } catch (Exception e) {
            throw new RuntimeException("Failed to test extensions for class " + clazz.getSimpleName(), e);
        }
    }
}
