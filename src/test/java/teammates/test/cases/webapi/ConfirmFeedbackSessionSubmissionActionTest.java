package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.ui.webapi.action.ConfirmFeedbackSessionSubmissionAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.ConfirmationResponse;
import teammates.ui.webapi.request.Intent;

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
    private FeedbackSessionAttributes awaitingSession;
    private List<FeedbackResponseAttributes> allResponsesInSession1Course1;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SUBMISSION_CONFIRMATION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    protected void prepareTestData() {
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
        awaitingSession = typicalBundle.feedbackSessions.get("awaiting.session");
        FeedbackResponseAttributes response1ForQ1S1C1 = typicalBundle.feedbackResponses.get("response1ForQ1S1C1");
        FeedbackResponseAttributes response2ForQ1S1C1 = typicalBundle.feedbackResponses.get("response2ForQ1S1C1");
        FeedbackResponseAttributes response1ForQ2S1C1 = typicalBundle.feedbackResponses.get("response1ForQ2S1C1");
        FeedbackResponseAttributes response2ForQ2S1C1 = typicalBundle.feedbackResponses.get("response2ForQ2S1C1");
        FeedbackResponseAttributes response3ForQ2S1C1 = typicalBundle.feedbackResponses.get("response3ForQ2S1C1");
        FeedbackResponseAttributes response1ForQ3S1C1 = typicalBundle.feedbackResponses.get("response1ForQ3S1C1");
        allResponsesInSession1Course1 = new ArrayList<>();
        allResponsesInSession1Course1.add(response1ForQ1S1C1);
        allResponsesInSession1Course1.add(response2ForQ1S1C1);
        allResponsesInSession1Course1.add(response1ForQ2S1C1);
        allResponsesInSession1Course1.add(response2ForQ2S1C1);
        allResponsesInSession1Course1.add(response3ForQ2S1C1);
        allResponsesInSession1Course1.add(response1ForQ3S1C1);
    }

    @Test
    @Override
    protected void testExecute() throws Exception {

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

        // verify 1 email sent
        verifyNumberOfEmailsSent(a, 1);

        EmailWrapper email = a.getEmailSender().getEmailsSent().get(0);

        assertEquals(student1InCourse1.email, email.getRecipient());

        // verify update session's respondent list task added
        TaskWrapper taskAdded = a.getTaskQueuer().getTasksAdded().get(0);

        assertEquals(typicalCourse1.getId(), taskAdded.getParamMap().get(Const.ParamsNames.COURSE_ID)[0]);
        assertEquals(session1InCourse1.getFeedbackSessionName(),
                taskAdded.getParamMap().get(Const.ParamsNames.FEEDBACK_SESSION_NAME)[0]);
        assertEquals(student1InCourse1.email, taskAdded.getParamMap().get(Const.ParamsNames.RESPONDENT_EMAIL)[0]);
        assertEquals("false", taskAdded.getParamMap().get(Const.ParamsNames.RESPONDENT_IS_INSTRUCTOR)[0]);
        assertEquals("false", taskAdded.getParamMap().get(Const.ParamsNames.RESPONDENT_IS_TO_BE_REMOVED)[0]);

        ______TS("Typical success case with student intent, not responded before");

        loginAsStudent(student4InCourse1.getGoogleId());

        for (FeedbackResponseAttributes res : allResponsesInSession1Course1) {
            assertNotEquals(student4InCourse1.getEmail(), res.getGiver());
        }

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

        // verify 1 email sent
        verifyNumberOfEmailsSent(a, 1);

        email = a.getEmailSender().getEmailsSent().get(0);

        assertEquals(student4InCourse1.email, email.getRecipient());

        // verify update session's respondent list task added
        taskAdded = a.getTaskQueuer().getTasksAdded().get(0);

        assertEquals(typicalCourse1.getId(), taskAdded.getParamMap().get(Const.ParamsNames.COURSE_ID)[0]);
        assertEquals(session1InCourse1.getFeedbackSessionName(),
                taskAdded.getParamMap().get(Const.ParamsNames.FEEDBACK_SESSION_NAME)[0]);
        assertEquals(student4InCourse1.email, taskAdded.getParamMap().get(Const.ParamsNames.RESPONDENT_EMAIL)[0]);
        assertEquals("false", taskAdded.getParamMap().get(Const.ParamsNames.RESPONDENT_IS_INSTRUCTOR)[0]);
        assertEquals("true", taskAdded.getParamMap().get(Const.ParamsNames.RESPONDENT_IS_TO_BE_REMOVED)[0]);

        ______TS("Typical success case with instructor intent");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] instructorParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "false",
        };

        a = getAction(instructorParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        cr = (ConfirmationResponse) r.getOutput();
        assertEquals("Submission confirmed", cr.getMessage());

        verifyNumberOfEmailsSent(a, 0);

        // verify update session's respondent list task added
        taskAdded = a.getTaskQueuer().getTasksAdded().get(0);

        assertEquals(typicalCourse1.getId(), taskAdded.getParamMap().get(Const.ParamsNames.COURSE_ID)[0]);
        assertEquals(session1InCourse1.getFeedbackSessionName(),
                taskAdded.getParamMap().get(Const.ParamsNames.FEEDBACK_SESSION_NAME)[0]);
        assertEquals(instructor1OfCourse1.email, taskAdded.getParamMap().get(Const.ParamsNames.RESPONDENT_EMAIL)[0]);
        assertEquals("true", taskAdded.getParamMap().get(Const.ParamsNames.RESPONDENT_IS_INSTRUCTOR)[0]);
        assertEquals("false", taskAdded.getParamMap().get(Const.ParamsNames.RESPONDENT_IS_TO_BE_REMOVED)[0]);

        ______TS("Typical success case with instructor intent, not responded before");

        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        for (FeedbackResponseAttributes res : allResponsesInSession1Course1) {
            assertNotEquals(instructor2OfCourse1.getEmail(), res.getGiver());
        }

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

        verifyNumberOfEmailsSent(a, 0);

        // verify update session's respondent list task added
        taskAdded = a.getTaskQueuer().getTasksAdded().get(0);

        assertEquals(typicalCourse1.getId(), taskAdded.getParamMap().get(Const.ParamsNames.COURSE_ID)[0]);
        assertEquals(session1InCourse1.getFeedbackSessionName(),
                taskAdded.getParamMap().get(Const.ParamsNames.FEEDBACK_SESSION_NAME)[0]);
        assertEquals(instructor2OfCourse1.email, taskAdded.getParamMap().get(Const.ParamsNames.RESPONDENT_EMAIL)[0]);
        assertEquals("true", taskAdded.getParamMap().get(Const.ParamsNames.RESPONDENT_IS_INSTRUCTOR)[0]);
        assertEquals("true", taskAdded.getParamMap().get(Const.ParamsNames.RESPONDENT_IS_TO_BE_REMOVED)[0]);

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

        ______TS("preview mode, cannot access");

        loginAsInstructor(instructor1OfCourse1.googleId);

        String[] previewParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
                Const.ParamsNames.PREVIEWAS, student1InCourse1.email,
        };

        verifyCannotAccess(previewParams);

        ______TS("session not open for submission but in moderation mode, can access");

        loginAsInstructor(instructor1OfCourse1.googleId);

        assertFalse(awaitingSession.isOpened());

        String[] moderationParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, awaitingSession.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, student1InCourse1.getEmail(),
        };

        verifyCanAccess(moderationParams);

        ______TS("session not open for submission, cannot access");

        loginAsStudent(student1InCourse1.googleId);

        assertFalse(awaitingSession.isOpened());
        String[] sessionNotOpenSubmissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, awaitingSession.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };

        verifyCannotAccess(sessionNotOpenSubmissionParams);

        ______TS("Student intends to submit feedback session in other course, should not be accessible");

        loginAsStudent(student1InCourse2.googleId);

        String[] studentSubmitSessionInOtherCourseParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };

        verifyCannotAccess(studentSubmitSessionInOtherCourseParams);

        ______TS("Student intends to submit feedback session in his course, should be accessible");

        loginAsStudent(student1InCourse1.googleId);

        String[] studentSubmitSessionInCourseParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
                Const.ParamsNames.SEND_SUBMISSION_EMAIL, "true",
        };
        verifyCanAccess(studentSubmitSessionInCourseParams);

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

}
