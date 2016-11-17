package teammates.test.cases.common;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.testng.annotations.Test;

import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;

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
        String testDate = "01/02/2013";
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
        assertFalse(TimeHelper.isWithinPastHour(new Date(timeCalendar.getTime().getTime() + gracePeriod * 60000),
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
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON", TimeHelper.formatTime12H(date));
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON UTC", TimeHelper.formatDateTimeForComments(date));
        assertEquals("30 Dec 12:00 NOON", TimeHelper.formatDateTimeForInstructorHomePage(date));
    }
    
    @Test
    public void testGetTimeZone() {
        ______TS("Time zone for Toronto (less than UTC)");
        double timeZoneToronto = -5.0;
        assertEquals(TimeZone.getTimeZone("GMT-05:00"), TimeHelper.getTimeZone(timeZoneToronto));

        ______TS("Time zone for London (UTC)");
        double timeZoneLondon = 0.0;
        assertEquals(TimeZone.getTimeZone("GMT"), TimeHelper.getTimeZone(timeZoneLondon));

        ______TS("Time zone for New Zealand, Chatham Islands (greater than UTC and not full hour)");
        double timeZoneChathamIslands = 12.75;
        assertEquals(TimeZone.getTimeZone("GMT+12:45"), TimeHelper.getTimeZone(timeZoneChathamIslands));
    }
    
    @Test
    public void testFormatTime12HForCalendar() {
        ______TS("Calendar time is morning time (AM)");
        Calendar morningTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        morningTime.clear();
        morningTime.set(2016, 10, 17, 9, 41);
        assertEquals("Thu, 17 Nov 2016, 09:41 AM", TimeHelper.formatTime12H(morningTime));

        ______TS("Calendar time is NOON");
        Calendar noonTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        noonTime.clear();
        noonTime.set(2016, 10, 16, 12, 0);
        assertEquals("Wed, 16 Nov 2016, 12:00 NOON", TimeHelper.formatTime12H(noonTime));

        ______TS("Calendar time is afternoon time (PM)");
        Calendar afternoonTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        afternoonTime.clear();
        afternoonTime.set(2016, 10, 20, 19, 15);
        assertEquals("Sun, 20 Nov 2016, 07:15 PM", TimeHelper.formatTime12H(afternoonTime));
    }
    
    @Test
    public void testConvertToUserTimeZone() {
        Calendar utcTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcTime.clear();
        utcTime.set(2015, 0, 15, 4, 15, 0);

        ______TS("Calendar time zone conversion");
        assertEquals(TimeZone.getTimeZone("GMT-9:30"),
                TimeHelper.convertToUserTimeZone(utcTime, -9.5).getTimeZone());
        assertEquals("Wed, 14 Jan 2015, 06:45 PM", TimeHelper.formatTime12H(utcTime));
        assertEquals(TimeZone.getTimeZone("GMT+4:00"),
                TimeHelper.convertToUserTimeZone(utcTime, 4.0).getTimeZone());
        assertEquals("Thu, 15 Jan 2015, 08:15 AM", TimeHelper.formatTime12H(utcTime));
    }
}
