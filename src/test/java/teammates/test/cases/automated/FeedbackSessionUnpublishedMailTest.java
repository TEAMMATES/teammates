package teammates.test.cases.automated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.automated.EmailAction;
import teammates.logic.automated.FeedbackSessionUnpublishedMailAction;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.util.Priority;

@Priority(-1)
public class FeedbackSessionUnpublishedMailTest extends BaseComponentUsingTaskQueueTestCase {
    
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @SuppressWarnings("serial")
    public static class FeedbackSessionUnpublishedCallback extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_TYPE));
            EmailType typeOfMail = EmailType.valueOf(paramMap.get(ParamsNames.EMAIL_TYPE));
            assertEquals(EmailType.FEEDBACK_UNPUBLISHED, typeOfMail);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_FEEDBACK));
            assertNotNull(paramMap.get(ParamsNames.EMAIL_FEEDBACK));
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_COURSE));
            assertNotNull(paramMap.get(ParamsNames.EMAIL_COURSE));
            
            FeedbackSessionUnpublishedCallback.taskCount++;
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }

    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.tearDown();
        gaeSimulation.setupWithTaskQueueCallbackClass(FeedbackSessionUnpublishedCallback.class);
        gaeSimulation.resetDatastore();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }
    
    @Test
    public void testFeedbackSessionUnpublishedMailAction() throws Exception {

        ______TS("Emails Test : activate all sessions with unpublished mails sent");
        for (FeedbackSessionAttributes fs : dataBundle.feedbackSessions.values()) {
            fs.setSentPublishedEmail(false);
            fsLogic.updateFeedbackSession(fs);
            assertFalse(fsLogic.getFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId()).isSentPublishedEmail());
        }
        
        ______TS("Emails Test : set session 1 to unsent unpublished emails and unpublish");
        // unpublished session with emails unsent for unpublished session.
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        String courseName = CoursesLogic.inst().getCourse(session1.getCourseId()).getName();
        session1.setResultsVisibleFromTime(TimeHelper.getDateOffsetToCurrentTime(+1));
        session1.setSentPublishedEmail(true);
        fsLogic.updateFeedbackSession(session1);
        
        Map<String, String> paramMap = createParamMapForAction(session1);
        EmailAction fsUnpublishedAction = new FeedbackSessionUnpublishedMailAction(paramMap);
        int course1StudentCount = studentsLogic.getStudentsForCourse(session1.getCourseId()).size();
        int course1InstructorCount = instructorsLogic.getInstructorsForCourse(session1.getCourseId()).size();
        
        List<EmailWrapper> preparedEmails = fsUnpublishedAction.getPreparedEmailsAndPerformSuccessOperations();
        assertEquals(course1StudentCount + course1InstructorCount, preparedEmails.size());

        for (EmailWrapper email : preparedEmails) {
            assertEquals(String.format(EmailType.FEEDBACK_UNPUBLISHED.getSubject(), courseName,
                                       session1.getFeedbackSessionName()),
                         email.getSubject());
        }
        
    }
    
    private Map<String, String> createParamMapForAction(FeedbackSessionAttributes fs) {
        Map<String, String> paramMap = new HashMap<String, String>();
        
        paramMap.put(ParamsNames.EMAIL_TYPE, EmailType.FEEDBACK_UNPUBLISHED.toString());
        paramMap.put(ParamsNames.EMAIL_FEEDBACK, fs.getFeedbackSessionName());
        paramMap.put(ParamsNames.EMAIL_COURSE, fs.getCourseId());
        
        return paramMap;
    }

}
