package teammates.test.cases.automated;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
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
import teammates.logic.automated.FeedbackSessionOpeningMailAction;
import teammates.logic.core.Emails;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.Emails.EmailType;
import teammates.test.util.TestHelper;

public class FeedbackSessionOpeningReminderTest extends BaseComponentUsingTaskQueueTestCase {

    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @SuppressWarnings("serial")
    public static class FeedbackSessionOpeningCallback extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_TYPE));
            EmailType typeOfMail = EmailType.valueOf((String) paramMap.get(ParamsNames.EMAIL_TYPE));
            assertEquals(EmailType.FEEDBACK_OPENING, typeOfMail);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_FEEDBACK));
            assertNotNull(paramMap.get(ParamsNames.EMAIL_FEEDBACK));
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_COURSE));
            assertNotNull(paramMap.get(ParamsNames.EMAIL_COURSE));
            
            FeedbackSessionOpeningCallback.taskCount++;
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }
    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.tearDown();
        gaeSimulation.setupWithTaskQueueCallbackClass(FeedbackSessionOpeningCallback.class);
        gaeSimulation.resetDatastore();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }
    
    @Test
    public void testAdditionOfTaskToTaskQueue() throws Exception {
        
        FeedbackSessionOpeningCallback.resetTaskCount();
        
        ______TS("3 sessions opened and emails sent, 1 awaiting");
        fsLogic.scheduleFeedbackSessionOpeningEmails();
        if(!FeedbackSessionOpeningCallback.verifyTaskCount(0)){
            assertEquals(FeedbackSessionOpeningCallback.taskCount, 0);
        }
        
        ______TS("2 sessions opened and emails sent, 1 session opened without emails sent, "
                + "1 session opened without emails sent with sending open email disabled");
        // Modify session to set emails as unsent but still open
        // by closing and opening the session.
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions
                .get("session1InCourse1");
        session1.startTime = TimeHelper.getDateOffsetToCurrentTime(2);
        session1.endTime = TimeHelper.getDateOffsetToCurrentTime(3);
        fsLogic.updateFeedbackSession(session1);
        session1.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        fsLogic.updateFeedbackSession(session1);
        TestHelper.verifyPresentInDatastore(session1);
        
        // Modify session to set emails as unsent but still open
        // by closing and opening the session. Also disable sending
        // open emails, but in the current implementation open emails
        // will still be sent regardless.
        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions
                .get("session2InCourse1");
        session2.startTime = TimeHelper.getDateOffsetToCurrentTime(2);
        session2.endTime = TimeHelper.getDateOffsetToCurrentTime(3);
        session2.isOpeningEmailEnabled = false;
        fsLogic.updateFeedbackSession(session2);
        session2.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        fsLogic.updateFeedbackSession(session2);
        TestHelper.verifyPresentInDatastore(session2);
        
        int counter = 0;

        while(counter != 10){
            FeedbackSessionOpeningCallback.resetTaskCount();
            fsLogic.scheduleFeedbackSessionOpeningEmails();
            if (FeedbackSessionOpeningCallback.verifyTaskCount(2)) {
                break;
            }
            counter++;
        }
        assertEquals(2, FeedbackSessionOpeningCallback.taskCount);
    }

    @Test
    public void testFeedbackSessionOpeningMailAction() throws Exception{
        
        ______TS("MimeMessage Test : 2 sessions opened and emails sent, 1 session opened without emails sent, "
                + "1 session opened without emails sent with sending open email disabled");
        // Modify session to set emails as unsent but still open
        // by closing and opening the session.
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions
                .get("session1InCourse1");
        session1.startTime = TimeHelper.getDateOffsetToCurrentTime(2);
        session1.endTime = TimeHelper.getDateOffsetToCurrentTime(3);
        fsLogic.updateFeedbackSession(session1);
        session1.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        fsLogic.updateFeedbackSession(session1);
        
        // Modify session to set emails as unsent but still open
        // by closing and opening the session. Also disable sending
        // open emails, but in the current implementation open emails
        // will still be sent regardless.
        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions
                .get("session2InCourse1");
        session2.startTime = TimeHelper.getDateOffsetToCurrentTime(2);
        session2.endTime = TimeHelper.getDateOffsetToCurrentTime(3);
        session2.isOpeningEmailEnabled = false;
        fsLogic.updateFeedbackSession(session2);
        session2.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        fsLogic.updateFeedbackSession(session2);
        
        int course1StudentCount = 5; 
        int course1InstructorCount = 5;
        
        prepareAndSendOpeningMailForSession(session1, course1StudentCount, course1InstructorCount);
        
        ______TS("testing whether session2 still requires opening mails even though it's already disabled");
        FeedbackSessionOpeningCallback.resetTaskCount();
        fsLogic.scheduleFeedbackSessionOpeningEmails();
        assertTrue(FeedbackSessionOpeningCallback.verifyTaskCount(1));

        prepareAndSendOpeningMailForSession(session2, course1StudentCount, course1InstructorCount);

        ______TS("testing whether no more mails are sent");
        FeedbackSessionOpeningCallback.resetTaskCount();
        fsLogic.scheduleFeedbackSessionOpeningEmails();
        if(!FeedbackSessionOpeningCallback.verifyTaskCount(0)){
            assertEquals(FeedbackSessionOpeningCallback.taskCount, 0);
        }
    }
    
    private void prepareAndSendOpeningMailForSession(FeedbackSessionAttributes session, int studentCount,
                                                     int instructorCount) throws MessagingException {
        HashMap<String, String> paramMap = createParamMapForAction(session);
        EmailAction fsOpeningAction = new FeedbackSessionOpeningMailAction(paramMap);

        List<MimeMessage> preparedEmails = fsOpeningAction.getPreparedEmailsAndPerformSuccessOperations();
        assertEquals(studentCount + instructorCount, preparedEmails.size());

        for (MimeMessage m : preparedEmails) {
            String subject = m.getSubject();
            assertTrue(subject.contains(session.feedbackSessionName));
            assertTrue(subject.contains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_OPENING));
        }
    }

    private HashMap<String, String> createParamMapForAction(FeedbackSessionAttributes fs) {
        //Prepare parameter map to be used with FeedbackSessionOpeningMailAction
        HashMap<String, String> paramMap = new HashMap<String, String>();
        
        paramMap.put(ParamsNames.EMAIL_TYPE, EmailType.FEEDBACK_OPENING.toString());
        paramMap.put(ParamsNames.EMAIL_FEEDBACK, fs.feedbackSessionName);
        paramMap.put(ParamsNames.EMAIL_COURSE, fs.courseId);
        
        return paramMap;
    }
}
