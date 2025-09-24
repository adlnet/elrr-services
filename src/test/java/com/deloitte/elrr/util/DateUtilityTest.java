package com.deloitte.elrr.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

/**
 * @author mnelakurti
 *
 */
class DateUtilityTest {

    @Test
    void test() throws Exception {
        assertNotNull(DateUtility.addUXDate(new Date(), 1));
        assertNotNull(DateUtility.addUXDate("2022-11-11", 1));
        // Use time tolerance for current date
        Date now = new Date();
        Date currentDate = DateUtility.getCurrentDate();
        long diffMillis = Math.abs(currentDate.getTime() - now.getTime());
        assertTrue(diffMillis < 1000,
                "Current date difference is too large: " + diffMillis);
        assertNotNull(DateUtility.getDate("2022-11-11"));
        assertNotNull(DateUtility.getYearIndex("2020-05-30", 2, "2022-11-11"));
        assertNotNull(DateUtility.getLastDayOfMonth("2022-11-11"));
        // assertNotNull(DateUtility.addUXDate("11/11/2022", 1));
        // assertNotNull(DateUtility.addUXDate(new Date(),1));
        // fail("Not yet implemented");
        DateUtility.getUXDate(new Date());
        DateUtility.getUXDate("2022-11-11");
        DateUtility.getUXDateFromEndDate("2022-11-11", 1);
        DateUtility.getYear("2022-11-11");
        DateUtility.getYearIndex("2022-11-11", 1, "2022-11-11");
        DateUtility.getYearIndex("2022-11-11", 1, "2022-11-12");
        DateUtility.getYearIndex("2022-11-12", 1, "2022-11-11");
        assertNotNull(DateUtility.getDate(new Date()));
    }

}
