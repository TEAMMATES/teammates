package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorFeedbackEditPageAction;
import teammates.ui.controller.ShowPageResult;

/**
 * SUT: {@link InstructorFeedbackEditPageAction}.
 */
public class InstructorFeedbackEditPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1OfCourse1.googleId);

        // declare all variables to be used
        String expectedString = "";
        FeedbackSessionAttributes feedbackSessionAttributes;
        String[] submissionParams;
        InstructorFeedbackEditPageAction instructorFeedbackEditPageAction;
        ShowPageResult showPageResult;

        ______TS("typical success case");

        feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session1InCourse1");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_ENABLE_EDIT, "true"
        };

        instructorFeedbackEditPageAction = getAction(submissionParams);
        showPageResult = getShowPageResult(instructorFeedbackEditPageAction);

        expectedString = getPageResultDestination(
                Const.ViewURIs.INSTRUCTOR_FEEDBACK_EDIT, false, instructor1OfCourse1.googleId);
        assertEquals(expectedString, showPageResult.getDestinationWithParams());

        assertEquals("", showPageResult.getStatusMessage());

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditPage|||instructorFeedbackEditPage|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||instructorFeedbackEdit "
                + "Page Load<br>Editing information for Feedback Session "
                + "<span class=\"bold\">[" + feedbackSessionAttributes.getFeedbackSessionName() + "]</span>"
                + "in Course: <span class=\"bold\">[idOfTypicalCourse1]</span>"
                + "|||/page/instructorFeedbackEditPage";
        AssertHelper.assertLogMessageEquals(expectedString, instructorFeedbackEditPageAction.getLogMessage());

        ______TS("failure 1: non-existent feedback session");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "randomName for Session123",
                Const.ParamsNames.FEEDBACK_SESSION_ENABLE_EDIT, "true"
        };

        instructorFeedbackEditPageAction = getAction(submissionParams);
        try {
            showPageResult = getShowPageResult(instructorFeedbackEditPageAction);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException uae) {
            assertEquals("Trying to access system using a non-existent feedback session entity",
                         uae.getMessage());
        }
    }

    @Override
    protected InstructorFeedbackEditPageAction getAction(String... params) {
        return (InstructorFeedbackEditPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_SESSION_ENABLE_EDIT, "true"
        };

        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
