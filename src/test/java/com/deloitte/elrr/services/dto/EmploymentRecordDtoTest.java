package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

class EmploymentRecordDtoTest {

    /**
    *
    */
    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(EmploymentRecordDto.class);
    }

    /**
     *
     */
    @Test
    void testToString() {
        assertNotNull(new EmploymentRecordDto().toString());
    }

    /**
     * Test that EmploymentRecordDto can handle extensions
     */
    @Test
    void testEmploymentRecordDtoExtensions() {
        ValueObjectTestUtility.validateExtensions(EmploymentRecordDto.class);
    }
}
