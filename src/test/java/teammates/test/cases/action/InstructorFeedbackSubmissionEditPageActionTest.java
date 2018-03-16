package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackSubmissionEditPageAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

/**
 * SUT: {@link InstructorFeedbackSubmissionEditPageAction}.
 */
public class InstructorFeedbackSubmissionEditPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("not enough parameters");

        String[] paramsWithoutCourseId = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName()
        };
        String[] paramsWithoutFeedbackSessionName = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId()
        };

        verifyAssumptionFailure(paramsWithoutCourseId);
        verifyAssumptionFailure(paramsWithoutFeedbackSessionName);

        ______TS("Test null feedback session name parameter");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.USER_ID, instructor.googleId
        };

        InstructorFeedbackSubmissionEditPageAction a;
        ShowPageResult r;

        try {
            a = getAction(submissionParams);
            r = getShowPageResult(a);
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                                       Const.ParamsNames.FEEDBACK_SESSION_NAME), e.getMessage());
        }

        ______TS("Test null course id parameter");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.USER_ID, instructor.googleId
        };

        try {
            a = getAction(submissionParams);
            r = getShowPageResult(a);
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                                       Const.ParamsNames.COURSE_ID), e.getMessage());
        }

        ______TS("Test insufficient authorization");

        instructor = typicalBundle.instructors.get("helperOfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.USER_ID, instructor.googleId
        };

        try {
            a = getAction(submissionParams);
            r = getShowPageResult(a);
            signalFailureToDetectException("Did not detect insufficient authorization.");
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor "
                         + "[helper@course1.tmt] for this purpose", e.getMessage());
        }

        ______TS("Test feedback session that does not exist");

        instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "feedback session that does not exist",
                Const.ParamsNames.USER_ID, instructor.googleId
        };

        a = getAction(submissionParams);
        RedirectResult rr = getRedirectResult(a);

        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_HOME_PAGE, false, instructor.googleId),
                rr.getDestinationWithParams());
        assertFalse(rr.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_DELETED_NO_ACCESS,
                     rr.getStatusMessage());

        ______TS("typical success case");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.USER_ID, instructor.googleId
        };

        a = getAction(params);
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, false, instructor.googleId),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_SUBMISSIONS_CAN_SUBMIT_PARTIAL_ANSWER, r.getStatusMessage());

        ______TS("masquerade mode");

        gaeSimulation.loginAsAdmin("admin.user");

        a = getAction(params);
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, false, instructor.googleId),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_SUBMISSIONS_CAN_SUBMIT_PARTIAL_ANSWER, r.getStatusMessage());

        ______TS("closed session case");

        gaeSimulation.loginAsInstructor(instructor.googleId);

        session = typicalBundle.feedbackSessions.get("closedSession");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.USER_ID, instructor.googleId
        };

        a = getAction(params);
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, false, instructor.googleId),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN, r.getStatusMessage());

    }

    @Override
    protected InstructorFeedbackSubmissionEditPageAction getAction(String... params) {
        return (InstructorFeedbackSubmissionEditPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName()
        };
        verifyUnaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
