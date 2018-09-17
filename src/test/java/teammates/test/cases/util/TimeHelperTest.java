package teammates.test.cases.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

import org.testng.annotations.Test;

import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link TimeHelper}.
 */
public class TimeHelperTest extends BaseTestCase {

    @Test
    public void testParseDateTimeFromSessionsForm() {
        String testDate = "Fri, 01 Feb, 2013";
        String testTime = "0";
        LocalDateTime expectedOutput = LocalDateTime.of(2013, Month.FEBRUARY, 1, 0, 0);

        testTime = "0";
        ______TS("boundary case: time = 0");
        assertEquals(expectedOutput, TimeHelper.parseDateTimeFromSessionsForm(testDate, testTime));

        ______TS("boundary case: time = 24");
        testTime = "24";
        expectedOutput = LocalDateTime.of(2013, Month.FEBRUARY, 1, 23, 59);
        assertEquals(expectedOutput, TimeHelper.parseDateTimeFromSessionsForm(testDate, testTime));

        ______TS("negative time");
        assertNull(TimeHelper.parseDateTimeFromSessionsForm(testDate, "-5"));

        ______TS("large time");
        assertNull(TimeHelper.parseDateTimeFromSessionsForm(testDate, "68"));

        ______TS("date null");
        assertNull(TimeHelper.parseDateTimeFromSessionsForm(null, testTime));

        ______TS("time null");
        assertNull(TimeHelper.parseDateTimeFromSessionsForm(testDate, null));

        ______TS("invalid time");
        assertNull(TimeHelper.parseDateTimeFromSessionsForm(testDate, "invalid time"));

        ______TS("fractional time");
        assertNull(TimeHelper.parseDateTimeFromSessionsForm(testDate, "5.5"));

        ______TS("invalid date");
        assertNull(TimeHelper.parseDateTimeFromSessionsForm("invalid date", testDate));
    }

    @Test
    public void testEndOfYearDates() {
        LocalDateTime date = LocalDateTime.of(2015, Month.DECEMBER, 30, 12, 0);
        assertEquals("Wed, 30 Dec, 2015", TimeHelper.formatDateForSessionsForm(date));
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON", TimeHelper.formatDateTimeForDisplay(date));
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON UTC", TimeHelper.formatDateTimeForDisplay(
                date.atZone(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC")));
        assertEquals("30 Dec 12:00 NOON", TimeHelper.formatDateTimeForInstructorHomePage(date));
    }

    @Test
    public void testFormatDateForInstructorPages() {
        ZoneId zoneId = ZoneId.of("UTC");
        Instant instant = LocalDateTime.of(2018, Month.AUGUST, 18, 0, 0).atZone(zoneId).toInstant();
        assertEquals("18 Aug 2018", TimeHelper.formatDateForInstructorPages(instant, zoneId));
        assertEquals("", TimeHelper.formatDateForInstructorPages(null, zoneId));
    }

    @Test
    public void testFormatDateTimeForDisplay() {
        ZoneId zoneId = ZoneId.of("UTC");
        Instant instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 12, 0).atZone(zoneId).toInstant();
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC", TimeHelper.formatDateTimeForDisplay(instant, zoneId));

        zoneId = ZoneId.of("Asia/Singapore");
        instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 16, 0).atZone(zoneId).toInstant();
        assertEquals("Mon, 30 Nov 2015, 04:00 PM SGT", TimeHelper.formatDateTimeForDisplay(instant, zoneId));

        instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 4, 0).atZone(zoneId).toInstant();
        assertEquals("Mon, 30 Nov 2015, 04:00 AM SGT", TimeHelper.formatDateTimeForDisplay(instant, zoneId));
    }

}
