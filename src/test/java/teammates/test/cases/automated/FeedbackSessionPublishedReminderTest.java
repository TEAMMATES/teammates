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
import teammates.logic.automated.FeedbackSessionPublishedMailAction;
import teammates.logic.core.Emails;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.Emails.EmailType;
import teammates.test.util.Priority;
import teammates.test.util.TestHelper;

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
            EmailType typeOfMail = EmailType.valueOf((String) paramMap.get(ParamsNames.EMAIL_TYPE));
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
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }
    
    @Test
    public void testAdditionOfTaskToTaskQueue() throws Exception {        
        FeedbackSessionPublishedCallback.resetTaskCount();
        
        ______TS("3 sessions unpublished, 1 published and emails unsent");
        int counter = 0;

        while(counter < 10){
            counter++;
            FeedbackSessionPublishedCallback.resetTaskCount();
            fsLogic.scheduleFeedbackSessionPublishedEmails();
            if(FeedbackSessionPublishedCallback.verifyTaskCount(1)){
                break;
            }
        }
        
        assertEquals(FeedbackSessionPublishedCallback.taskCount, 1);
       
        ______TS("publish sessions");
        //  1 sessions unpublished, 1 published and email sent,
        //  1 published by changing publish time, 1 manually published
        
        // Publish session by moving automated publish time
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        session1.resultsVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        fsLogic.updateFeedbackSession(session1);
        TestHelper.verifyPresentInDatastore(session1);
        
        // Do a manual publish
        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions.get("session2InCourse1");
        session2.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
        fsLogic.updateFeedbackSession(session2);
        TestHelper.verifyPresentInDatastore(session2);
        
        // Publish session by moving automated publish time and disable publish reminder
        FeedbackSessionAttributes session3 = dataBundle.feedbackSessions.get("gracePeriodSession");
        session3.resultsVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session3.isPublishedEmailEnabled = false;
        fsLogic.updateFeedbackSession(session3);
        TestHelper.verifyPresentInDatastore(session3);
            
        // Check that 3 published sessions will have emails sent as
        // Manually publish sessions have emails also added to the task queue
        fsLogic.publishFeedbackSession(session2.feedbackSessionName, session2.courseId);

        counter = 0;

        while(counter < 10){
            counter++;
            FeedbackSessionPublishedCallback.resetTaskCount();
            fsLogic.scheduleFeedbackSessionPublishedEmails();
            if(FeedbackSessionPublishedCallback.verifyTaskCount(3)){
                break;
            }
        }
        assertEquals(3, FeedbackSessionPublishedCallback.taskCount);
        
        ______TS("unpublish a session");
        fsLogic.unpublishFeedbackSession(session2.feedbackSessionName, session2.courseId);
        
        counter = 0;

        while(counter < 10){
            counter++;
            FeedbackSessionPublishedCallback.resetTaskCount();
            fsLogic.scheduleFeedbackSessionPublishedEmails();
            if(FeedbackSessionPublishedCallback.verifyTaskCount(2)){
                break;
            }
        }
        if(counter == 10){
            assertEquals(FeedbackSessionPublishedCallback.taskCount, 2);
        }
       
    }

    @Test
    public void testFeedbackSessionPublishedMailAction() throws Exception{

        ______TS("MimeMessage Test : activate all sessions with mails sent");
        for (FeedbackSessionAttributes fs : dataBundle.feedbackSessions.values()) {
            fs.sentPublishedEmail = true;
            fsLogic.updateFeedbackSession(fs);
            assertTrue(fsLogic.getFeedbackSession(fs.feedbackSessionName, fs.courseId).sentPublishedEmail);
        }
        ______TS("MimeMessage Test : set session 1 to unsent emails and publish");
        // Modify session to set as published but emails unsent
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        session1.resultsVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session1.sentPublishedEmail = false;
        fsLogic.updateFeedbackSession(session1);
        
        HashMap<String, String> paramMap = createParamMapForAction(session1);
        EmailAction fsPublishedAction = new FeedbackSessionPublishedMailAction(paramMap);
        int course1StudentCount = 5; 
        int course1InstructorCount = 5;
        
        List<MimeMessage> preparedEmails = fsPublishedAction.getPreparedEmailsAndPerformSuccessOperations();
        assertEquals(course1StudentCount + course1InstructorCount, preparedEmails.size());

        for (MimeMessage m : preparedEmails) {
            String subject = m.getSubject();
            assertTrue(subject.contains(session1.feedbackSessionName));
            assertTrue(subject.contains(Emails.SUBJECT_PREFIX_FEEDBACK_SESSION_PUBLISHED));
        }
        
        ______TS("testing whether no more mails are sent");
        FeedbackSessionPublishedCallback.resetTaskCount();
        fsLogic.scheduleFeedbackSessionPublishedEmails();
        if(!FeedbackSessionPublishedCallback.verifyTaskCount(0)){
            assertEquals(FeedbackSessionPublishedCallback.taskCount, 0);
        }
    }
    
    private HashMap<String, String> createParamMapForAction(FeedbackSessionAttributes fs) {
        //Prepare parameter map to be used with FeedbackSessionPublishedMailAction
        HashMap<String, String> paramMap = new HashMap<String, String>();
        
        paramMap.put(ParamsNames.EMAIL_TYPE, EmailType.FEEDBACK_PUBLISHED.toString());
        paramMap.put(ParamsNames.EMAIL_FEEDBACK, fs.feedbackSessionName);
        paramMap.put(ParamsNames.EMAIL_COURSE, fs.courseId);
        
        return paramMap;
    }
}
