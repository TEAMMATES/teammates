package teammates.test.cases.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link TimeHelper}.
 */
public class TimeHelperTest extends BaseTestCase {

    @Test
    public void testCombineDateTime() {
        String testDate = "Fri, 01 Feb, 2013";
        String testTime = "0";
        LocalDateTime expectedOutput = LocalDateTime.of(2013, 2, 1, 0, 0);

        testTime = "0";
        ______TS("boundary case: time = 0");
        assertEquals(expectedOutput, TimeHelper.combineDateTime(testDate, testTime));

        ______TS("boundary case: time = 24");
        testTime = "24";
        expectedOutput = LocalDateTime.of(2013, 2, 1, 23, 59);
        assertEquals(expectedOutput, TimeHelper.combineDateTime(testDate, testTime));

        ______TS("negative time");
        assertNull(TimeHelper.combineDateTime(testDate, "-5"));

        ______TS("large time");
        assertNull(TimeHelper.combineDateTime(testDate, "68"));

        ______TS("date null");
        assertNull(TimeHelper.combineDateTime(null, testTime));

        ______TS("time null");
        assertNull(TimeHelper.combineDateTime(testDate, null));

        ______TS("invalid time");
        assertNull(TimeHelper.combineDateTime(testDate, "invalid time"));

        ______TS("fractional time");
        assertNull(TimeHelper.combineDateTime(testDate, "5.5"));

        ______TS("invalid date");
        assertNull(TimeHelper.combineDateTime("invalid date", testDate));
    }

    @Test
    public void testEndOfYearDates() {
        LocalDateTime ldt = LocalDateTime.of(2015, Month.DECEMBER, 30, 12, 0);
        assertEquals("30/12/2015", TimeHelper.formatDate(ldt));
        assertEquals("Wed, 30 Dec, 2015", TimeHelper.formatDateForSessionsForm(ldt));
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON", TimeHelper.formatTime12H(ldt));
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON UTC+0000",
                TimeHelper.formatDateTimeForSessions(ldt.toInstant(ZoneOffset.UTC), ZoneId.of("UTC")));
        assertEquals("30 Dec 12:00 NOON", TimeHelper.formatDateTimeForInstructorHomePage(ldt));
    }

    @Test
    public void testFormatDateTimeForSessions() {
        Instant i1 = LocalDateTime.of(2015, Month.NOVEMBER, 30, 12, 0).toInstant(ZoneOffset.UTC);
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC+0000", TimeHelper.formatDateTimeForSessions(i1, ZoneId.of("UTC")));

        Instant i2 = LocalDateTime.of(2015, Month.NOVEMBER, 30, 4, 0).toInstant(ZoneOffset.UTC);
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC+0800", TimeHelper.formatDateTimeForSessions(i2, ZoneId.of("+08:00")));
        assertEquals("Mon, 30 Nov 2015, 04:00 PM UTC+1200", TimeHelper.formatDateTimeForSessions(i2, ZoneId.of("+12:00")));

        Instant i3 = LocalDateTime.of(2015, Month.NOVEMBER, 30, 16, 0).toInstant(ZoneOffset.UTC);
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC-0400", TimeHelper.formatDateTimeForSessions(i3, ZoneId.of("-04:00")));
        assertEquals("Mon, 30 Nov 2015, 11:45 AM UTC-0415", TimeHelper.formatDateTimeForSessions(i3, ZoneId.of("-04:15")));
    }

    @Test
    public void testConvertLocalDateToUtc() {
        long offsetHours = 8;

        ______TS("typical time");
        Instant now = Instant.now();
        Date localDate = Date.from(now);
        Date utcDate = Date.from(now.minus(Duration.ofHours(offsetHours)));
        assertEquals(utcDate, TimeHelper.convertLocalDateToUtc(localDate, offsetHours));

        ______TS("special time");
        localDate = Date.from(Const.TIME_REPRESENTS_FOLLOW_OPENING);
        assertEquals(localDate, TimeHelper.convertLocalDateToUtc(localDate, offsetHours));
        localDate = Date.from(Const.TIME_REPRESENTS_FOLLOW_VISIBLE);
        assertEquals(localDate, TimeHelper.convertLocalDateToUtc(localDate, offsetHours));
        localDate = Date.from(Const.TIME_REPRESENTS_NEVER);
        assertEquals(localDate, TimeHelper.convertLocalDateToUtc(localDate, offsetHours));
        localDate = Date.from(Const.TIME_REPRESENTS_LATER);
        assertEquals(localDate, TimeHelper.convertLocalDateToUtc(localDate, offsetHours));
        localDate = Date.from(Const.TIME_REPRESENTS_NOW);
        assertEquals(localDate, TimeHelper.convertLocalDateToUtc(localDate, offsetHours));

        ______TS("null time");
        assertNull(TimeHelper.convertLocalDateToUtc(null, offsetHours));
    }

}
