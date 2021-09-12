package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionData;

/**
 * SUT: {@link RestoreFeedbackSessionAction}.
 */
public class RestoreFeedbackSessionActionTest extends BaseActionTest<RestoreFeedbackSessionAction> {

    private final InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
    private final FeedbackSessionAttributes firstFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
    private final String instructorId = instructor1OfCourse1.getGoogleId();
    private final String courseId = instructor1OfCourse1.getCourseId();
    private final String feedbackSessionName = firstFeedbackSession.getFeedbackSessionName();
    private final String[] submissionParams = new String[] {
            Const.ParamsNames.COURSE_ID, courseId,
            Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
    };

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.BIN_SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    @Override
    protected void testExecute() {
        // See test cases below.
    }

    @Test
    protected void testExecute_withSessionInBin_shouldRestoreSession() throws Exception {
        loginAsInstructor(instructorId);
        logic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);
        RestoreFeedbackSessionAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        FeedbackSessionData feedbackSessionMessage = (FeedbackSessionData) result.getOutput();

        // Verify response
        assertEquals(courseId, feedbackSessionMessage.getCourseId());
        assertEquals(feedbackSessionName, feedbackSessionMessage.getFeedbackSessionName());

        // Verify model
        assertFalse(logic.getFeedbackSession(feedbackSessionName, courseId).isSessionDeleted());
    }

    @Test
    protected void testExecute_withSessionNotInBin_shouldFail() {
        loginAsInstructor(instructorId);

        EntityNotFoundException notFoundException = verifyEntityNotFound(submissionParams);
        assertEquals("Feedback session is not in recycle bin", notFoundException.getMessage());
    }

    @Test
    protected void testExecute_withEmptyParameters_shouldFail() {
        loginAsInstructor(instructorId);

        verifyHttpParameterFailure();
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        logic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }

}
