package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

public class PermissionsWrapperDtoTest {

    /**
     * 
     */
    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(PermissionsWrapperDto.class);
    }
    /**
     * 
     */
    @Test
    void testToString() {
        assertNotNull(new PermissionsWrapperDto().toString());
    }
}
