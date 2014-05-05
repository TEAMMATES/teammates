package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.FeedbackSessionsDb;

public class InstructorFeedbackUnpublishActionTest extends BaseActionTest {
    DataBundle dataBundle;
        
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_UNPUBLISH;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        
        makeFeedbackSessionPublished(session); //we have to revert to the closed state
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName 
        };
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        
        makeFeedbackSessionPublished(session); //we have to revert to the closed state
        
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        //TODO: implement this
        //TODO: ensure cannot unpublish if not published already
    }

    private void makeFeedbackSessionPublished(FeedbackSessionAttributes session) throws Exception {
        session.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        session.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session.resultsVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session.sentPublishedEmail = true;
        assertTrue(session.isPublished());
        new FeedbackSessionsDb().updateFeedbackSession(session);
    }
}
