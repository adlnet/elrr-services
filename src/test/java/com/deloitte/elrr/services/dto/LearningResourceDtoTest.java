package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

class LearningResourceDtoTest {

    /**
    *
    */
    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(LearningResourceDto.class);
    }

    /**
     *
     */
    @Test
    void testToString() {
        assertNotNull(new LearningResourceDto().toString());
    }

    /**
     * Test that LearningResourceDto can handle extensions
     */
    @Test
    void testLearningResourceDtoExtensions() {
        ValueObjectTestUtility.validateExtensions(LearningResourceDto.class);
    }
}
