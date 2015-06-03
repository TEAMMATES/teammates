package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.Action;
import teammates.ui.controller.RedirectResult;

public class InstructorFeedbackDeleteActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_DELETE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
        };
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        assertNotNull(fsDb.getFeedbackSession(fs.courseId, fs.feedbackSessionName));
        
        Action a = gaeSimulation.getActionObject(uri, submissionParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();
        
        assertNull(fsDb.getFeedbackSession(fs.courseId, fs.feedbackSessionName));
        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE
                         + "?error=false&user=idOfInstructor1OfCourse1", 
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_DELETED, r.getStatusMessage());
        assertEquals(false, r.isError);
    }    
}
