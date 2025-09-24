package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

public class ClientTokenListItemDtoTest {

    /**
     * 
     */
    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(ClientTokenListItemDto.class);
    }
    /**
     * 
     */
    @Test
    void testToString() {
        assertNotNull(new ClientTokenListItemDto().toString());
    }
}
