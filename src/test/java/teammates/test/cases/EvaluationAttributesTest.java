package teammates.test.cases;

import static org.testng.AssertJUnit.*;
import static teammates.common.Common.EOL;
import static teammates.common.FieldValidator.*;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;

public class EvaluationAttributesTest extends BaseTestCase {


	@Test
	public void testCalculateEvalStatus() throws InterruptedException {
		
		double timeZone;
		int gracePeriod;
		EvaluationAttributes evaluation = new EvaluationAttributes();
		int safetyMargin = 1000; // we use this to compensate for test execution time

		______TS("in the awaiting period");

		evaluation.startTime = Common.getMsOffsetToCurrentTime(safetyMargin);
		evaluation.endTime = Common.getDateOffsetToCurrentTime(1);

		timeZone = 0.0;
		evaluation.timeZone = timeZone;

		gracePeriod = 0;
		;
		evaluation.gracePeriod = gracePeriod;

		evaluation.published = false;
		AssertJUnit.assertEquals(EvalStatus.AWAITING, evaluation.getStatus());

		______TS("in the middle of open period");

		evaluation.startTime = Common.getDateOffsetToCurrentTime(-1);
		AssertJUnit.assertEquals(EvalStatus.OPEN, evaluation.getStatus());

		______TS("just before grace period expires");

		gracePeriod = 5;
		int gracePeriodInMs = gracePeriod * 60 * 1000;
		evaluation.gracePeriod = gracePeriod;
		
		evaluation.endTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				gracePeriodInMs - safetyMargin, timeZone);
		
		timeZone = 0.0;
		evaluation.timeZone = timeZone;
		
		AssertJUnit.assertEquals(EvalStatus.OPEN, evaluation.getStatus());

		______TS("just after the grace period expired");

		gracePeriod = 5;
		gracePeriodInMs = gracePeriod * 60 * 1000;
		evaluation.gracePeriod = gracePeriod;
		
		timeZone = 0.0;
		evaluation.timeZone = timeZone;
		
