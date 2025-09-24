package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

class FacilityDtoTest {

    /**
    *
    */
    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(FacilityDto.class);
    }

    /**
     *
     */
    @Test
    void testToString() {
        assertNotNull(new FacilityDto().toString());
    }

    /**
     * Test that FacilityDto can handle extensions
     */
    @Test
    void testFacilityDtoExtensions() {
        ValueObjectTestUtility.validateExtensions(FacilityDto.class);
    }
}
