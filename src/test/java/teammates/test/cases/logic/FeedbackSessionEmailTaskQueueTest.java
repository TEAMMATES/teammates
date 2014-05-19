package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.cases.BaseComponentUsingTaskQueueTestCase;
import teammates.test.cases.BaseTaskQueueCallback;

/**
 *  Tests feedback session reminder and publish emails.
 *  Tests the SystemParams.EMAIL_TASK_QUEUE, and SystemParams.SEND_EMAIL_TASK_QUEUE
 *  
 */
public class FeedbackSessionEmailTaskQueueTest extends
        BaseComponentUsingTaskQueueTestCase {
    
    private static final Logic logic = new Logic();
    private static final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();

    @SuppressWarnings("serial")
    public static class FeedbackSessionsEmailTaskQueueCallback extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_SUBJECT) || paramMap.containsKey(ParamsNames.EMAIL_FEEDBACK));
             
            FeedbackSessionsEmailTaskQueueCallback.taskCount++;
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }
    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.setupWithTaskQueueCallbackClass(FeedbackSessionsEmailTaskQueueCallback.class);
        gaeSimulation.resetDatastore();
        turnLoggingUp(FeedbackSessionsLogic.class);
    }
    
    @Test
    public void testAll() throws Exception {
        testFeedbackSessionsPublishEmail();
        testFeedbackSessionsRemindEmail();
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(FeedbackSessionsLogic.class);
    }
    

    private void testFeedbackSessionsPublishEmail() throws Exception{
        
        FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();

        restoreTypicalDataInDatastore();
        
        ______TS("Publish feedback session and send email");
        
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        
        FeedbackSessionAttributes fsa = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");
        fsa.endTime = TimeHelper.getHoursOffsetToCurrentTime(0);
        
        feedbackSessionsLogic.updateFeedbackSession(fsa);
        feedbackSessionsLogic.publishFeedbackSession(fsa.feedbackSessionName, fsa.courseId);
        FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(1);
        FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
    
        
        ______TS("Try to publish non-existent feedback session");
        
        fsa.endTime = TimeHelper.getHoursOffsetToCurrentTime(0);
        feedbackSessionsLogic.updateFeedbackSession(fsa);
        try {
            feedbackSessionsLogic.publishFeedbackSession("non-existent-feedback-session", "non-existent-course");
        } catch (Exception e) {
            assertEquals("Trying to publish a non-existant session.", 
                    e.getMessage());
        }
        FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(0);
        
    }
    
    private void testFeedbackSessionsRemindEmail() throws Exception {
        
        FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
        DataBundle dataBundle = getTypicalDataBundle();

        restoreTypicalDataInDatastore(); 

        ______TS("Send feedback session reminder email");
        
        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("session1InCourse1");
        logic.sendReminderForFeedbackSession(fsa.courseId, fsa.feedbackSessionName);
        
        FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(7);// 7 people have not completed feedback session.
        FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
        
        ______TS("Try to send reminder for null feedback session");
        
        fsa = dataBundle.feedbackSessions.get("session1InCourse1");
        try {
            logic.sendReminderForFeedbackSession(fsa.courseId, null);
        } catch (AssertionError a) {
            assertEquals("The supplied parameter was null\n", a.getMessage());
        }
        FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(0);
    }
}
