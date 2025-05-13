package com.library.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for date operations.
 */
public class DateUtil {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");
    
    /**
     * Formats a date to string using the default date format (yyyy-MM-dd).
     * 
     * @param date The date to format
     * @return The formatted date string
     */
    
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        synchronized (DISPLAY_DATE_FORMAT) {
            return DISPLAY_DATE_FORMAT.format(date);
        }
    }
    
    /**
     * Formats a date to string using the date time format (yyyy-MM-dd HH:mm:ss).
     * 
     * @param date The date to format
     * @return The formatted date time string
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        synchronized (DATE_TIME_FORMAT) {
            return DATE_TIME_FORMAT.format(date);
        }
    }
    
    /**
     * Parses a date string to a Date object using the default date format (yyyy-MM-dd).
     * 
     * @param dateStr The date string to parse
     * @return The parsed Date object
     * @throws ParseException if the string cannot be parsed
     */
    public static Date parseDate(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        synchronized (DATE_FORMAT) {
            return DATE_FORMAT.parse(dateStr);
        }
    }
    
    /**
     * Parses a date time string to a Date object using the date time format (yyyy-MM-dd HH:mm:ss).
     * 
     * @param dateTimeStr The date time string to parse
     * @return The parsed Date object
     * @throws ParseException if the string cannot be parsed
     */
    public static Date parseDateTime(String dateTimeStr) throws ParseException {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        synchronized (DATE_TIME_FORMAT) {
            return DATE_TIME_FORMAT.parse(dateTimeStr);
        }
    }
    
    /**
     * Calculates a due date based on a start date and a loan period.
     * 
     * @param startDate The start date
     * @param days The number of days in the loan period
     * @return The calculated due date
     */
    public static Date calculateDueDate(Date startDate, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
    
    /**
     * Calculates the number of days between two dates.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return The number of days between the dates
     */
    public static long daysBetween(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        return diff / (24 * 60 * 60 * 1000);
    }
    
    /**
     * Checks if a date is before the current date.
     * 
     * @param date The date to check
     * @return true if the date is in the past, false otherwise
     */
    public static boolean isPastDate(Date date) {
        return date.before(new Date());
    }
    
    /**
     * Gets the start of a day (00:00:00) for a given date.
     * 
     * @param date The date
     * @return The date at the start of the day
     */
    public static Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Gets the end of a day (23:59:59) for a given date.
     * 
     * @param date The date
     * @return The date at the end of the day
     */
    public static Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    /**
     * Gets a date that is a specified number of days ago.
     * 
     * @param days The number of days to go back
     * @return The date a specified number of days ago
     */
    public static Date getDaysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return cal.getTime();
    }
    
    /**
     * Gets a date that is a specified number of months ago.
     * 
     * @param months The number of months to go back
     * @return The date a specified number of months ago
     */
    public static Date getMonthsAgo(int months) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -months);
        return cal.getTime();
    }
    
    /**
     * Gets a date that is a specified number of years ago.
     * 
     * @param years The number of years to go back
     * @return The date a specified number of years ago
     */
    public static Date getYearsAgo(int years) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -years);
        return cal.getTime();
    }
    
    /**
     * Creates a Date object from individual date components.
     * 
     * @param year The year
     * @param month The month (1-12)
     * @param day The day of the month
     * @return The created Date object
     */
    public static Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar months are 0-based
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
