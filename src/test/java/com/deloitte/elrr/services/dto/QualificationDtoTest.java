package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

class QualificationDtoTest {

    /**
    *
    */
    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(QualificationDto.class);
    }

    /**
     *
     */
    @Test
    void testToString() {
        assertNotNull(new QualificationDto().toString());
    }

    /**
     * Test that QualificationDto can handle extensions
     */
    @Test
    void testQualificationDtoExtensions() {
        ValueObjectTestUtility.validateExtensions(QualificationDto.class);
    }
}
