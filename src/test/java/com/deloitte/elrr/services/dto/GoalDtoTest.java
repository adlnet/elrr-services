package com.deloitte.elrr.services.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.util.ValueObjectTestUtility;

class GoalDtoTest {

    /**
    *
    */
    @Test
    void test() {
        ValueObjectTestUtility.validateAccessors(GoalDto.class);
    }

    /**
     *
     */
    @Test
    void testToString() {
        assertNotNull(new GoalDto().toString());
    }

    /**
     * Test that GoalDto can handle extensions
     */
    @Test
    void testGoalDtoExtensions() {
        ValueObjectTestUtility.validateExtensions(GoalDto.class);
    }
}
