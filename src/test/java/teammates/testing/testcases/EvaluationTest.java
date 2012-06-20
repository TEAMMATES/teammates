package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.api.InvalidParametersException;
import teammates.persistent.Evaluation;

public class EvaluationTest extends BaseTestCase {
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(Evaluation.class);
	}

	@Test
	public void testIsReady() throws InvalidParametersException {

		// Create evaluation object to use as the test object
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		Evaluation e = new Evaluation("course1", "evalution 1", "instructions",
				true, start.getTime(), end.getTime(), 0.0, 0);
		int oneSecInMilliSeconds = 1 * 1000;
		double timeZone = 0.0;
		
		______TS("ready, just after start time");

		// start time set to 1 sec before current time
		e.setStart(Common.getMsOffsetToCurrentTime(-oneSecInMilliSeconds));
		e.setDeadline(Common.getDateOffsetToCurrentTime(1));
		e.setActivated(false);
		e.setTimeZone(0.0);
		assertEquals(true, e.isReady());
		
		// negative time zone, starting just before current time
		timeZone = -2.0;
		e.setStart(Common.getMsOffsetToCurrentTimeInUserTimeZone(-oneSecInMilliSeconds,timeZone));
		e.setDeadline(Common.getDateOffsetToCurrentTime(1));
		e.setActivated(false);
		e.setTimeZone(timeZone);
		assertEquals(true, e.isReady());
		
		
		// positive time zone, starting just before current time
		timeZone = 2.0;
		e.setStart(Common.getMsOffsetToCurrentTimeInUserTimeZone(-oneSecInMilliSeconds, timeZone));
		e.setDeadline(Common.getDateOffsetToCurrentTime(1));
		e.setActivated(false);
		e.setTimeZone(timeZone);
		assertEquals(true, e.isReady());
		
		______TS("not ready, just before start time");
		//start time set to 1 sec after current time
		oneSecInMilliSeconds = 1 * 1000;
		e.setStart(Common.getMsOffsetToCurrentTime(+oneSecInMilliSeconds));
		e.setDeadline(Common.getDateOffsetToCurrentTime(1));
		e.setActivated(false);
		e.setTimeZone(0.0);
		assertEquals(false, e.isReady());

		// negative time zone, starting just after current time
		timeZone = -2.0;
		e.setStart(Common.getMsOffsetToCurrentTimeInUserTimeZone(oneSecInMilliSeconds, timeZone));
		e.setDeadline(Common.getDateOffsetToCurrentTime(1));
		e.setActivated(false);
		e.setTimeZone(timeZone);
		assertEquals(false, e.isReady());
		
		// positive time zone, starting just after current time
		timeZone = 2.0;
		e.setStart(Common.getMsOffsetToCurrentTimeInUserTimeZone(oneSecInMilliSeconds, timeZone));
		e.setDeadline(Common.getDateOffsetToCurrentTime(1));
		e.setActivated(false);
		e.setTimeZone(timeZone);
		assertEquals(false, e.isReady());
		
		______TS("not ready, already activated");
		
		// start time set to 1 sec before current time
		e.setStart(Common.getMsOffsetToCurrentTime(-oneSecInMilliSeconds));
		e.setDeadline(Common.getDateOffsetToCurrentTime(1));
		e.setActivated(true);
		e.setTimeZone(0.0);
		assertEquals(false, e.isReady());

		// start time set to 1 sec after current time
		e.setStart(Common.getMsOffsetToCurrentTime(+oneSecInMilliSeconds));
		e.setDeadline(Common.getDateOffsetToCurrentTime(1));
		e.setActivated(true);
		e.setTimeZone(0.0);
		assertEquals(false, e.isReady());

	}
	


	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassHeader();
		turnLoggingDown(Evaluation.class);
	}

}
