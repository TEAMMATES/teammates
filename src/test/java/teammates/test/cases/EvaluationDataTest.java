package teammates.test.cases;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.EvaluationData.EvalStatus;

public class EvaluationDataTest extends BaseTestCase {

	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(EvaluationData.class);
	}

	@Test
	public void testCalculateEvalStatus() throws InterruptedException {
		
		double timeZone;
		int gracePeriod;
		EvaluationData evaluation = new EvaluationData();
		int safetyMargin = 1000; // we use this to compensate for test execution

		______TS("in the awaiting period");

		evaluation.startTime = Common.getMsOffsetToCurrentTime(safetyMargin);
		evaluation.endTime = Common.getDateOffsetToCurrentTime(1);

		timeZone = 0.0;
		evaluation.timeZone = timeZone;

		gracePeriod = 0;
		;
		evaluation.gracePeriod = gracePeriod;

		evaluation.published = false;
		assertEquals(EvalStatus.AWAITING, evaluation.getStatus());

		______TS("in the middle of open period");

		evaluation.startTime = Common.getDateOffsetToCurrentTime(-1);
		assertEquals(EvalStatus.OPEN, evaluation.getStatus());

		______TS("just before grace period expires");

		gracePeriod = 5;
		int gracePeriodInMs = gracePeriod * 60 * 1000;
		evaluation.gracePeriod = gracePeriod;
		
		evaluation.endTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				gracePeriodInMs - safetyMargin, timeZone);
		
		timeZone = 0.0;
		evaluation.timeZone = timeZone;
		
		assertEquals(EvalStatus.OPEN, evaluation.getStatus());

		______TS("just after the grace period expired");

		gracePeriod = 5;
		gracePeriodInMs = gracePeriod * 60 * 1000;
		evaluation.gracePeriod = gracePeriod;
		
		timeZone = 0.0;
		evaluation.timeZone = timeZone;
		
		evaluation.endTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				- gracePeriodInMs - safetyMargin, timeZone);
		
		assertEquals(EvalStatus.CLOSED, evaluation.getStatus());

		______TS("already published");
		
		evaluation.published = true;
		assertEquals(EvalStatus.PUBLISHED, evaluation.getStatus());
		evaluation.published = false;

		______TS("checking for user in different time zone");
		// do similar testing for +1.0 time zone
		
		timeZone = 1.0;
		evaluation.timeZone = timeZone;

		// in AWAITING period
		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				safetyMargin, timeZone);
		evaluation.endTime = Common.getDateOffsetToCurrentTime(1);
		assertEquals(EvalStatus.AWAITING, evaluation.getStatus());

		// in OPEN period
		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				-safetyMargin, timeZone);
		assertEquals(EvalStatus.OPEN, evaluation.getStatus());

		//TODO: just before grace period expired
		
		gracePeriod = 5;
		evaluation.gracePeriod = gracePeriod;

		timeZone = 1.0;
		evaluation.timeZone = timeZone;

		evaluation.startTime = Common.getDateOffsetToCurrentTime(-2);
		evaluation.endTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				- gracePeriodInMs + safetyMargin, timeZone);

		assertEquals(EvalStatus.OPEN, evaluation.getStatus());
		
		// just after grace period
		gracePeriod = 5;
		evaluation.gracePeriod = gracePeriod;

		timeZone = 1.0;
		evaluation.timeZone = timeZone;

		evaluation.startTime = Common.getDateOffsetToCurrentTime(-2);
		evaluation.endTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				- gracePeriodInMs - safetyMargin, timeZone);

		assertEquals(EvalStatus.CLOSED, evaluation.getStatus());

		// already PUBLISHED
		evaluation.published = true;
		assertEquals(EvalStatus.PUBLISHED, evaluation.getStatus());
	}

	@Test
	public void testValidate() {
		EvaluationData e = new EvaluationData();

		e.course = "valid-course";
		e.name = "valid name";
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = Common.getDateOffsetToCurrentTime(2);

		// minimal properties, still valid
		assertTrue(e.getInvalidStateInfo(), e.isValid());

		e.activated = false;
		e.published = false;
		e.instructions = "valid instructions";
		e.timeZone = 0.0;
		e.gracePeriod = 5;
		e.p2pEnabled = true;

		// other properties added, still valid
		assertTrue(e.getInvalidStateInfo(),e.isValid());

		// no course: invalid
		e.course = null;
		assertFalse(e.isValid());
		assertEquals(e.getInvalidStateInfo(), EvaluationData.ERROR_FIELD_COURSE);
		
		// no name: invalid
		e.course = "valid-course";
		e.name = null;
		assertFalse(e.isValid());
		assertEquals(e.getInvalidStateInfo(), EvaluationData.ERROR_FIELD_NAME);
		
		// no start time: invalid
		e.name = "valid name";
		e.startTime = null;
		assertFalse(e.isValid());
		assertEquals(e.getInvalidStateInfo(), EvaluationData.ERROR_FIELD_STARTTIME);
		
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = null;
		assertFalse(e.isValid());
		assertEquals(e.getInvalidStateInfo(), EvaluationData.ERROR_FIELD_ENDTIME);
		
		// end before start: invalid
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.startTime = Common.getDateOffsetToCurrentTime(2);
		print(Common.calendarToString(Common
				.dateToCalendar(e.startTime)));
		print(Common.calendarToString(Common
				.dateToCalendar(e.endTime)));
		assertFalse(e.isValid());
		assertEquals(e.getInvalidStateInfo(), EvaluationData.ERROR_END_BEFORE_START);

		// published before endtime: invalid
		e.published = true;
		e.startTime = Common.getDateOffsetToCurrentTime(0);
		e.endTime = Common.getMsOffsetToCurrentTime(5);
		assertFalse(e.isValid());
		assertEquals(e.getInvalidStateInfo(), EvaluationData.ERROR_PUBLISHED_BEFORE_END);

		// just after endtime and published: valid
		e.startTime = Common.getDateOffsetToCurrentTime(-1);
		e.endTime = Common.getMsOffsetToCurrentTime(-5);
		e.published = true;
		assertTrue(e.getInvalidStateInfo(), e.isValid());

		// activated before start time: invalid
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = Common.getDateOffsetToCurrentTime(2);
		e.published = false;
		e.activated = true;
		assertFalse(e.isValid());
		assertEquals(e.getInvalidStateInfo(), EvaluationData.ERROR_ACTIVATED_BEFORE_START);
	}
	
	@Test
	public void testGetInvalidStateInfo(){
	    //already tested in testValidate() above
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(EvaluationData.class);
	}
}
