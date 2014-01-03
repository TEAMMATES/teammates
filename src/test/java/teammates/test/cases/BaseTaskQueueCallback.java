package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;
import java.util.Map;

import teammates.common.util.ThreadHelper;

import com.google.appengine.api.taskqueue.dev.LocalTaskQueueCallback;

@SuppressWarnings("serial")
public abstract class BaseTaskQueueCallback implements LocalTaskQueueCallback {

	protected static int taskCount;
	
	@Override
	public void initialize(Map<String, String> arg0) {
		taskCount = 0;
	}
	
	public static void resetTaskCount() {
		taskCount = 0;
	}
	
	public static void verifyTaskCount(int expectedCount) {
		waitForTaskQueueExecution(expectedCount);
		assertEquals(expectedCount, BaseTaskQueueCallback.taskCount);
	}

	public static void waitForTaskQueueExecution(int expectedNumberOfTasks) {
		/*
		 *  Current rate of task execution is 1/s
		 *  Wait for 1 more second to see if erroneous or unwanted tasks
		 *  are added too
		 */
		ThreadHelper.waitFor((expectedNumberOfTasks + 1) * 1000);
	}
}
