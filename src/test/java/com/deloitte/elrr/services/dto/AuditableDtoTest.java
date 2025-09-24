package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

class AuditableDtoTest {

    /**
     * Test to validate accessors using utility method.
     */
    @Test
    void test() {
        // Create a concrete implementation for testing since AuditableDto is abstract
        class TestAuditableDto extends AuditableDto {
        }
        
        ValueObjectTestUtility.validateAccessors(TestAuditableDto.class);
    }

    /**
     * Test toString method.
     */
    @Test
    void testToString() {
        // Create a concrete implementation for testing since AuditableDto is abstract
        class TestAuditableDto extends AuditableDto {
        }
        
        assertNotNull(new TestAuditableDto().toString());
    }
}
