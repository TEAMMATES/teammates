package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackQuestionCopyPageAction;
import teammates.ui.controller.ShowPageResult;

/**
 * SUT: {@link InstructorFeedbackQuestionCopyPageAction}.
 */
public class InstructorFeedbackQuestionCopyPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_COPY_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("typical success case");

        FeedbackSessionAttributes feedbackSessionAttributes =
                typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName()
        };

        InstructorFeedbackQuestionCopyPageAction action = getAction(submissionParams);
        ShowPageResult result = getShowPageResult(action);

        String expectedString = getPageResultDestination(
                Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_COPY_MODAL, false, instructor1OfCourse1.googleId);
        assertEquals(expectedString, result.getDestinationWithParams());

        assertTrue(result.getStatusMessage().isEmpty());

        ______TS("failure: non-existent feedback session");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "Non-existent Session Name"
        };

        action = getAction(submissionParams);
        try {
            result = getShowPageResult(action);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException uae) {
            assertEquals("Trying to access system using a non-existent feedback session entity",
                         uae.getMessage());
        }

        ______TS("failure: unsufficient permissions");
        gaeSimulation.loginAsInstructor(typicalBundle.accounts.get("helperOfCourse1").googleId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName()
        };

        action = getAction(submissionParams);
        try {
            result = getShowPageResult(action);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException uae) {
            assertEquals("Feedback session [First feedback session] is not accessible "
                         + "to instructor [helper@course1.tmt] for privilege [canmodifysession]",
                         uae.getMessage());
        }
    }

    @Override
    protected InstructorFeedbackQuestionCopyPageAction getAction(String... params) {
        return (InstructorFeedbackQuestionCopyPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
        verifyUnaccessibleWithoutModifySessionPrivilege(params);
    }
}
