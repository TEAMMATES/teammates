package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.InstructorFeedbackUnpublishAction;
import teammates.ui.controller.RedirectResult;

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
        gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session2InCourse1");
        String[] unpublishParams = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName
        };
        
        ______TS("Typical Case: ensure Unpublish Action can be executed");
        
        makeFeedbackSessionPublished(session);
        
        InstructorFeedbackUnpublishAction unpublishAction = getAction(unpublishParams);
        RedirectResult result = (RedirectResult) unpublishAction.executeAndPostProcess();
        
        String expectedDestination = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE + 
                "?message=The+feedback+session+has+been+unpublished." + 
                "&error=false" + 
                "&user=idOfInstructor1OfCourse1";
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED, result.getStatusMessage());
        assertFalse(result.isError);
    }

    private void makeFeedbackSessionPublished(FeedbackSessionAttributes session) throws Exception {
        session.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        session.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session.resultsVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        session.sentPublishedEmail = true;
        assertTrue(session.isPublished());
        new FeedbackSessionsDb().updateFeedbackSession(session);
    }
    
    private InstructorFeedbackUnpublishAction getAction(String[] params){
        return (InstructorFeedbackUnpublishAction) gaeSimulation.getActionObject(uri, params);
    }
}
