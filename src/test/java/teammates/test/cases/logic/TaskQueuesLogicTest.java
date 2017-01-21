package teammates.test.cases.logic;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import teammates.common.util.Const.TaskQueue;
import teammates.common.util.TaskWrapper;
import teammates.common.util.ThreadHelper;
import teammates.logic.core.TaskQueuesLogic;
import teammates.test.cases.BaseComponentTestCase;

import com.google.appengine.api.taskqueue.dev.LocalTaskQueueCallback;
import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

/**
 * SUT: {@link TaskQueuesLogic}.
 */
public class TaskQueuesLogicTest extends BaseComponentTestCase {
    
    private static final TaskQueuesLogic taskQueuesLogic = new TaskQueuesLogic();
    
    @Override
    @BeforeTest
    public void testSetup() {
        gaeSimulation.setupWithTaskQueueCallbackClass(MockTaskQueueCallback.class);
    }
    
    @SuppressWarnings("serial")
    public static class MockTaskQueueCallback implements LocalTaskQueueCallback {
        
        private static int taskCount;
        private static final int TASK_QUEUE_RESPONSE_OK = 200;
        
        @Override
        public int execute(URLFetchRequest arg0) {
            taskCount++;
            return TASK_QUEUE_RESPONSE_OK;
        }
        
        @Override
        public void initialize(Map<String, String> arg0) {
            resetTaskCount();
        }
        
        private static void resetTaskCount() {
            taskCount = 0;
        }
        
    }
    
    @Test(enabled = false)
    public void allTests() {
        
        int waitTimeForQueueInMs = 1000; // the buffer time for the task to be queued
        
        assertEquals(0, MockTaskQueueCallback.taskCount);
        
        ______TS("add tasks for immediate queueing");
        
        taskQueuesLogic.addTask(new TaskWrapper(TaskQueue.SEND_EMAIL_QUEUE_NAME, "/workerUrl",
                                                new HashMap<String, String[]>()));
        ThreadHelper.waitFor(waitTimeForQueueInMs);
        assertEquals(1, MockTaskQueueCallback.taskCount);
        
        // add another task from different queue
        
        taskQueuesLogic.addTask(new TaskWrapper(TaskQueue.ADMIN_SEND_EMAIL_QUEUE_NAME, "/workerUrl",
                                                new HashMap<String, String[]>()));
        ThreadHelper.waitFor(waitTimeForQueueInMs);
        assertEquals(2, MockTaskQueueCallback.taskCount);
        
        MockTaskQueueCallback.resetTaskCount();
        assertEquals(0, MockTaskQueueCallback.taskCount);
        
        ______TS("add task for delayed queueing");
        
        taskQueuesLogic.addDeferredTask(
                new TaskWrapper(TaskQueue.SEND_EMAIL_QUEUE_NAME, "/workerUrl", new HashMap<String, String[]>()), 2000);
        ThreadHelper.waitFor(waitTimeForQueueInMs + 250);
        assertEquals(0, MockTaskQueueCallback.taskCount); // task is not queued yet
        ThreadHelper.waitFor(500);
        assertEquals(0, MockTaskQueueCallback.taskCount); // task is still not queued yet
        ThreadHelper.waitFor(1250);
        assertEquals(1, MockTaskQueueCallback.taskCount); // task is queued
        
    }
    
}
