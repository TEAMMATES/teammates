package teammates.testing.testcases;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import teammates.api.Common;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.EvaluationData.EvalStatus;

public class EvaluationDataTest extends BaseTestCase {

	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLogginUp(EvaluationData.class);
	}

	@Test
	public void testCalculateEvalStatus() throws InterruptedException {
		printTestCaseHeader();

		______TS("in the awaiting period");

		EvaluationData evaluation = new EvaluationData();
		evaluation.startTime = Common.getMilliSecondOffsetToCurrentTime(1000);
		evaluation.endTime = Common.getDateOffsetToCurrentTime(1);
		evaluation.timeZone = 0.0;
		evaluation.gracePeriod = 0;
		evaluation.published = false;
		assertEquals(EvalStatus.AWAITING, evaluation.getStatus());

		______TS("in the middle of open period");
		evaluation.startTime = Common.getMilliSecondOffsetToCurrentTime(-1);
		assertEquals(EvalStatus.OPEN, evaluation.getStatus());

		______TS("in the grace period");
		evaluation.endTime = Common.getDateOffsetToCurrentTime(0);
		evaluation.timeZone = 0.0;
		evaluation.gracePeriod = 0;
		Thread.sleep(5);
		assertEquals(EvalStatus.CLOSED, evaluation.getStatus());

		______TS("just after the grace period");

		// set it us such that grace period just expired
		int gracePeriod = 5;
		evaluation.endTime = Common
				.getMilliSecondOffsetToCurrentTime(-gracePeriod * 60 * 1000);
		evaluation.timeZone = 0.0;
		evaluation.gracePeriod = gracePeriod;
		Thread.sleep(5);
		assertEquals(EvalStatus.CLOSED, evaluation.getStatus());

		______TS("already published");
		evaluation.published = true;
		assertEquals(EvalStatus.PUBLISHED, evaluation.getStatus());

		______TS("checking for user in different time zone");
		// do similar testing for +1.0 time zone

		evaluation.published = false;
		evaluation.timeZone = 1.0;
		int timeZoneOffsetInMilliSec = 60 * 60 * 1000;

		// in AWAITING period
		evaluation.startTime = Common
				.getMilliSecondOffsetToCurrentTime(-timeZoneOffsetInMilliSec + 1000);
		evaluation.endTime = Common.getDateOffsetToCurrentTime(1);
		assertEquals(EvalStatus.AWAITING, evaluation.getStatus());

		// in OPEN period
		evaluation.startTime = Common
				.getMilliSecondOffsetToCurrentTime(-timeZoneOffsetInMilliSec - 1000);
		assertEquals(EvalStatus.OPEN, evaluation.getStatus());

		// just after grace period
		gracePeriod = 5;

		evaluation.startTime = Common.getMilliSecondOffsetToCurrentTime(-1);
		evaluation.endTime = Common
				.getMilliSecondOffsetToCurrentTime(-timeZoneOffsetInMilliSec
						- gracePeriod * 60 * 1000);
		evaluation.gracePeriod = gracePeriod;
		Thread.sleep(5);
		assertEquals(EvalStatus.CLOSED, evaluation.getStatus());

		// already PUBLISHED
		evaluation.published = true;
		assertEquals(EvalStatus.PUBLISHED, evaluation.getStatus());
	}
	
	@Test
	public void testValidate() throws InvalidParametersException{
		EvaluationData e = new EvaluationData();
		
		e.course = "valid-course";
		e.name = "valid name";
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = Common.getDateOffsetToCurrentTime(2);
		
		//minimal properties, still valid
		e.validate();
		
		e.activated = false;
		e.published = false;
		e.instructions = "valid instructions";
		e.timeZone = 0.0;
		e.gracePeriod = 5;
		e.p2pEnabled = true;
		
		//other properties added, still valid
		e.validate();
		
		//no course: invalid
		e.course = null;
		verifyInvalidState(e, Common.ERRORCODE_NULL_PARAMETER);
		
		//no name: invalid
		e.course = "valid-course";
		e.name = null; 
		verifyInvalidState(e, Common.ERRORCODE_NULL_PARAMETER);
		
		//no start time: invalid
		e.name = "valid name";
		e.startTime = null;
		verifyInvalidState(e, Common.ERRORCODE_NULL_PARAMETER);
		
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = null;
		verifyInvalidState(e, Common.ERRORCODE_NULL_PARAMETER);
		
		//end before start: invalid
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.startTime = Common.getDateOffsetToCurrentTime(2);
		System.out.println(Common.calendarToString(Common.dateToCalendar(e.startTime)));
		System.out.println(Common.calendarToString(Common.dateToCalendar(e.endTime)));
		verifyInvalidState(e, Common.ERRORCODE_END_BEFORE_START);
		
		//published before endtime: invalid
		e.published=true;
		e.startTime = Common.getDateOffsetToCurrentTime(0);
		e.endTime = Common.getMilliSecondOffsetToCurrentTime(5);
		verifyInvalidState(e, Common.ERRORCODE_PUBLISHED_BEFORE_CLOSING);
		
		//just after endtime and published: valid
		e.startTime = Common.getDateOffsetToCurrentTime(-1);
		e.endTime = Common.getMilliSecondOffsetToCurrentTime(-5);
		e.published=true;
		e.validate();
		
		//activated before start time: invalid
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = Common.getDateOffsetToCurrentTime(2);
		e.published=false;
		e.activated=true;
		verifyInvalidState(e, Common.ERRORCODE_ACTIVATED_BEFORE_START);
		
	}


	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(EvaluationData.class);
	}

	private void verifyInvalidState(EvaluationData eval, String expectedErrorCode) {
		try {
			eval.validate();
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(expectedErrorCode, e.errorCode);
		}
		
	}
}
