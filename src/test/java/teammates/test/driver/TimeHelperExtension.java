package teammates.test.driver;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Holds additional methods for {@link teammates.common.util.TimeHelper} used only in tests.
 */
public final class TimeHelperExtension {

    private TimeHelperExtension() {
        // utility class
    }

    /**
     * Returns one of these : 0100H, 0200H, ..., 0900H, 1000H, ... 2300H, 2359H.
     * Note the last one is different from the others.
     */
    @Deprecated
    public static String convertToDisplayValueInTimeDropDown(LocalDateTime date) {
        int optionValue = convertToOptionValueInTimeDropDown(date);
        if (optionValue == 24) {
            return "2359H";
        } else if (optionValue >= 0 && optionValue < 10) {
            return "0" + optionValue + "00H";
        } else if (optionValue >= 10 && optionValue < 24) {
            return optionValue + "00H";
        } else {
            throw new RuntimeException("Unrecognized time option: " + optionValue);
        }
    }

    /**
     * Formats a date in the corresponding option value in 'Time' dropdowns The
     * hour just after midnight is converted to option 24 (i.e., 2359 as shown
     * to the user) 23.59 is also converted to 24. (i.e., 23.59-00.59 ---> 24)
     */
    @Deprecated
    public static int convertToOptionValueInTimeDropDown(LocalDateTime localDateTime) {
        //TODO: see if we can eliminate this method (i.e., merge with convertToDisplayValueInTimeDropDown)
        int hour = localDateTime.getHour();
        if (hour == 0 || hour == 23 && localDateTime.getMinute() == 59) {
            return 24;
        }
        return hour;
    }

    /**
     * Returns an java.time.Instant object that is offset by a number of minutes from now.
     * @param offsetInMinutes number of minutes offset by (integer).
     * @return java.time.Instant offset by offsetInMinutes minutes from now.
     */
    public static Instant getInstantMinutesOffsetFromNow(long offsetInMinutes) {
        return Instant.now().plus(Duration.ofMinutes(offsetInMinutes));
    }

    /**
     * Returns an java.time.Instant object that is offset by a number of hours from now.
     * @param offsetInHours number of hours offset by (integer).
     * @return java.time.Instant offset by offsetInHours hours from now.
     */
    public static Instant getInstantHoursOffsetFromNow(long offsetInHours) {
        return Instant.now().plus(Duration.ofHours(offsetInHours));
    }

    /**
     * Parses a {@code LocalDate} object from a date string and parsing pattern.
     *
     * @param dateString the string containing the date
     * @param pattern    the parsing pattern of the datetime string
     * @return the parsed {@code LocalDate} object, or {@code null} if there are errors
     */
    private static LocalDate parseLocalDate(String dateString, String pattern) {
        if (dateString == null || pattern == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a {@code LocalDateTime} object from separated date, hour and minute strings.
     * Example: date "Tue, 01 Apr, 2014", hour "23", min "59"
     *
     * @param date date in format "EEE, dd MMM, yyyy"
     * @param hour hour-of-day (0-23)
     * @param min  minute-of-hour (0-59)
     * @return the parsed {@code LocalDateTime} object, or {@code null} if there are errors
     */
    @Deprecated
    public static LocalDateTime parseDateTimeFromSessionsForm(String date, String hour, String min) {
        LocalDate localDate = parseDateFromSessionsForm(date);
        if (localDate == null) {
            return null;
        }
        if (hour == null || min == null) {
            return null;
        }
        try {
            return localDate.atTime(Integer.parseInt(hour), Integer.parseInt(min));
        } catch (DateTimeException | NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a {@code LocalDate} object from a date string.
     * Example: date "Tue, 01 Apr, 2014"
     *
     * @param date date in format "EEE, dd MMM, yyyy"
     * @return the parsed {@code LocalDate} object, or {@code null} if there are errors
     */
    @Deprecated
    public static LocalDate parseDateFromSessionsForm(String date) {
        return parseLocalDate(date, "EEE, dd MMM, yyyy");
    }

    /**
     * Formats a datetime stamp from a {@code LocalDateTime} using a formatting pattern.
     *
     * <p>Note: a formatting pattern containing 'a' (for the period; AM/PM) is treated differently at noon/midday.
     * Using that pattern with a datetime whose time falls on "12:00 PM" will cause it to be formatted as "12:00 NOON".</p>
     *
     * @param localDateTime the LocalDateTime to be formatted
     * @param pattern       formatting pattern, see Oracle docs for DateTimeFormatter for pattern table
     * @return the formatted datetime stamp string
     */
    private static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null || pattern == null) {
            return "";
        }
        String processedPattern = pattern;
        if (localDateTime.getHour() == 12 && localDateTime.getMinute() == 0) {
            processedPattern = pattern.replace("a", "'NOON'");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(processedPattern);
        return localDateTime.format(formatter);
    }

    /**
     * Formats a date stamp from a {@code localDateTime} for populating the sessions form.
     * Example: Sun, 01 Apr, 2018
     *
     * <p>This method discards the time stored in the {@code localDateTime}.</p>
     *
     * @param localDateTime the LocalDateTime to be formatted
     * @return the formatted date stamp string
     */
    @Deprecated
    public static String formatDateForSessionsForm(LocalDateTime localDateTime) {
        return formatLocalDateTime(localDateTime, "EEE, dd MMM, yyyy");
    }

    /**
     * Converts the {@code Instant} at the specified {@code timeZone} to {@code localDateTime}.
     */
    public static LocalDateTime convertInstantToLocalDateTime(Instant instant, ZoneId timeZoneId) {
        return instant == null ? null : instant.atZone(timeZoneId).toLocalDateTime();
    }

}
