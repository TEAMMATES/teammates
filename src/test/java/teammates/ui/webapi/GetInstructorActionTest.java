package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.output.InstructorData;
import teammates.ui.output.MessageOutput;
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
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.googleId);

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

        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());
        InstructorData response = (InstructorData) actionOutput.getOutput();
        assertEquals(instructor1OfCourse1.name, response.getName());
        assertNull(response.getGoogleId());
        assertNull(response.getKey());

        ______TS("Typical Success Case with FULL_DETAIL");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        getInstructorAction = getAction(params);
        actionOutput = getJsonResult(getInstructorAction);

        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());
        response = (InstructorData) actionOutput.getOutput();
        assertEquals(instructor1OfCourse1.getName(), response.getName());

        ______TS("Course ID given but Course is non existent (INSTRUCTOR_SUBMISSION)");

        String[] invalidCourseParams = new String[] {
                Const.ParamsNames.COURSE_ID, "1234A",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        GetInstructorAction invalidCourseAction = getAction(invalidCourseParams);
        JsonResult invalidCourseOutput = getJsonResult(invalidCourseAction);
        MessageOutput invalidCourseMsg = (MessageOutput) invalidCourseOutput.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, invalidCourseOutput.getStatusCode());
        assertEquals("Instructor could not be found for this course", invalidCourseMsg.getMessage());

        ______TS("Instructor not found case with FULL_DETAIL");
        invalidCourseParams = new String[] {
                Const.ParamsNames.COURSE_ID, "Unknown",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        invalidCourseAction = getAction(invalidCourseParams);
        invalidCourseOutput = getJsonResult(invalidCourseAction);
        invalidCourseMsg = (MessageOutput) invalidCourseOutput.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, invalidCourseOutput.getStatusCode());
        assertEquals("Instructor could not be found for this course", invalidCourseMsg.getMessage());

        ______TS("Intent is specified as STUDENT_SUBMISSION");

        assertThrows(InvalidHttpParameterException.class, () -> {
            String[] invalidIntentParams = new String[] {
                    Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                    Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                    Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
            };

            getAction(new GetInstructorAction(), invalidIntentParams).execute();
        });

        ______TS("Intent is specified as something new");

        assertThrows(IllegalArgumentException.class, () -> {
            String[] invalidIntentParams = new String[] {
                    Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                    Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                    Const.ParamsNames.INTENT, "RANDOM INTENT",
            };

            getAction(new GetInstructorAction(), invalidIntentParams).execute();
        });
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("only instructors of the same course with correct privilege can access");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, submissionParams);

        ______TS("feedback session does not exist");

        assertThrows(EntityNotFoundException.class, () -> {
            String[] invalidFeedbackSessionParams = new String[] {
                    Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                    Const.ParamsNames.FEEDBACK_SESSION_NAME, "TEST_SESSION",
                    Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
            };

            verifyAccessibleForInstructorsOfTheSameCourse(invalidFeedbackSessionParams);
        });

        ______TS("need login for FULL_DETAILS intent");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyAnyLoggedInUserCanAccess(submissionParams);
    }

}
