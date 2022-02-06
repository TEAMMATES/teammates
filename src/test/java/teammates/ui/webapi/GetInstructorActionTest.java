package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.InstructorData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetInstructorAction}.
 */
public class GetInstructorActionTest extends BaseActionTest<GetInstructorAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Not enough parameters");
        verifyHttpParameterFailure();
        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId());
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_SESSION_NAME,
                feedbackSessionAttributes.getFeedbackSessionName());
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName());

        ______TS("Typical Success Case with INSTRUCTOR_SUBMISSION");
        String[] params = {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        GetInstructorAction getInstructorAction = getAction(params);
        JsonResult actionOutput = getJsonResult(getInstructorAction);

        InstructorData response = (InstructorData) actionOutput.getOutput();
        assertEquals(instructor1OfCourse1.getName(), response.getName());
        assertNull(response.getGoogleId());
        assertNull(response.getKey());

        ______TS("Typical Success Case with FULL_DETAIL");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        getInstructorAction = getAction(params);
        actionOutput = getJsonResult(getInstructorAction);

        response = (InstructorData) actionOutput.getOutput();
        assertEquals(instructor1OfCourse1.getName(), response.getName());

        ______TS("Course ID given but Course is non existent (INSTRUCTOR_SUBMISSION)");

        String[] invalidCourseParams = new String[] {
                Const.ParamsNames.COURSE_ID, "1234A",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(invalidCourseParams);
        assertEquals("Instructor could not be found for this course", enfe.getMessage());

        ______TS("Instructor not found case with FULL_DETAIL");
        invalidCourseParams = new String[] {
                Const.ParamsNames.COURSE_ID, "Unknown",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        enfe = verifyEntityNotFound(invalidCourseParams);
        assertEquals("Instructor could not be found for this course", enfe.getMessage());

        ______TS("Intent is specified as STUDENT_SUBMISSION");

        String[] invalidIntentParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyHttpParameterFailure(invalidIntentParams);

        ______TS("Intent is specified as something new");

        invalidIntentParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, "RANDOM INTENT",
        };

        verifyHttpParameterFailure(invalidIntentParams);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 =
                logic.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.tmt");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("only instructors of the same course with correct privilege can access");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyCanAccess(submissionParams);

        ______TS("unregistered instructor is accessible with key");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.REGKEY, instructor1OfCourse1.getKey(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyAccessibleForUnregisteredUsers(submissionParams);

        ______TS("need login for FULL_DETAILS intent");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyAnyLoggedInUserCanAccess(submissionParams);
    }

}
