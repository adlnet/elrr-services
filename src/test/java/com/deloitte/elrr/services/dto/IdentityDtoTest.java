package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

class IdentityDtoTest {

    /**
    *
    */
    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(IdentityDto.class);
    }

    /**
     *
     */
    @Test
    void testToString() {
        assertNotNull(new IdentityDto().toString());
    }
}
