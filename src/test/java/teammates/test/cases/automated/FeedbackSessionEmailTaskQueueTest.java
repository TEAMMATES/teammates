package teammates.test.cases.automated;

import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;
import teammates.logic.core.FeedbackSessionsLogic;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

/**
 *  Tests feedback session reminder and publish emails.
 *  Tests the SystemParams.EMAIL_TASK_QUEUE, and SystemParams.SEND_EMAIL_TASK_QUEUE
 * 
 */
@Test(sequential = true)
public class FeedbackSessionEmailTaskQueueTest extends BaseComponentUsingTaskQueueTestCase {
    
    private static final Logic logic = new Logic();
    private static final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    private static final DataBundle dataBundle = getTypicalDataBundle();

    @SuppressWarnings("serial")
    public static class FeedbackSessionsEmailTaskQueueCallback extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.SUBMISSION_FEEDBACK));
            assertNotNull(paramMap.get(ParamsNames.SUBMISSION_FEEDBACK));

            assertTrue(paramMap.containsKey(ParamsNames.SUBMISSION_COURSE));
            assertNotNull(paramMap.get(ParamsNames.SUBMISSION_COURSE));
            
            FeedbackSessionsEmailTaskQueueCallback.taskCount++;
             
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }
    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.tearDown();
        gaeSimulation.setupWithTaskQueueCallbackClass(FeedbackSessionsEmailTaskQueueCallback.class);
        gaeSimulation.resetDatastore();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAll() throws Exception {
        testFeedbackSessionsPublishEmail();
        testFeedbackSessionsRemindEmail();
    }
    
    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }

    private void testFeedbackSessionsPublishEmail() throws Exception {
        
        FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
        
        ______TS("Publish feedback session and send email");
        
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        
        FeedbackSessionAttributes fsa = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");
        fsa.setEndTime(TimeHelper.getHoursOffsetToCurrentTime(0));
        
        feedbackSessionsLogic.updateFeedbackSession(fsa);

        int counter = 0;

        while (counter != 10) {
            FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
            feedbackSessionsLogic.publishFeedbackSession(fsa.getFeedbackSessionName(), fsa.getCourseId());
            if (FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(1)) {
                break;
            }
            counter++;
        }
     
        assertEquals(FeedbackSessionsEmailTaskQueueCallback.taskCount, 1);
        
        ______TS("Try to publish non-existent feedback session");
        
        FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
        fsa.setEndTime(TimeHelper.getHoursOffsetToCurrentTime(0));
        feedbackSessionsLogic.updateFeedbackSession(fsa);
        try {
            feedbackSessionsLogic.publishFeedbackSession("non-existent-feedback-session", "non-existent-course");
        } catch (Exception e) {
            assertEquals("Trying to publish a non-existent feedback session: " + "non-existent-course" + "/"
                         + "non-existent-feedback-session", e.getMessage());
        }
        if (!FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(0)) {
            assertEquals(FeedbackSessionsEmailTaskQueueCallback.taskCount, 0);
        }
        
    }
    
    private void testFeedbackSessionsRemindEmail() {
        
        FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();

        ______TS("Send feedback session reminder email");
        
        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("session2InCourse1");
        int counter = 0;

        while (counter != 10) {
            FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
            logic.sendReminderForFeedbackSession(fsa.getCourseId(), fsa.getFeedbackSessionName());
            if (FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(1)) {
                break;
            }
            counter++;
        }

        assertEquals(1, FeedbackSessionsEmailTaskQueueCallback.taskCount);

        ______TS("Try to send reminder for null feedback session");
        
        FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
        fsa = dataBundle.feedbackSessions.get("session2InCourse1");
        try {
            logic.sendReminderForFeedbackSession(fsa.getCourseId(), null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals("The supplied parameter was null\n", ae.getMessage());
        }
        
        assertEquals(0, FeedbackSessionsEmailTaskQueueCallback.taskCount);
    }
    
    @Test
    public void testScheduleFeedbackSessionOpeningEmails() throws Exception {
        // this method tests a function from FeedbackSessionLogic.java

        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
        
        ______TS("no opening email tasks to be sent");
                
        fsLogic.scheduleFeedbackSessionOpeningEmails();
        assertEquals(0, FeedbackSessionsEmailTaskQueueCallback.taskCount);
        
        ______TS("1 opening email task to be sent");
        
        FeedbackSessionAttributes fsa = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");
        assertEquals("First feedback session", fsa.getFeedbackSessionName());
        fsa.setStartTime(TimeHelper.getDateOffsetToCurrentTime(1));
        fsa.setEndTime(TimeHelper.getDateOffsetToCurrentTime(2));
        
        fsLogic.updateFeedbackSession(fsa);
        fsa = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");
        assertFalse(fsa.isSentOpenEmail());
        fsa.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-1));
        fsLogic.updateFeedbackSession(fsa);
        
        int counter = 0;

        while (counter != 10) {
            FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
            fsLogic.scheduleFeedbackSessionOpeningEmails();
            if (FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(1)) {
                break;
            }
            counter++;
        }

        assertEquals(FeedbackSessionsEmailTaskQueueCallback.taskCount, 1);
    }
    
    @Test
    public void testScheduleFeedbackSessionClosingEmails() throws Exception {
        // this method tests a function from FeedbackSessionLogic.java
        
        FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        
        ______TS("no closing email tasks to be sent");
        
        assertTrue(fsLogic.getFeedbackSessionsClosingWithinTimeLimit().isEmpty());
        fsLogic.scheduleFeedbackSessionOpeningEmails();
        if (!FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(0)) {
            assertEquals(FeedbackSessionsEmailTaskQueueCallback.taskCount, 0);
        }
        
        ______TS("1 closing email task to be sent");

        FeedbackSessionAttributes fsa = fsLogic.getFeedbackSession("First feedback session", "idOfTypicalCourse1");
        fsa.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-3));
        fsa.setEndTime(TimeHelper.getMsOffsetToCurrentTime((SystemParams.NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT
                + (int) fsa.getTimeZone()) * 60 * 60 * 1000 - 60 * 1000));
        fsLogic.updateFeedbackSession(fsa);
        
        assertFalse(fsLogic.getFeedbackSessionsClosingWithinTimeLimit().isEmpty());
        
        int counter = 0;
        while (counter != 10) {
            FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
            fsLogic.scheduleFeedbackSessionClosingEmails();
            if (FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(1)) {
                break;
            }
            counter++;
        }

        assertEquals(FeedbackSessionsEmailTaskQueueCallback.taskCount, 1);
    }
    
    @Test
    public void testScheduleFeedbackSessionPublishedEmails() throws Exception {
        // this method tests a function from FeedbackSessionLogic.java
        
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        
        ______TS("1 closing email tasks to be sent");
        
        int counter = 0;
        while (counter != 10) {
            FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
            fsLogic.scheduleFeedbackSessionPublishedEmails(); // empty session
            if (FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(1)) {
                break;
            }
            counter++;
        }
        assertEquals(FeedbackSessionsEmailTaskQueueCallback.taskCount, 1);
        
        ______TS("0 closing email task to be sent");

        FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
        FeedbackSessionAttributes fsa = fsLogic.getFeedbackSession("Empty session", "idOfTypicalCourse1");
        fsa.setPublishedEmailEnabled(false);
        fsLogic.updateFeedbackSession(fsa);
        fsLogic.scheduleFeedbackSessionPublishedEmails();
        
        if (!FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(0)) {
            assertEquals(FeedbackSessionsEmailTaskQueueCallback.taskCount, 0);
        }
    }
    
    @Test
    public void testSendFeedbackSessionUnpublishedEmail() throws Exception {
        // this method tests a function from FeedbackSessionLogic.java
        
        FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

        ______TS("1 unpublished email to be sent");
        
        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("session2InCourse1");
        int counter = 0;
        
        // retry test when it fails, to avoid test failures when other tests ran
        // in parallel were also adding tasks to the same task queue.
        while (counter != 10) {
            FeedbackSessionsEmailTaskQueueCallback.resetTaskCount();
            fsLogic.sendFeedbackSessionUnpublishedEmail(fsa);
            if (FeedbackSessionsEmailTaskQueueCallback.verifyTaskCount(1)) {
                break;
            }
            counter++;
        }

        assertEquals(1, FeedbackSessionsEmailTaskQueueCallback.taskCount);

    }
}
