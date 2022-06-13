package teammates.common.util;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A helper class to hold time-related functions (e.g., converting dates to strings etc.).
 *
 * <p>Time zone is assumed as UTC unless specifically mentioned.
 */
public final class TimeHelper {

    private TimeHelper() {
        // utility class
    }

    /**
     * Returns an Instant that represents the nearest hour before the given object.
     *
     * <p>The time zone used is assumed to be the default timezone, namely UTC.
     */
    public static Instant getInstantNearestHourBefore(Instant instant) {
        String nearestHourString = formatInstant(instant, Const.DEFAULT_TIME_ZONE, "yyyy-MM-dd'T'HH:00:00.00'Z'");
        return parseInstant(nearestHourString);
    }

    /**
     * Returns an Instant that is offset by a number of days from now.
     *
     * @param offsetInDays integer number of days to offset by
     * @return an Instant offset by {@code offsetInDays} days
     */
    public static Instant getInstantDaysOffsetFromNow(long offsetInDays) {
        return Instant.now().plus(Duration.ofDays(offsetInDays));
    }

    /**
     * Returns an Instant that is offset by a number of days before now.
     *
     * @param offsetInDays integer number of days to offset by
     * @return an Instant offset by {@code offsetInDays} days
     */
    public static Instant getInstantDaysOffsetBeforeNow(long offsetInDays) {
        return Instant.now().minus(Duration.ofDays(offsetInDays));
    }

    /**
     * Returns an Instant that is offset by a number of hours from now.
     *
     * @param offsetInHours integer number of hours to offset by
     * @return an Instant offset by {@code offsetInHours} hours
     */
    public static Instant getInstantHoursOffsetFromNow(long offsetInHours) {
        return Instant.now().plus(Duration.ofHours(offsetInHours));
    }

    /**
     * Formats a datetime stamp from an {@code instant} using a formatting pattern.
     *
     * <p>Note: a formatting pattern containing 'a' (for the period; AM/PM) is treated differently at noon/midday.
     * Using that pattern with a datetime whose time falls on "12:00 PM" will cause it to be formatted as "12:00 NOON".</p>
     *
     * @param instant  the instant to be formatted
     * @param timeZone the time zone to compute local datetime
     * @param pattern  formatting pattern, see Oracle docs for DateTimeFormatter for pattern table
     * @return the formatted datetime stamp string
     */
    public static String formatInstant(Instant instant, String timeZone, String pattern) {
        if (instant == null || timeZone == null || pattern == null) {
            return "";
        }
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(timeZone));
        String processedPattern = pattern;
        if (zonedDateTime.getHour() == 12 && zonedDateTime.getMinute() == 0) {
            processedPattern = pattern.replace("a", "'NOON'");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(processedPattern);
        return zonedDateTime.format(formatter);
    }

    /**
     * Gets an Instant which is adjusted for midnight time (23:59 and 00:00) at the specified time zone.
     * The direction of adjustment (23:59 to 00:00 or vice versa) is determined by {@code isForward} parameter.
     */
    public static Instant getMidnightAdjustedInstantBasedOnZone(Instant instant, String timeZone, boolean isForward) {
        if (isSpecialTime(instant)) {
            return instant;
        }
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(timeZone));
        if (isForward && zonedDateTime.getHour() == 23 && zonedDateTime.getMinute() == 59) {
            zonedDateTime = zonedDateTime.plusMinutes(1L);
        } else if (!isForward && zonedDateTime.getHour() == 0 && zonedDateTime.getMinute() == 0) {
            zonedDateTime = zonedDateTime.minusMinutes(1L);
        }
        return zonedDateTime.toInstant();
    }

    /**
     * Returns whether the given {@code instant} is being used as a special representation, signifying its face value
     * should not be used without proper processing.
     *
     * <p>A {@code null} instant is not a special time.</p>
     *
     * @param instant the instant to test
     * @return {@code true} if the given instant is used as a special representation, {@code false} otherwise
     */
    public static boolean isSpecialTime(Instant instant) {
        if (instant == null) {
            return false;
        }

        return instant.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)
                || instant.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)
                || instant.equals(Const.TIME_REPRESENTS_LATER)
                || instant.equals(Const.TIME_REPRESENTS_NOW);
    }

    /**
     * Parses an {@code Instant} object from a datetime string in the ISO 8601 format.
     *
     * @return the parsed {@code Instant} object
     * @throws AssertionError if there is a parsing error
     * @see <a href="https://www.w3.org/TR/NOTE-datetime">https://www.w3.org/TR/NOTE-datetime</a>
     */
    public static Instant parseInstant(String dateTimeString) {
        try {
            return OffsetDateTime.parse(dateTimeString).toInstant();
        } catch (DateTimeParseException e) {
            assert false : "Date in String is in wrong format.";
            return null;
        }
    }

}
