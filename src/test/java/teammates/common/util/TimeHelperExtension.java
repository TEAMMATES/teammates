package teammates.common.util;

import java.time.Duration;
import java.time.Instant;

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

}
