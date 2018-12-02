package teammates.test.driver;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;

import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

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
     * Returns an java.time.Instant object that is offset by a number of days from now,
     * and adjusted to the start of the day in admin's time zone.
     * @param dayOffset number of days offset by (integer).
     * @return          java.time.Instant with appropriate offset from now
     */
    public static Instant getBeginOfTheDayOffsetNowInAdminTimeZone(int dayOffset) {
        return TimeHelper.getInstantDaysOffsetFromNow(dayOffset).atZone(Const.SystemParams.ADMIN_TIME_ZONE)
                .toLocalDate().atStartOfDay(Const.SystemParams.ADMIN_TIME_ZONE).toInstant();
    }

    /**
     * Returns an java.time.Instant object that is offset by a number of days from now,
     * and adjusted to the end of the day in admin's time zone.
     * @param dayOffset number of days offset by (integer).
     * @return          java.time.Instant with appropriate offset from now
     */
    public static Instant getEndOfTheDayOffsetNowInAdminTimeZone(int dayOffset) {
        return TimeHelper.getInstantDaysOffsetFromNow(dayOffset).atZone(Const.SystemParams.ADMIN_TIME_ZONE)
                .toLocalDate().atTime(LocalTime.MAX).atZone(Const.SystemParams.ADMIN_TIME_ZONE).toInstant();
    }

}
