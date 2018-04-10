package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorFeedbackPreviewAsInstructorAction;
import teammates.ui.controller.ShowPageResult;

/**
 * SUT: {@link InstructorFeedbackPreviewAsInstructorAction}.
 */
public class InstructorFeedbackPreviewAsInstructorActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2 = typicalBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes instructorHelper = typicalBundle.instructors.get("helperOfCourse1");
        String idOfInstructor1 = instructor1.googleId;
        String idOfInstructor2 = instructor2.googleId;
        String idOfInstructorHelper = instructorHelper.googleId;

        gaeSimulation.loginAsInstructor(idOfInstructor1);

        ______TS("typical success case");

        String feedbackSessionName = "First feedback session";
        String courseId = "idOfTypicalCourse1";
        String previewAsEmail = instructor2.email;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, previewAsEmail
        };

        InstructorFeedbackPreviewAsInstructorAction paia = getAction(submissionParams);
        ShowPageResult showPageResult = getShowPageResult(paia);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, false, idOfInstructor1),
                showPageResult.getDestinationWithParams());

        assertEquals("", showPageResult.getStatusMessage());

        AssertHelper.assertLogMessageEquals(
                "TEAMMATESLOG|||instructorFeedbackPreviewAsInstructor|||instructorFeedbackPreviewAsInstructor"
                + "|||true|||Instructor|||Instructor 1 of Course 1"
                + "|||" + idOfInstructor1 + "|||instr1@course1.tmt|||"
                + "Preview feedback session as instructor (" + instructor2.email + ")<br>"
                + "Session Name: First feedback session<br>Course ID: " + instructor1.courseId
                + "|||/page/instructorFeedbackPreviewAsInstructor",
                paia.getLogMessage());

        gaeSimulation.loginAsInstructor(idOfInstructor2);

        ______TS("typical success case");

        feedbackSessionName = "First feedback session";
        courseId = "idOfTypicalCourse1";
        previewAsEmail = instructor1.email;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, previewAsEmail
        };

        paia = getAction(submissionParams);
        showPageResult = getShowPageResult(paia);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, false, idOfInstructor2),
                showPageResult.getDestinationWithParams());

        assertEquals("", showPageResult.getStatusMessage());

        AssertHelper.assertLogMessageEquals(
                "TEAMMATESLOG|||instructorFeedbackPreviewAsInstructor|||instructorFeedbackPreviewAsInstructor"
                + "|||true|||Instructor|||Instructor 2 of Course 1"
                + "|||" + idOfInstructor2 + "|||instr2@course1.tmt|||"
                + "Preview feedback session as instructor (" + instructor1.email + ")<br>"
                + "Session Name: First feedback session<br>Course ID: " + instructor1.courseId
                + "|||/page/instructorFeedbackPreviewAsInstructor",
                paia.getLogMessage());

        gaeSimulation.loginAsInstructor(idOfInstructorHelper);

        ______TS("failure: not enough privilege");

        feedbackSessionName = "First feedback session";
        courseId = "idOfTypicalCourse1";
        previewAsEmail = instructor2.email;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, previewAsEmail
        };

        try {
            paia = getAction(submissionParams);
            showPageResult = getShowPageResult(paia);
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor ["
                         + instructorHelper.email + "] for privilege [canmodifysession]", e.getMessage());
        }

        gaeSimulation.loginAsInstructor(idOfInstructor1);

        ______TS("failure: non-existent previewas email");

        previewAsEmail = "non-existentEmail@course13212.tmt";

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, previewAsEmail
        };

        try {
            paia = getAction(submissionParams);
            showPageResult = getShowPageResult(paia);
            signalFailureToDetectException();
        } catch (EntityNotFoundException enfe) {
            assertEquals("Instructor Email " + previewAsEmail + " does not exist in " + courseId + ".",
                         enfe.getMessage());
        }
    }

    @Override
    protected InstructorFeedbackPreviewAsInstructorAction getAction(String... params) {
        return (InstructorFeedbackPreviewAsInstructorAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.PREVIEWAS, instructor.email
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
