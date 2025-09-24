package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

class LocationDtoTest {

    /**
    *
    */
    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(LocationDto.class);
    }

    /**
     *
     */
    @Test
    void testToString() {
        assertNotNull(new LocationDto().toString());
    }

    /**
     * Test that LocationDto can handle extensions
     */
    @Test
    void testLocationDtoExtensions() {
        ValueObjectTestUtility.validateExtensions(LocationDto.class);
    }
}