		evaluation.endTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				- gracePeriodInMs - safetyMargin, timeZone);
		
		AssertJUnit.assertEquals(EvalStatus.CLOSED, evaluation.getStatus());

		______TS("already published");
		
		evaluation.published = true;
		AssertJUnit.assertEquals(EvalStatus.PUBLISHED, evaluation.getStatus());
		evaluation.published = false;

		______TS("checking for user in different time zone");
		// do similar testing for +1.0 time zone
		
		timeZone = 1.0;
		evaluation.timeZone = timeZone;

		// in AWAITING period
		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				safetyMargin, timeZone);
		evaluation.endTime = Common.getDateOffsetToCurrentTime(1);
		AssertJUnit.assertEquals(EvalStatus.AWAITING, evaluation.getStatus());

		// in OPEN period
		evaluation.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				-safetyMargin, timeZone);
		AssertJUnit.assertEquals(EvalStatus.OPEN, evaluation.getStatus());

		//TODO: just before grace period expired
		
		gracePeriod = 5;
		evaluation.gracePeriod = gracePeriod;

		timeZone = 1.0;
		evaluation.timeZone = timeZone;

		evaluation.startTime = Common.getDateOffsetToCurrentTime(-2);
		evaluation.endTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				- gracePeriodInMs + safetyMargin, timeZone);

		AssertJUnit.assertEquals(EvalStatus.OPEN, evaluation.getStatus());
		
		// just after grace period
		gracePeriod = 5;
		evaluation.gracePeriod = gracePeriod;

		timeZone = 1.0;
		evaluation.timeZone = timeZone;

		evaluation.startTime = Common.getDateOffsetToCurrentTime(-2);
		evaluation.endTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(
				- gracePeriodInMs - safetyMargin, timeZone);

		AssertJUnit.assertEquals(EvalStatus.CLOSED, evaluation.getStatus());

		// already PUBLISHED
		evaluation.published = true;
		AssertJUnit.assertEquals(EvalStatus.PUBLISHED, evaluation.getStatus());
	}

	@Test
	public void testValidate() {
		EvaluationAttributes e = new EvaluationAttributes();

		e.course = "";
		e.name = "";
		e.instructions = Common.generateStringOfLength(EVALUATION_INSTRUCTIONS_MAX_LENGTH+1);
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = Common.getDateOffsetToCurrentTime(2);
		e.activated = false;
		e.published = false;
		e.timeZone = 0.0;
		e.gracePeriod = 5;
		e.p2pEnabled = true;
		
		assertEquals("invalid values", false, e.isValid());
		String errorMessage = 
				String.format(COURSE_ID_ERROR_MESSAGE, e.course, REASON_EMPTY) + EOL 
				+ String.format(EVALUATION_NAME_ERROR_MESSAGE, e.name, REASON_EMPTY) + EOL 
				+ String.format(EVALUATION_INSTRUCTIONS_ERROR_MESSAGE, e.instructions, REASON_TOO_LONG);
		assertEquals("valid values", errorMessage, e.getInvalidStateInfo());

		e.course = "valid-course";
		e.name = "valid name";
		e.instructions = "valid instructions";
		assertTrue("valid, minimal properties", e.isValid());
		assertEquals("valid, minimal properties", "", e.getInvalidStateInfo());


		assertEquals("valid values", true, e.isValid());
		assertEquals("valid values", "", e.getInvalidStateInfo());
		
		e.startTime = null;
		try {
			e.getInvalidStateInfo();
			signalFailureToDetectAssumptionViolation("null start time not detected");
		} catch (AssertionError e1) {
			ignoreExpectedException();
		}
		
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = null;
		try {
			e.getInvalidStateInfo();
			signalFailureToDetectAssumptionViolation("null end time not detected");
		} catch (AssertionError e1) {
			ignoreExpectedException();
		}

		
		// SUCCESS : end == start
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.startTime = e.endTime;
		print(Common.calendarToString(Common
				.dateToCalendar(e.startTime)));
		print(Common.calendarToString(Common
				.dateToCalendar(e.endTime)));
		AssertJUnit.assertTrue(e.isValid());
		
		// FAIL : end before start
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.startTime = Common.getDateOffsetToCurrentTime(2);
		print(Common.calendarToString(Common
				.dateToCalendar(e.startTime)));
		print(Common.calendarToString(Common
				.dateToCalendar(e.endTime)));
		AssertJUnit.assertFalse(e.isValid());
		AssertJUnit.assertEquals(e.getInvalidStateInfo(), EvaluationAttributes.ERROR_END_BEFORE_START);

		// FAIL : published before endtime: invalid
		e.published = true;
		e.startTime = Common.getDateOffsetToCurrentTime(0);
		e.endTime = Common.getMsOffsetToCurrentTime(5);
		AssertJUnit.assertFalse(e.isValid());
		AssertJUnit.assertEquals(e.getInvalidStateInfo(), EvaluationAttributes.ERROR_PUBLISHED_BEFORE_END);

		// SUCCESS : just after endtime and published: valid
		e.startTime = Common.getDateOffsetToCurrentTime(-1);
		e.endTime = Common.getMsOffsetToCurrentTime(-5);
		e.published = true;
		AssertJUnit.assertTrue(e.getInvalidStateInfo(), e.isValid());

		// FAIL : activated before start time: invalid
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = Common.getDateOffsetToCurrentTime(2);
		e.published = false;
		e.activated = true;
		AssertJUnit.assertFalse(e.isValid());
		AssertJUnit.assertEquals(e.getInvalidStateInfo(), EvaluationAttributes.ERROR_ACTIVATED_BEFORE_START);
	}
	
	@Test
	public void testGetInvalidStateInfo(){
	    //already tested in testValidate() above
	}
	
	@Test
	public void testIsValid(){
		//already tested in testValidate() above
	}
	
}
