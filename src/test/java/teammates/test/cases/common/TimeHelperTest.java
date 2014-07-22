package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.testng.annotations.Test;

import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;

public class TimeHelperTest extends BaseTestCase {
    
    @Test
    public void testFormatTimeForEvaluation(){
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        
        c.set(Calendar.HOUR_OF_DAY, 9);
        assertEquals("9", TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));
        
        c.set(Calendar.HOUR_OF_DAY, 22);
        c.set(Calendar.MINUTE, 59);
        assertEquals("22", TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));
        
        //special cases that returns 24
        
        c.set(Calendar.HOUR_OF_DAY, 0);
        assertEquals("24", TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));
        
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        assertEquals("24", TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));
        
    }
    
    @Test
    public void testCombineDateTime() throws ParseException{
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
    public void testConvertTimeStringToLongString(){
       
      
       ______TS("typical case: 2 fields with units, no value missing"); 
       
       String timeString = "1 s  223 ms";
       assertEquals(1223, TimeHelper.convertTimeToMillis(timeString));
       
       timeString = "1 s 3 ms";
       assertEquals(1003, TimeHelper.convertTimeToMillis(timeString));
       
       timeString = "0 s 3 ms";
       assertEquals(3, TimeHelper.convertTimeToMillis(timeString));
       
       ______TS("typical case: 2 fields with units, some values missing"); 
      
       timeString = " s 3 ms";
       assertEquals(3, TimeHelper.convertTimeToMillis(timeString));
       
       ______TS("typical case: 2 fields with units, over than 60 seconds"); 
       
       timeString = " 600s 3 ms";
       assertEquals(60000, TimeHelper.convertTimeToMillis(timeString));
       
       
       ______TS("typical case: 1 fields with unit, no value missing"); 
       
       timeString = "1 s";
       assertEquals(1000, TimeHelper.convertTimeToMillis(timeString));
       
       timeString = "1000 ms";
       assertEquals(1000, TimeHelper.convertTimeToMillis(timeString));
       
       ______TS("typical case: 1 fields with unit, but value is missing"); 
       
       timeString = " ms";
       assertEquals(0, TimeHelper.convertTimeToMillis(timeString));
       
       ______TS("typical case: 1 fields with unit, over 60 seconds"); 
       
       timeString = " 10000000ms";
       assertEquals(60000, TimeHelper.convertTimeToMillis(timeString));
       
       
       ______TS("case: invalid format");
       
       timeString = "rubbish vlaue";
       assertEquals(0, TimeHelper.convertTimeToMillis(timeString));
       
       timeString = "";
       assertEquals(0, TimeHelper.convertTimeToMillis(timeString));
       
       ______TS("case: invalid format, unrecognized unit");
       
       timeString = "1 min";
       assertEquals(0, TimeHelper.convertTimeToMillis(timeString));
       
       timeString = "1 min 2ms";
       assertEquals(2, TimeHelper.convertTimeToMillis(timeString));
       
       ______TS("case: invalid format, unit missing");
       
       timeString = "1: 3 ms";
       assertEquals(3, TimeHelper.convertTimeToMillis(timeString));
     
       timeString = "1s: 3";
       assertEquals(1000, TimeHelper.convertTimeToMillis(timeString));
     
       timeString = "1 : 3";
       assertEquals(0, TimeHelper.convertTimeToMillis(timeString));
     
       timeString = "1 :   1   : 223";
       assertEquals(0, TimeHelper.convertTimeToMillis(timeString));
             
       timeString = "100000";
       assertEquals(0, TimeHelper.convertTimeToMillis(timeString));
       
    }
    
    
    
    
}
