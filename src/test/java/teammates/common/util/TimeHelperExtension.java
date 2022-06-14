package teammates.common.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Holds additional methods for {@link teammates.common.util.TimeHelper} used only in tests.
 */
public final class TimeHelperExtension {

    private TimeHelperExtension() {
        // utility class
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
     * Returns an java.time.Instant object that is offset by a number of hours from now truncated to hours.
     * @param offsetInHours number of hours offset by (integer).
     * @return java.time.Instant offset by offsetInHours hours from now truncated to hours in the given timezone.
     */
    public static Instant getInstantTruncatedHoursOffsetFromNow(long offsetInHours) {
        return Instant.now().plus(Duration.ofHours(offsetInHours)).truncatedTo(ChronoUnit.HOURS);
    }

    /**
     * Returns an java.time.Instant object that is offset by a number of hours from now truncated to hours
     * in the given timezone.
     * @param offsetInHours number of hours offset by (integer).
     * @param timezone timezone used.
     * @return java.time.Instant offset by offsetInHours hours from now truncated to hours in the given timezone.
     */
    public static Instant getTimezoneInstantTruncatedHoursOffsetFromNow(long offsetInHours, String timezone) {
        return ZonedDateTime.now(ZoneId.of(timezone)).plus(Duration.ofHours(offsetInHours))
                .truncatedTo(ChronoUnit.HOURS).toInstant();
    }

    /**
     * Returns an java.time.Instant object that is offset by a number of days from now.
     * @param offsetInDays number of days offset by (integer).
     * @return java.time.Instant offset by offsetInDays days from now.
     */
    public static Instant getInstantDaysOffsetFromNow(long offsetInDays) {
        return Instant.now().plus(Duration.ofDays(offsetInDays));
    }

}
