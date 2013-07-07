package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Calendar;
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
		
		c.set(Calendar.HOUR_OF_DAY, 14);
		assertEquals("14", TimeHelper.convertToOptionValueInTimeDropDown(c.getTime()));
		
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

}
