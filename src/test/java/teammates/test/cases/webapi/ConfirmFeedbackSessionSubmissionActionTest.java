package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.ConfirmFeedbackSessionSubmissionAction;
import teammates.ui.webapi.action.ConfirmFeedbackSessionSubmissionAction.ConfirmationResponse;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.JsonResult;

/**
 * SUT: {@link ConfirmFeedbackSessionSubmissionAction}.
 */
public class ConfirmFeedbackSessionSubmissionActionTest extends BaseActionTest<ConfirmFeedbackSessionSubmissionAction> {

    private StudentAttributes student1InCourse1;
    private StudentAttributes student4InCourse1;
    private StudentAttributes student1InCourse2;
    private InstructorAttributes instructor1OfCourse1;
    private InstructorAttributes instructor2OfCourse1;
    private InstructorAttributes instructor1OfCourse2;
    private CourseAttributes typicalCourse1;
    private CourseAttributes typicalCourse2;
    private FeedbackSessionAttributes session1InCourse1;
    private FeedbackSessionAttributes session1InCourse2;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SUBMISSION_CONFIRMATION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        useTypicalDataBundle();

        ______TS("Not enough parameters");

        loginAsStudent(student1InCourse1.getGoogleId());

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, typicalCourse1.getId());
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName());
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName());
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true");

        ______TS("Typical success case with student intent");

        loginAsStudent(student1InCourse1.getGoogleId());

        String[] studentParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };

        ConfirmFeedbackSessionSubmissionAction a = getAction(studentParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        ConfirmationResponse cr = (ConfirmationResponse) r.getOutput();
        assertEquals("Submission confirmed", cr.getMessage());

        ______TS("Typical success case with student intent, not responded before");

        loginAsStudent(student4InCourse1.getGoogleId());

        String[] studentNotRespondedParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };

        a = getAction(studentNotRespondedParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        cr = (ConfirmationResponse) r.getOutput();
        assertEquals("Submission confirmed", cr.getMessage());

        ______TS("Typical success case with instructor intent");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] instructorParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };

        a = getAction(instructorParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        cr = (ConfirmationResponse) r.getOutput();
        assertEquals("Submission confirmed", cr.getMessage());

        ______TS("Typical success case with instructor intent, not responded before");

        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        String[] instructorNotRespondedParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "false",
        };

        a = getAction(instructorNotRespondedParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        cr = (ConfirmationResponse) r.getOutput();
        assertEquals("Submission confirmed", cr.getMessage());

        ______TS("Failed case with invalid intent");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] invalidIntentParams = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };

        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntentParams).execute());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        useTypicalDataBundle();

        ______TS("Student intends to submit feedback session in other course, should not be accessible");

        loginAsStudent(student1InCourse2.googleId);

        String[] studentSubmitSessionInOtherCourseParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };

        assertThrows(EntityNotFoundException.class,
                () -> getAction(studentSubmitSessionInOtherCourseParams).checkAccessControl());

        ______TS("Student intends to submit feedback session in his course, should be accessible");

        loginAsStudent(student1InCourse1.googleId);

        String[] studentSubmitSessionInCourseParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };
        verifyCanAccess(studentSubmitSessionInCourseParams);

        ______TS("Student intends to submit feedback session only contains instructor questions"
                + ", should be accessible");

        loginAsStudent(student1InCourse2.googleId);

        String[] studentSubmitInstructorSessionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse2.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse2.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };
        verifyCanAccess(studentSubmitInstructorSessionParams);

        ______TS("Instructor intends to submit feedback session in other course, should not be accessible");

        loginAsInstructor(instructor1OfCourse1.googleId);

        String[] instructorSubmitSessionInOtherCourseParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse2.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse2.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };

        verifyCannotAccess(instructorSubmitSessionInOtherCourseParams);

        ______TS("Instructor intends to submit feedback session in his course, should be accessible");

        loginAsInstructor(instructor1OfCourse2.googleId);

        String[] instructorSubmitSessionInCourseParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse2.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse2.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };

        verifyCanAccess(instructorSubmitSessionInCourseParams);

        ______TS("Unknown intent, should be accessible");

        loginAsInstructor(instructor1OfCourse2.googleId);

        String[] unknownIntentParams = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse2.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse2.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };

        assertThrows(InvalidHttpParameterException.class, () -> getAction(unknownIntentParams).checkAccessControl());
    }

    private void useTypicalDataBundle() {
        removeAndRestoreTypicalDataBundle();
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        student4InCourse1 = typicalBundle.students.get("student4InCourse1");
        student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
        typicalCourse1 = typicalBundle.courses.get("typicalCourse1");
        typicalCourse2 = typicalBundle.courses.get("typicalCourse2");
        session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        session1InCourse2 = typicalBundle.feedbackSessions.get("session1InCourse2");
    }

}
