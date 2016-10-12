package teammates.test.cases.automated;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.automated.EmailAction;
import teammates.logic.automated.FeedbackSessionPublishedMailAction;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.util.Priority;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

@Priority(-1)
public class FeedbackSessionPublishedReminderTest extends BaseComponentUsingTaskQueueTestCase {

    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @SuppressWarnings("serial")
    public static class FeedbackSessionPublishedCallback extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_TYPE));
            EmailType typeOfMail = EmailType.valueOf(paramMap.get(ParamsNames.EMAIL_TYPE));
            assertEquals(EmailType.FEEDBACK_PUBLISHED, typeOfMail);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_FEEDBACK));
            assertNotNull(paramMap.get(ParamsNames.EMAIL_FEEDBACK));
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_COURSE));
            assertNotNull(paramMap.get(ParamsNames.EMAIL_COURSE));
            
            FeedbackSessionPublishedCallback.taskCount++;
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }

    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.tearDown();
        gaeSimulation.setupWithTaskQueueCallbackClass(FeedbackSessionPublishedCallback.class);
        gaeSimulation.resetDatastore();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }
    
    @Test
    public void testAdditionOfTaskToTaskQueue() throws Exception {
        FeedbackSessionPublishedCallback.resetTaskCount();
        
        ______TS("3 sessions unpublished, 1 published and emails unsent");
        int counter = 0;

        while (counter < 10) {
            counter++;
            FeedbackSessionPublishedCallback.resetTaskCount();
            fsLogic.scheduleFeedbackSessionPublishedEmails();
            if (FeedbackSessionPublishedCallback.verifyTaskCount(1)) {
                break;
            }
        }
        
        assertEquals(FeedbackSessionPublishedCallback.taskCount, 1);
       
        ______TS("publish sessions");
        //  1 sessions unpublished, 1 published and email sent,
        //  1 published by changing publish time, 1 manually published
        
        // Publish session by moving automated publish time
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        session1.setResultsVisibleFromTime(TimeHelper.getDateOffsetToCurrentTime(-1));
        fsLogic.updateFeedbackSession(session1);
        verifyPresentInDatastore(session1);
        
        // Do a manual publish
        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions.get("session2InCourse1");
        session2.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        fsLogic.updateFeedbackSession(session2);
        verifyPresentInDatastore(session2);
        
        // Publish session by moving automated publish time and disable publish reminder
        FeedbackSessionAttributes session3 = dataBundle.feedbackSessions.get("gracePeriodSession");
        session3.setResultsVisibleFromTime(TimeHelper.getDateOffsetToCurrentTime(-1));
        session3.setPublishedEmailEnabled(false);
        fsLogic.updateFeedbackSession(session3);
        verifyPresentInDatastore(session3);
            
        // Check that 3 published sessions will have emails sent as
        // Manually publish sessions have emails also added to the task queue
        fsLogic.publishFeedbackSession(session2.getFeedbackSessionName(), session2.getCourseId());

        counter = 0;

        while (counter < 10) {
            counter++;
            FeedbackSessionPublishedCallback.resetTaskCount();
            fsLogic.scheduleFeedbackSessionPublishedEmails();
            if (FeedbackSessionPublishedCallback.verifyTaskCount(3)) {
                break;
            }
        }
        assertEquals(3, FeedbackSessionPublishedCallback.taskCount);
        
        ______TS("unpublish a session");
        fsLogic.unpublishFeedbackSession(session2.getFeedbackSessionName(), session2.getCourseId());
        
        counter = 0;

        while (counter < 10) {
            counter++;
            FeedbackSessionPublishedCallback.resetTaskCount();
            fsLogic.scheduleFeedbackSessionPublishedEmails();
            if (FeedbackSessionPublishedCallback.verifyTaskCount(2)) {
                break;
            }
        }
        if (counter == 10) {
            assertEquals(FeedbackSessionPublishedCallback.taskCount, 2);
        }
       
    }

    @Test
    public void testFeedbackSessionPublishedMailAction() throws Exception {

        ______TS("Emails Test : activate all sessions with mails sent");
        for (FeedbackSessionAttributes fs : dataBundle.feedbackSessions.values()) {
            fs.setSentPublishedEmail(true);
            fsLogic.updateFeedbackSession(fs);
            assertTrue(fsLogic.getFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId()).isSentPublishedEmail());
        }
        ______TS("Emails Test : set session 1 to unsent emails and publish");
        // Modify session to set as published but emails unsent
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        String courseName = CoursesLogic.inst().getCourse(session1.getCourseId()).getName();
        session1.setResultsVisibleFromTime(TimeHelper.getDateOffsetToCurrentTime(-1));
        session1.setSentPublishedEmail(false);
        fsLogic.updateFeedbackSession(session1);
        
        HashMap<String, String> paramMap = createParamMapForAction(session1);
        EmailAction fsPublishedAction = new FeedbackSessionPublishedMailAction(paramMap);
        int course1StudentCount = 5;
        int course1InstructorCount = 5;
        
        List<EmailWrapper> preparedEmails = fsPublishedAction.getPreparedEmailsAndPerformSuccessOperations();
        assertEquals(course1StudentCount + course1InstructorCount, preparedEmails.size());

        for (EmailWrapper email : preparedEmails) {
            assertEquals(String.format(EmailType.FEEDBACK_PUBLISHED.getSubject(), courseName,
                                       session1.getFeedbackSessionName()),
                         email.getSubject());
        }
        
        ______TS("testing whether no more mails are sent");
        FeedbackSessionPublishedCallback.resetTaskCount();
        fsLogic.scheduleFeedbackSessionPublishedEmails();
        if (!FeedbackSessionPublishedCallback.verifyTaskCount(0)) {
            assertEquals(FeedbackSessionPublishedCallback.taskCount, 0);
        }
    }
    
    private HashMap<String, String> createParamMapForAction(FeedbackSessionAttributes fs) {
        //Prepare parameter map to be used with FeedbackSessionPublishedMailAction
        HashMap<String, String> paramMap = new HashMap<String, String>();
        
        paramMap.put(ParamsNames.EMAIL_TYPE, EmailType.FEEDBACK_PUBLISHED.toString());
        paramMap.put(ParamsNames.EMAIL_FEEDBACK, fs.getFeedbackSessionName());
        paramMap.put(ParamsNames.EMAIL_COURSE, fs.getCourseId());
        
        return paramMap;
    }
}
