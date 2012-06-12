package teammates.testing.testcases;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
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

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(EvaluationData.class);
	}

}
