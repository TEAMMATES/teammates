package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;
import teammates.logic.core.EvaluationsLogic;
import teammates.test.cases.BaseComponentUsingTaskQueueTestCase;
import teammates.test.cases.BaseTaskQueueCallback;

public class EvaluationsEmailTaskQueueTest extends
		BaseComponentUsingTaskQueueTestCase {
	
	private static final Logic logic = new Logic();
	private static final EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();

	@SuppressWarnings("serial")
	public static class EvaluationsEmailTaskQueueCallback extends BaseTaskQueueCallback {
		
		@Override
		public int execute(URLFetchRequest request) {
			HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
			
			assertTrue(paramMap.containsKey(ParamsNames.SUBMISSION_EVAL));
			assertNotNull(paramMap.get(ParamsNames.SUBMISSION_EVAL));

			assertTrue(paramMap.containsKey(ParamsNames.SUBMISSION_COURSE));
			assertNotNull(paramMap.get(ParamsNames.SUBMISSION_COURSE));
			
			EvaluationsEmailTaskQueueCallback.taskCount++;
			return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
		}
	}
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		gaeSimulation.setupWithTaskQueueCallbackClass(EvaluationsEmailTaskQueueCallback.class);
		gaeSimulation.resetDatastore();
		turnLoggingUp(EvaluationsLogic.class);
	}
	
	@Test
	public void testAll() throws Exception {
		testEvaluationsPublishEmail();
		testEvaluationsRemindEmail();
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(EvaluationsLogic.class);
	}
	
	private void testEvaluationsPublishEmail() throws Exception{
		restoreTypicalDataInDatastore();
		EvaluationsEmailTaskQueueCallback.resetTaskCount();
		DataBundle dataBundle = getTypicalDataBundle();

		______TS("Publish evaluation and send email");
		
		EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
		eval.endTime = TimeHelper.getHoursOffsetToCurrentTime(0);
		evaluationsLogic.updateEvaluation(eval);
		evaluationsLogic.publishEvaluation(eval.courseId, eval.name);
		EvaluationsEmailTaskQueueCallback.verifyTaskCount(1);
		EvaluationsEmailTaskQueueCallback.resetTaskCount();
	
		
		______TS("Try to publish non-existent evaluation");
		
		eval.endTime = TimeHelper.getHoursOffsetToCurrentTime(0);
		evaluationsLogic.updateEvaluation(eval);
		try {
			evaluationsLogic.publishEvaluation("non-existent-course", "non-existent-evaluation");
		} catch (Exception e) {
			assertEquals("Trying to edit non-existent evaluation non-existent-course/non-existent-evaluation", 
					e.getMessage());
		}
		EvaluationsEmailTaskQueueCallback.verifyTaskCount(0);
		
	}
	
	private void testEvaluationsRemindEmail() throws Exception {
		restoreTypicalDataInDatastore();
		EvaluationsEmailTaskQueueCallback.resetTaskCount();
		DataBundle dataBundle = getTypicalDataBundle();

		______TS("Send evaluation reminder email");
		
		EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
		logic.sendReminderForEvaluation(eval.courseId, eval.name);
		EvaluationsEmailTaskQueueCallback.verifyTaskCount(1);
		EvaluationsEmailTaskQueueCallback.resetTaskCount();
		
		______TS("Try to send reminder for null evaluation");
		
		eval = dataBundle.evaluations.get("evaluation1InCourse1");
		try {
			logic.sendReminderForEvaluation(eval.courseId, null);
		} catch (AssertionError a) {
			assertEquals("The supplied parameter was null\n", a.getMessage());
		}
		EvaluationsEmailTaskQueueCallback.verifyTaskCount(0);
	}
}
