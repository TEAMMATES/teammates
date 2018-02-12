package teammates.test.cases.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.testng.annotations.Test;

import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link TimeHelper}.
 */
public class TimeHelperTest extends BaseTestCase {

    @Test
    public void testFormatTimeForEvaluation() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        c.set(Calendar.HOUR_OF_DAY, 9);
        assertEquals(9, TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));

        c.set(Calendar.HOUR_OF_DAY, 22);
        c.set(Calendar.MINUTE, 59);
        assertEquals(22, TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));

        //special cases that returns 24

        c.set(Calendar.HOUR_OF_DAY, 0);
        assertEquals(24, TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));

        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        assertEquals(24, TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));

    }

    @Test
    public void testCombineDateTime() {
        String testDate = "Fri, 01 Feb, 2013";
        String testTime = "0";
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(2013, 1, 1, 0, 0, 0);
        Date expectedOutput = cal.getTime();

        testTime = "0";
        ______TS("boundary case: time = 0");
        assertEquals(expectedOutput, TimeHelper.combineDateTime(testDate, testTime));

        ______TS("boundary case: time = 24");
        testTime = "24";
        cal.clear();
        cal.set(2013, 1, 1, 23, 59);
        expectedOutput = cal.getTime();
        assertEquals(expectedOutput, TimeHelper.combineDateTime(testDate, testTime));

        ______TS("negative time");
        cal.clear();
        cal.set(2013, 1, 1, -5, 0);
        expectedOutput = cal.getTime();
        assertEquals(expectedOutput, TimeHelper.combineDateTime(testDate, "-5"));

        ______TS("large time");
        cal.clear();
        cal.set(2013, 1, 1, 68, 0);
        expectedOutput = cal.getTime();
        assertEquals(expectedOutput, TimeHelper.combineDateTime(testDate, "68"));

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
    public void testIsWithinHour() {
        Calendar timeCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        ______TS("Time within past hour");

        timeCalendar.add(Calendar.MINUTE, -10);
        assertTrue(TimeHelper.isWithinPastHour(timeCalendar.getTime(),
                   Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime()));

        ______TS("End time not within past hour");

        timeCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        timeCalendar.add(Calendar.MINUTE, -70);
        assertFalse(TimeHelper.isWithinPastHour(timeCalendar.getTime(),
                    Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime()));

        timeCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        timeCalendar.add(Calendar.MINUTE, 10);
        assertFalse(TimeHelper.isWithinPastHour(timeCalendar.getTime(),
                    Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime()));

        timeCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        timeCalendar.add(Calendar.MINUTE, 10);
        assertFalse(TimeHelper.isWithinPastHour(timeCalendar.getTime(), timeCalendar.getTime()));

        ______TS("Session ended but grace time left");
        int gracePeriod = 15;
        timeCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        timeCalendar.add(Calendar.MINUTE, -10);
        assertFalse(TimeHelper.isWithinPastHour(new Date(timeCalendar.getTime().getTime() + gracePeriod * 60000L),
                    Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime()));
    }

    @Test
    public void testIsTimeWithinPeriod() {
        Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar endCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar timeCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        // Set start time to 5 days before today and end time to 5 days after today
        startCalendar.add(Calendar.DAY_OF_MONTH, -5);
        endCalendar.add(Calendar.DAY_OF_MONTH, 5);

        Date startTime = startCalendar.getTime();
        Date endTime = endCalendar.getTime();
        Date time;

        ______TS("Time within period test");
        time = timeCalendar.getTime();

        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, true));
        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, false));
        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, true));
        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, false));

        ______TS("Time on start time test");
        timeCalendar = startCalendar;
        time = timeCalendar.getTime();

        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, true));
        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, false));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, false));

        ______TS("Time before start time test");
        timeCalendar.add(Calendar.DAY_OF_MONTH, -10);
        time = timeCalendar.getTime();

        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, false));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, false));

        ______TS("Time on end time test");
        timeCalendar = endCalendar;
        time = timeCalendar.getTime();

        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, false));
        assertTrue(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, false));

        ______TS("Time after start time test");
        timeCalendar.add(Calendar.DAY_OF_MONTH, 10);
        time = timeCalendar.getTime();

        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, true, false));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, time, false, false));

        ______TS("Start time null test");
        assertFalse(TimeHelper.isTimeWithinPeriod(null, endTime, time, true, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(null, endTime, time, true, false));
        assertFalse(TimeHelper.isTimeWithinPeriod(null, endTime, time, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(null, endTime, time, false, false));

        ______TS("End time null test");
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, null, time, true, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, null, time, true, false));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, null, time, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, null, time, false, false));

        ______TS("Time null test");
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, null, true, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, null, true, false));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, null, false, true));
        assertFalse(TimeHelper.isTimeWithinPeriod(startTime, endTime, null, false, false));
    }

    @Test
    public void testEndOfYearDates() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(2015, 11, 30, 12, 0, 0);
        Date date = cal.getTime();
        assertEquals("30/12/2015", TimeHelper.formatDate(date));
        assertEquals("Wed, 30 Dec, 2015", TimeHelper.formatDateForSessionsForm(date));
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON", TimeHelper.formatTime12H(date));
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON UTC+0000", TimeHelper.formatDateTimeForSessions(date, 0));
        assertEquals("30 Dec 12:00 NOON", TimeHelper.formatDateTimeForInstructorHomePage(date));
    }

    @Test
    public void testFormatDateTimeForSessions() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(2015, 10, 30, 12, 0, 0);
        Date date = cal.getTime();
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC+0000", TimeHelper.formatDateTimeForSessions(date, 0));

        cal.clear();
        cal.set(2015, 10, 30, 4, 0, 0);
        date = cal.getTime();
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC+0800", TimeHelper.formatDateTimeForSessions(date, 8));

        cal.clear();
        cal.set(2015, 10, 30, 4, 0, 0);
        date = cal.getTime();
        assertEquals("Mon, 30 Nov 2015, 04:00 PM UTC+1200", TimeHelper.formatDateTimeForSessions(date, 12));

        cal.clear();
        cal.set(2015, 10, 30, 16, 0, 0);
        date = cal.getTime();
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC-0400", TimeHelper.formatDateTimeForSessions(date, -4));

        cal.clear();
        cal.set(2015, 10, 30, 16, 0, 0);
        date = cal.getTime();
        assertEquals("Mon, 30 Nov 2015, 11:45 AM UTC-0415", TimeHelper.formatDateTimeForSessions(date, -4.25));
    }

}
