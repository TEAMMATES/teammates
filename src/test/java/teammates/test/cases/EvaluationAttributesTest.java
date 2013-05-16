package teammates.test.cases;

import static org.testng.AssertJUnit.*;
import static teammates.common.Common.EOL;
import static teammates.common.FieldValidator.*;

import java.util.Calendar;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.sun.xml.internal.ws.api.ha.StickyFeature;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.Evaluation;

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
		assertEquals("valid values", errorMessage, Common.toString(e.getInvalidStateInfo()));

		e.course = "valid-course";
		e.name = "valid name";
		e.instructions = "valid instructions";
		assertTrue("valid, minimal properties", e.isValid());


		assertEquals("valid values", true, e.isValid());
		
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
		assertTrue(e.isValid());
		
		// FAIL : end before start
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.startTime = Common.getDateOffsetToCurrentTime(2);
		assertFalse(e.isValid());
		assertEquals(EvaluationAttributes.ERROR_END_BEFORE_START, 
				Common.toString(e.getInvalidStateInfo()));

		// FAIL : published before endtime: invalid
		e.published = true;
		e.startTime = Common.getDateOffsetToCurrentTime(0);
		e.endTime = Common.getMsOffsetToCurrentTime(5);
		assertFalse(e.isValid());
		assertEquals(EvaluationAttributes.ERROR_PUBLISHED_BEFORE_END,
				Common.toString(e.getInvalidStateInfo()));

		// SUCCESS : just after endtime and published: valid
		e.startTime = Common.getDateOffsetToCurrentTime(-1);
		e.endTime = Common.getMsOffsetToCurrentTime(-5);
		e.published = true;
		assertTrue(e.isValid());

		// FAIL : activated before start time: invalid
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = Common.getDateOffsetToCurrentTime(2);
		e.published = false;
		e.activated = true;
		assertFalse(e.isValid());
		assertEquals(EvaluationAttributes.ERROR_ACTIVATED_BEFORE_START,
			Common.toString(e.getInvalidStateInfo()));
	}
	
	@Test
	public void testGetInvalidStateInfo(){
	    //already tested in testValidate() above
	}
	
	@Test
	public void testIsValid(){
		//already tested in testValidate() above
	}
	
	@Test
	public void testIsReady() throws InvalidParametersException {

		// Create evaluation object to use as the test object
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		EvaluationAttributes e = new EvaluationAttributes(
				new Evaluation("course1", "evalution 1", "instructions",
				true, start.getTime(), end.getTime(), 0.0, 0));
		int oneSecInMilliSeconds = 1 * 1000;
		double timeZone = 0.0;
		
		______TS("ready, just after start time");

		// start time set to 1 sec before current time
		e.startTime = Common.getMsOffsetToCurrentTime(-oneSecInMilliSeconds);
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.activated = false;
		e.timeZone = 0.0;
		assertEquals(true, e.isReadyToActivate());
		
		// negative time zone, starting just before current time
		timeZone = -2.0;
		e.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(-oneSecInMilliSeconds,timeZone);
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.activated = false;
		e.timeZone = timeZone;
		assertEquals(true, e.isReadyToActivate());
		
		
		// positive time zone, starting just before current time
		timeZone = 2.0;
		e.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(-oneSecInMilliSeconds, timeZone);
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.activated = false;
		e.timeZone = timeZone;
		assertEquals(true, e.isReadyToActivate());
		
		______TS("not ready, just before start time");
		//start time set to 1 sec after current time
		oneSecInMilliSeconds = 1 * 1000;
		e.startTime = Common.getMsOffsetToCurrentTime(+oneSecInMilliSeconds);
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.activated = false;
		e.timeZone = 0.0;
		assertEquals(false, e.isReadyToActivate());

		// negative time zone, starting just after current time
		timeZone = -2.0;
		e.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(oneSecInMilliSeconds, timeZone);
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.activated = false;
		e.timeZone = timeZone;
		assertEquals(false, e.isReadyToActivate());
		
		// positive time zone, starting just after current time
		timeZone = 2.0;
		e.startTime = Common.getMsOffsetToCurrentTimeInUserTimeZone(oneSecInMilliSeconds, timeZone);
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.activated = false;
		e.timeZone = timeZone;
		assertEquals(false, e.isReadyToActivate());
		
		______TS("not ready, already activated");
		
		// start time set to 1 sec before current time
		e.startTime = Common.getMsOffsetToCurrentTime(-oneSecInMilliSeconds);
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.activated = true;
		e.timeZone = 0.0;
		assertEquals(false, e.isReadyToActivate());

		// start time set to 1 sec after current time
		e.startTime = Common.getMsOffsetToCurrentTime(+oneSecInMilliSeconds);
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.activated = true;
		e.timeZone = 0.0;
		assertEquals(false, e.isReadyToActivate());

	}
	
	@Test
	public void testToString() {
		EvaluationAttributes e ;
		e = generateValidEvaluationAttributesObject();

		String inStringFormat = Common.getTeammatesGson().toJson(e,
				EvaluationAttributes.class);
		assertEquals(inStringFormat, e.toString());

	}

	public static EvaluationAttributes generateValidEvaluationAttributesObject() {
		EvaluationAttributes e;
		e = new EvaluationAttributes();

		e.course = "valid-course";
		e.name = "valid name";
		e.instructions = "1st line of instructions \n 2nd line of instructions";
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = Common.getDateOffsetToCurrentTime(2);
		e.activated = false;
		e.published = false;
		e.timeZone = 0.0;
		e.gracePeriod = 5;
		e.p2pEnabled = true;
		return e;
	}
	
}
