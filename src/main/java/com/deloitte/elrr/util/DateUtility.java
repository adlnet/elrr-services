package com.deloitte.elrr.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DateUtility {

    private DateUtility() {

    }
    /**
     *
     */
    private static String dateFormat = "yyyy-MM-dd";
    /**
     *
     */
    private static String dateFormatUx = "dd-MMM-yyyy";

    /**
     *
     * @param courseDateStr
     * @param renewal
     * @param renewalStartDateStr
     * @return int
     * @throws Exception
     */
    public static int getYearIndex(final String courseDateStr,
                      final int renewal, final String renewalStartDateStr)
                    throws ParseException {
        SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);
        Date courseDate = formatDate.parse(courseDateStr);
        GregorianCalendar courseDateGC = new GregorianCalendar();
        courseDateGC.setTime(courseDate);
        GregorianCalendar renewalStartDate = new GregorianCalendar();
        renewalStartDate.setTime(formatDate.parse(renewalStartDateStr));
        for (int i = 0; i < renewal; i++) {
            GregorianCalendar renewalEndDate = new GregorianCalendar();
            renewalEndDate.setTime(formatDate.parse(renewalStartDateStr));
            renewalEndDate.add(Calendar.YEAR, i + 1);
            if (renewalStartDate.compareTo(courseDateGC) <= 0
                    && renewalEndDate.compareTo(courseDateGC) >= 0) {
                return i;
            }
        }
        return -1;
    }
    /**
     *
     * @param date
     * @return String
     * @throws ParseException
     */
    public static String getDate(final Date date) {
        SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);
        return formatDate.format(date);
    }
    /**
     *
     * @return Date
     * @throws ParseException
     */
    public static Date getCurrentDate() {
        GregorianCalendar cal = new GregorianCalendar();
        return cal.getTime();
    }
    /**
     *
     * @param dateStr
     * @return String
     * @throws ParseException
     */
    public static String getUXDate(final String dateStr) throws ParseException {
        SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);
        SimpleDateFormat formatUXDate = new SimpleDateFormat(dateFormatUx);
        Date date = formatDate.parse(dateStr);
        return formatUXDate.format(date);
    }
    /**
     *
     * @param date
     * @return String
     * @throws ParseException
     */
    public static String getUXDate(final Date date) {
        SimpleDateFormat formatUXDate = new SimpleDateFormat(dateFormatUx);
        return formatUXDate.format(date);
    }
    /**
     *
     * @param endDateStr
     * @param year
     * @return String
     * @throws ParseException
     */
    public static String getUXDateFromEndDate(final String endDateStr,
            final int year) throws ParseException {
        SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);
        Date endDate = formatDate.parse(endDateStr);
        return getUXTimeFromEndDate(year, endDate);
    }
    /**
     *
     * @param year
     * @param endDate
     * @return String
     */
    private static String getUXTimeFromEndDate(final int year,
            final Date endDate) {
        GregorianCalendar calDate = new GregorianCalendar();
        SimpleDateFormat formatUXDate = new SimpleDateFormat(dateFormatUx);
        calDate.setTime(endDate);
        calDate.add(Calendar.YEAR, -year);
        return formatUXDate.format(calDate.getTime());
    }
    /**
     *
     * @param date
     * @param year
     * @return String
     * @throws ParseException
     */
    public static String addUXDate(final Date date, final int year) {
        return getUXTime(year, date);
    }
    /**
     *
     * @param dateStr
     * @param year
     * @return String
     * @throws ParseException
     */
    public static String addUXDate(final String dateStr, final int year)
            throws ParseException {
        SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);

        Date date = formatDate.parse(dateStr);
        return getUXTime(year, date);
    }
    /**
     *
     * @param year
     * @param date
     * @return String
     */
    private static String getUXTime(final int year, final Date date) {
        GregorianCalendar calDate = new GregorianCalendar();
        SimpleDateFormat formatUXDate = new SimpleDateFormat(dateFormatUx);
        calDate.setTime(date);
        calDate.add(Calendar.YEAR, year);
        calDate.add(Calendar.DATE, -1);
        return formatUXDate.format(calDate.getTime());
    }
    /**
     *
     * @param dateStr
     * @return GregorianCalendar
     * @throws ParseException
     */
    public static GregorianCalendar getDate(final String dateStr)
            throws ParseException {
        SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);

        Date date = formatDate.parse(dateStr);
        GregorianCalendar calDate = new GregorianCalendar();
        calDate.setTime(date);
        return calDate;
    }
    /**
     *
     * @param dateStr
     * @return int
     * @throws ParseException
     */
    public static int getYear(final String dateStr) throws ParseException {
        SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);

        Date date = formatDate.parse(dateStr);
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        return calDate.get(Calendar.YEAR);
    }
    /**
     *
     * @param dateStr
     * @return int
     * @throws ParseException
     */
    public static int getLastDayOfMonth(final String dateStr)
            throws ParseException {
        SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);

        Date date = formatDate.parse(dateStr);
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        return calDate.getActualMaximum(Calendar.DATE);

    }
}
