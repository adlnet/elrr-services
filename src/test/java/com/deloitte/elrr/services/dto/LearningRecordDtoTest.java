package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

class LearningRecordDtoTest {

    /**
    *
    */
    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(LearningRecordDto.class);
    }

    /**
     *
     */
    @Test
    void testToString() {
        assertNotNull(new LearningRecordDto().toString());
    }

    /**
     * Test that LearningRecordDto can handle extensions
     */
    @Test
    void testLearningRecordDtoExtensions() {
        ValueObjectTestUtility.validateExtensions(LearningRecordDto.class);
    }
}
