package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

class PersonDtoTest {

    /**
    *
    */
    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(PersonDto.class);
    }

    /**
     *
     */
    @Test
    void testToString() {
        assertNotNull(new PersonDto().toString());
    }

    /**
     * Test that PersonDto can handle extensions
     */
    @Test
    void testPersonDtoExtensions() {
        ValueObjectTestUtility.validateExtensions(PersonDto.class);
    }
}
