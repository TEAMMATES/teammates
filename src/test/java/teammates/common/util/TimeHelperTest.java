package teammates.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link TimeHelper}.
 */
public class TimeHelperTest extends BaseTestCase {

    private static final String DATETIME_DISPLAY_FORMAT = "EEE, dd MMM yyyy, hh:mm a z";

    @Test
    public void testEndOfYearDates() {
        LocalDateTime date = LocalDateTime.of(2015, Month.DECEMBER, 30, 12, 0);
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON UTC", TimeHelper.formatInstant(
                date.atZone(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"), DATETIME_DISPLAY_FORMAT));
    }

    @Test
    public void testFormatDateTimeForDisplay() {
        ZoneId zoneId = ZoneId.of("UTC");
        Instant instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 12, 0).atZone(zoneId).toInstant();
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC", TimeHelper.formatInstant(instant, zoneId, DATETIME_DISPLAY_FORMAT));

        zoneId = ZoneId.of("Asia/Singapore");
        instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 16, 0).atZone(zoneId).toInstant();
        assertEquals("Mon, 30 Nov 2015, 04:00 PM SGT", TimeHelper.formatInstant(instant, zoneId, DATETIME_DISPLAY_FORMAT));

        instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 4, 0).atZone(zoneId).toInstant();
        assertEquals("Mon, 30 Nov 2015, 04:00 AM SGT", TimeHelper.formatInstant(instant, zoneId, DATETIME_DISPLAY_FORMAT));
    }

}
