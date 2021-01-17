package teammates.common.util;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesProvider;

import teammates.common.exception.TeammatesException;

/**
 * A helper class to hold time-related functions (e.g., converting dates to strings etc.).
 *
 * <p>Time zone is assumed as UTC unless specifically mentioned.
 */
public final class TimeHelper {

    private static final Logger log = Logger.getLogger();

    private TimeHelper() {
        // utility class
    }

    /**
     * Registers the zone rules loaded from resources via {@link TzdbResourceZoneRulesProvider}.
     * Some manipulation of the system class loader is required to enable loading of a custom
     * {@link ZoneRulesProvider} in GAE.
     */
    public static void registerResourceZoneRules() {
        try {
            ClassLoader originalScl = ClassLoader.getSystemClassLoader();

            // ZoneRulesProvider uses the system class loader for loading a custom provider as the default provider.
            // However, GAE's system class loader includes only the Java runtime and not the application. Hence, we
            // use reflection to temporarily replace the system class loader with the class loader for the current context.
            Field scl = ClassLoader.class.getDeclaredField("scl");
            scl.setAccessible(true);
            scl.set(null, Thread.currentThread().getContextClassLoader());

            // ZoneRulesProvider reads this system property to determine which provider to use as the default.
            System.setProperty("java.time.zone.DefaultZoneRulesProvider",
                    TzdbResourceZoneRulesProvider.class.getCanonicalName());

            // This first reference to ZoneRulesProvider executes the class's static initialization block,
            // performing the actual registration of our custom provider named in the system property above.
            // The system class loader is used to load the class from the name.
            // If any exceptions occur, an Error is thrown.
            log.info("Registered zone rules version " + ZoneRulesProvider.getVersions("UTC").firstKey());

            // Restore the original system class loader.
            scl.set(null, originalScl);

        } catch (ReflectiveOperationException | Error e) {
            log.severe("Failed to register zone rules: " + TeammatesException.toStringWithStackTrace(e));
        }
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
    public static String formatInstant(Instant instant, ZoneId timeZone, String pattern) {
        if (instant == null || timeZone == null || pattern == null) {
            return "";
        }
        ZonedDateTime zonedDateTime = instant.atZone(timeZone);
        String processedPattern = pattern;
        if (zonedDateTime.getHour() == 12 && zonedDateTime.getMinute() == 0) {
            processedPattern = pattern.replace("a", "'NOON'");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(processedPattern);
        return zonedDateTime.format(formatter);
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
            Assumption.fail("Date in String is in wrong format.");
            return null;
        }
    }

}
