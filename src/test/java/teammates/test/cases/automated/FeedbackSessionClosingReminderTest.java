package teammates.test.cases.automated;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.automated.EmailAction;
import teammates.logic.automated.FeedbackSessionClosingMailAction;
import teammates.logic.core.Emails;
import teammates.logic.core.Emails.EmailType;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.util.TestHelper;

public class FeedbackSessionClosingReminderTest extends BaseComponentUsingTaskQueueTestCase {

    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @SuppressWarnings("serial")
    public static class FeedbackSessionClosingCallback extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_TYPE));
            
            EmailType typeOfMail = EmailType.valueOf((String) paramMap.get(ParamsNames.EMAIL_TYPE));
            assertEquals(EmailType.FEEDBACK_CLOSING, typeOfMail);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_FEEDBACK));
            assertNotNull(paramMap.get(ParamsNames.EMAIL_FEEDBACK));
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_COURSE));
            assertNotNull(paramMap.get(ParamsNames.EMAIL_COURSE));
            
            FeedbackSessionClosingCallback.taskCount++;
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }
    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.tearDown();
        gaeSimulation.setupWithTaskQueueCallbackClass(FeedbackSessionClosingCallback.class);
        gaeSimulation.resetDatastore();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }
    
    @Test
    public void testAdditionOfTaskToTaskQueue() throws Exception {        
        FeedbackSessionClosingCallback.resetTaskCount();
        
        ______TS("typical case, 0 sessions closing soon");
        fsLogic.scheduleFeedbackSessionClosingEmails();
        if(!FeedbackSessionClosingCallback.verifyTaskCount(0)){
            assertEquals(FeedbackSessionClosingCallback.taskCount, 0);
        }
        
        ______TS("typical case, two sessions closing soon, "
                + "1 session closing soon with disabled closing reminder.");
        // Modify session to close in 24 hours.
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions
                .get("session1InCourse1");

        session1.timeZone = 0;
        session1.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session1.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        fsLogic.updateFeedbackSession(session1);
        TestHelper.verifyPresentInDatastore(session1);
        
        // Reuse an existing session to create a new one that is
        // closing in 24 hours.
        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions
                .get("session2InCourse2");
        
        session2.timeZone = 0;
        session2.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session2.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        fsLogic.updateFeedbackSession(session2);
        TestHelper.verifyPresentInDatastore(session2);
        
        // Reuse an existing session to create a new one that is
        // closing in 24 hours and closing reminder disabled.
        FeedbackSessionAttributes session3 = dataBundle.feedbackSessions
                .get("session2InCourse1");
        
        session3.timeZone = 0;
        session3.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session3.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        session3.isClosingEmailEnabled = false;
        fsLogic.updateFeedbackSession(session3);
        TestHelper.verifyPresentInDatastore(session3);
        
        int counter = 0;
        while(counter != 10){
            FeedbackSessionClosingCallback.resetTaskCount();
            fsLogic.scheduleFeedbackSessionClosingEmails();
            //There are only 2 sessions closing reminder to be sent
            if(FeedbackSessionClosingCallback.verifyTaskCount(2)){
                break;
            }
            counter++;
        }
        if(counter == 10){
            assertEquals(FeedbackSessionClosingCallback.taskCount, 2);
        }
    }

    @Test
    public void testFeedbackSessionClosingMailAction() throws Exception{
        
        ______TS("typical case, testing mime messages");
        // Modify session to close in 24 hours.
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions
                .get("session1InCourse1");
        session1.timeZone = 0;
        session1.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session1.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        fsLogic.updateFeedbackSession(session1);
        HashMap<String, String> paramMap = createParamMapForAction(session1);
        
        EmailAction fsClosingAction = new FeedbackSessionClosingMailAction(paramMap);
        int course1StudentCount = 5-2; // 2 students have already completed the session 
        int course1InstructorCount = 5;
        
        List<MimeMessage> preparedEmails = fsClosingAction.getPreparedEmailsAndPerformSuccessOperations();
        assertEquals(course1StudentCount + course1InstructorCount, preparedEmails.size());
        
        for (MimeMessage m : preparedEmails) {
            String subject = m.getSubject();
            assertTrue(subject.contains(session1.feedbackSessionName));
            assertTrue(subject.contains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_CLOSING));
        }
        
        // Reuse an existing session to create a new one that is
        // closing in 24 hours.
        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions
                .get("session2InCourse2");
        session2.timeZone = 0;
        session2.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session2.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        fsLogic.updateFeedbackSession(session2);
        TestHelper.verifyPresentInDatastore(session2);
        
        // Reuse an existing session to create a new one that is
        // closing in 24 hours and closing reminder disabled.
        FeedbackSessionAttributes session3 = dataBundle.feedbackSessions
                .get("session2InCourse1");
        
        session3.timeZone = 0;
        session3.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session3.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        session3.isClosingEmailEnabled = false;
        fsLogic.updateFeedbackSession(session3);
        TestHelper.verifyPresentInDatastore(session3);
        
        paramMap = createParamMapForAction(session2);
        fsClosingAction = new FeedbackSessionClosingMailAction(paramMap);
        int course2StudentCount = 0; // there are no questions, so no students can see the session
        int course2InstructorCount = 3;
        
        preparedEmails = fsClosingAction.getPreparedEmailsAndPerformSuccessOperations();
        assertEquals(course2StudentCount + course2InstructorCount, preparedEmails.size());
        
        for (MimeMessage m : preparedEmails) {
            String subject = m.getSubject();
            assertTrue(subject.contains(session2.feedbackSessionName));
            assertTrue(subject.contains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_CLOSING));
        }
    }
    
    private HashMap<String, String> createParamMapForAction(FeedbackSessionAttributes fs) {
        //Prepare parameter map to be used with FeedbackSessionClosingMailAction
        HashMap<String, String> paramMap = new HashMap<String, String>();
        
        paramMap.put(ParamsNames.EMAIL_TYPE, EmailType.FEEDBACK_CLOSING.toString());
        paramMap.put(ParamsNames.EMAIL_FEEDBACK, fs.feedbackSessionName);
        paramMap.put(ParamsNames.EMAIL_COURSE, fs.courseId);
        
        return paramMap;
    }
}
