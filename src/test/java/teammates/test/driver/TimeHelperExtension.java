package teammates.test.driver;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import teammates.common.util.TimeHelper;

/**
 * Holds additional methods for {@link TimeHelper} used only in tests.
 */
public final class TimeHelperExtension {

    private TimeHelperExtension() {
        // utility class
    }

    /**
     * Returns the date object with specified offset in number of hours from now.
     */
    public static Date getHoursOffsetToCurrentTime(int offsetHours) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(cal.getTime());
        cal.add(Calendar.HOUR, +offsetHours);
        return cal.getTime();
    }

    /**
     * Returns one of these : 0100H, 0200H, ..., 0900H, 1000H, ... 2300H, 2359H.
     * Note the last one is different from the others.
     */
    public static String convertToDisplayValueInTimeDropDown(Date date) {
        int optionValue = TimeHelper.convertToOptionValueInTimeDropDown(date);
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
}
