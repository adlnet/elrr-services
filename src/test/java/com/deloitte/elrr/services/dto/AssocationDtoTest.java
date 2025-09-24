package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

class AssociationDtoTest {

    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(AssociationDto.class);
    }

    /**
     *
     */
    @Test
    void testToString() {
        assertNotNull(new AssociationDto().toString());
    }
}
