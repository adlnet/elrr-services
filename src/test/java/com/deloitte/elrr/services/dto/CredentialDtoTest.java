package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

class CredentialDtoTest {

    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(CredentialDto.class);
    }

    /**
     *
     */
    @Test
    void testToString() {
        assertNotNull(new CredentialDto().toString());
    }
}
