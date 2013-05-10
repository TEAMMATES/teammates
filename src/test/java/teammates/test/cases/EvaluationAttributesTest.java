package teammates.test.cases;

import static teammates.common.FieldValidator.EVALUATION_NAME_ERROR_MESSAGE;
import static teammates.common.FieldValidator.REASON_TOO_LONG;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.AssertJUnit;
import static org.testng.AssertJUnit.*;
import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;

public class EvaluationAttributesTest extends BaseTestCase {

	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(EvaluationAttributes.class);
	}

	@Test
	public void testCalculateEvalStatus() throws InterruptedException {
		
		double timeZone;
		int gracePeriod;
		EvaluationAttributes evaluation = new EvaluationAttributes();
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

		e.course = "valid-course";
		e.name = "valid name";
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = Common.getDateOffsetToCurrentTime(2);

		// minimal properties, still valid
		AssertJUnit.assertTrue(e.getInvalidStateInfo(), e.isValid());

		e.activated = false;
		e.published = false;
		e.instructions = "valid instructions";
		e.timeZone = 0.0;
		e.gracePeriod = 5;
		e.p2pEnabled = true;

		// SUCCESS : other properties added, still valid
		AssertJUnit.assertTrue(e.getInvalidStateInfo(),e.isValid());

		// FAIL : no course: invalid
		e.course = null;
		try {
			e.getInvalidStateInfo();
			throw new RuntimeException("Assumption violation not detected");
		} catch (AssertionError e1) {
			assertTrue(true);
		}
		
		// FAIL : no name: invalid
		e.course = "valid-course";
		e.name = null;
		try {
			e.getInvalidStateInfo();
			throw new RuntimeException("Assumption violation not detected");
		} catch (AssertionError e1) {
			assertTrue(true);
		}
		
		
		// SUCCESS : name at max length
		e.name = Common.generateStringOfLength(EvaluationAttributes.EVALUATION_NAME_MAX_LENGTH);
		AssertJUnit.assertTrue(e.isValid());
		
		// FAIL : name too long
		e.name += "e";
		AssertJUnit.assertFalse(e.isValid());
		AssertJUnit.assertEquals(
				String.format(EVALUATION_NAME_ERROR_MESSAGE, e.name, REASON_TOO_LONG),
				e.getInvalidStateInfo());
		
		// FAIL : no start time
		e.name = "valid name";
		e.startTime = null;
		AssertJUnit.assertFalse(e.isValid());
		AssertJUnit.assertEquals(e.getInvalidStateInfo(), EvaluationAttributes.ERROR_FIELD_STARTTIME);
		
		// FAIL : no end time
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = null;
		AssertJUnit.assertFalse(e.isValid());
		AssertJUnit.assertEquals(e.getInvalidStateInfo(), EvaluationAttributes.ERROR_FIELD_ENDTIME);
		
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

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(EvaluationAttributes.class);
	}
}
