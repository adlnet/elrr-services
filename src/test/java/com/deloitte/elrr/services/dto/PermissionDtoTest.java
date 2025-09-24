package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

public class PermissionDtoTest {

    /**
     * 
     */
    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(PermissionDto.class);
    }
    /**
     * 
     */
    @Test
    void testToString() {
        assertNotNull(new PermissionDto().toString());
    }
}
