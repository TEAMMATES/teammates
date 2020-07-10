package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.action.DeleteFeedbackResponseAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.request.Intent;

/**
 * SUT: {@link DeleteFeedbackResponseAction}.
 */
public class DeleteFeedbackResponseActionTest extends BaseActionTest<DeleteFeedbackResponseAction> {

    private StudentAttributes student1InCourse1;
    private StudentAttributes student2InCourse1;
    private StudentAttributes student5InCourse1;
    private InstructorAttributes instructor2OfCourse1;
    private InstructorAttributes instructor1OfCourse1;
    private FeedbackResponseAttributes typicalResponse;
    private FeedbackResponseAttributes testModerateResponse;
    private FeedbackResponseAttributes typicalResponse2;
    private FeedbackResponseAttributes typicalResponse3;
    private FeedbackResponseAttributes responseInClosedSession;
    private FeedbackSessionAttributes closedSession;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    protected void prepareTestData() {
        removeAndRestoreTypicalDataBundle();
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        StudentAttributes student4inCourse1 = typicalBundle.students.get("student4InCourse1");
        student5InCourse1 = typicalBundle.students.get("student5InCourse1");
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSessionAttributes session2 = typicalBundle.feedbackSessions.get("session2InCourse1");
        closedSession = typicalBundle.feedbackSessions.get("closedSession");

        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(
                session.getFeedbackSessionName(), session.getCourseId(), 1);

        String giverEmail = student1InCourse1.getEmail();
        String receiverEmail = student1InCourse1.getEmail();
        typicalResponse = logic.getFeedbackResponse(question.getId(),
                giverEmail, receiverEmail);

        FeedbackQuestionAttributes testModerateQuestion = logic.getFeedbackQuestion(
                session.getFeedbackSessionName(), session.getCourseId(), 2);
        testModerateResponse = logic.getFeedbackResponse(testModerateQuestion.getId(),
                student2InCourse1.getEmail(), student5InCourse1.getEmail());

        FeedbackQuestionAttributes question2 = logic.getFeedbackQuestion(
                session2.getFeedbackSessionName(), session2.getCourseId(), 1);
        typicalResponse2 = logic.getFeedbackResponse(question2.getId(),
                student4inCourse1.getEmail(), "Team 1.2");

        FeedbackQuestionAttributes question3 = logic.getFeedbackQuestion(
                session.getFeedbackSessionName(), session.getCourseId(), 3);
        typicalResponse3 = logic.getFeedbackResponse(question3.getId(),
                instructor1OfCourse1.getEmail(), "%GENERAL%");

        FeedbackQuestionAttributes question4 = logic.getFeedbackQuestion(
                closedSession.getFeedbackSessionName(), closedSession.getCourseId(), 1);
        responseInClosedSession = logic.getFeedbackResponse(question4.getId(),
                instructor1OfCourse1.getEmail(), instructor1OfCourse1.getEmail());
    }

    @Test
    @Override
    protected void testExecute() throws Exception {

        loginAsStudent(student1InCourse1.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString());

        ______TS("Unencrypted responseId");

        String[] invalidParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, typicalResponse.getId(),
        };
        verifyHttpParameterFailure(invalidParams);

        ______TS("Typical success case, student");

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(typicalResponse.getId()),
        };

        DeleteFeedbackResponseAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        // response is deleted
        assertNull(logic.getFeedbackResponse(typicalResponse.getId()));

        // delete the response again, throw NullPointerException
        a = getAction(params);
        DeleteFeedbackResponseAction finalAction = a;
        assertThrows(NullPointerException.class, () -> getJsonResult(finalAction));

        ______TS("Typical success case, instructor");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(typicalResponse3.getId()),
        };

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        // response is deleted
        assertNull(logic.getFeedbackResponse(typicalResponse3.getId()));

        // delete the response again, throw NullPointerException
        a = getAction(params);
        DeleteFeedbackResponseAction finalA = a;
        assertThrows(NullPointerException.class, () -> getJsonResult(finalA));
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {

        ______TS("wrong giver type");

        loginAsStudent(student1InCourse1.getGoogleId());

        String[] wrongGiverTypeParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(typicalResponse.getId()),
        };

        verifyCannotAccess(wrongGiverTypeParams);

        ______TS("preview mode, cannot access");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] previewParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(typicalResponse.getId()),
                Const.ParamsNames.PREVIEWAS, instructor1OfCourse1.email,
        };

        verifyCannotAccess(previewParams);

        ______TS("response in session not open, cannot access");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        assertFalse(closedSession.isOpened());

        String[] sessionNotOpenParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseInClosedSession.getId()),
        };

        verifyCannotAccess(sessionNotOpenParams);

        ______TS("Response contains question not intended shown to instructor, "
                + "moderated instructor should not be accessible");

        loginAsInstructor(instructor1OfCourse1.googleId);

        assertFalse(logic.getFeedbackQuestion(typicalResponse2.getFeedbackQuestionId())
                .getShowResponsesTo().contains(FeedbackParticipantType.INSTRUCTORS));

        String[] invalidModeratedInstructorSubmissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(typicalResponse2.getId()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, instructor1OfCourse1.getEmail(),
        };

        verifyCannotAccess(invalidModeratedInstructorSubmissionParams);

        ______TS("Instructor moderates student's response, but response not given by moderated student, "
                + "should not be accessible");

        loginAsInstructor(instructor1OfCourse1.googleId);

        assertEquals(FeedbackParticipantType.STUDENTS,
                logic.getFeedbackQuestion(testModerateResponse.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(student1InCourse1.getEmail(), testModerateResponse.getGiver());

        String[] moderatedStudentSubmissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(testModerateResponse.getId()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, student1InCourse1.getEmail(),
        };
        verifyCannotAccess(moderatedStudentSubmissionParams);

        ______TS("non-existent feedback response");

        loginAsStudent(student1InCourse1.getGoogleId());

        String[] nonExistParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt("randomNonExistId"),
        };

        assertThrows(EntityNotFoundException.class, () -> getAction(nonExistParams).checkAccessControl());

        ______TS("Student intends to access other person's response, should not be accessible");

        loginAsStudent(student2InCourse1.getGoogleId());

        assertEquals(FeedbackParticipantType.STUDENTS,
                logic.getFeedbackQuestion(typicalResponse.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(student2InCourse1.getEmail(), typicalResponse.getGiver());

        String[] studentAccessOtherPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(typicalResponse.getId()),
        };

        verifyCannotAccess(studentAccessOtherPersonParams);

        ______TS("Student intends to access own response, should be accessible");

        loginAsStudent(student1InCourse1.getGoogleId());

        assertEquals(student1InCourse1.getEmail(), typicalResponse.getGiver());

        String[] studentAccessOwnPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(typicalResponse.getId()),
        };

        verifyCanAccess(studentAccessOwnPersonParams);

        ______TS("Student intends to access same team's response, should be accessible");

        loginAsStudent(student1InCourse1.getGoogleId());

        assertEquals(FeedbackParticipantType.TEAMS,
                logic.getFeedbackQuestion(typicalResponse2.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(student1InCourse1.getTeam(), typicalResponse2.getGiver());

        String[] studentAccessOSameTeamParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(typicalResponse2.getId()),
        };

        verifyCanAccess(studentAccessOSameTeamParams);

        ______TS("Student intends to access other team's response, should not be accessible");

        loginAsStudent(student5InCourse1.getGoogleId());

        assertEquals(FeedbackParticipantType.TEAMS,
                logic.getFeedbackQuestion(typicalResponse2.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(student5InCourse1.getTeam(), typicalResponse2.getGiver());

        String[] studentAccessOtherTeamParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(typicalResponse2.getId()),
        };

        verifyCannotAccess(studentAccessOtherTeamParams);

        ______TS("Instructor intends to access other person's response, should not be accessible");

        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        assertEquals(FeedbackParticipantType.SELF,
                logic.getFeedbackQuestion(typicalResponse3.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(instructor2OfCourse1.getEmail(), typicalResponse3.getGiver());

        String[] instructorAccessOtherPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(typicalResponse3.getId()),
        };

        verifyCannotAccess(instructorAccessOtherPersonParams);

        ______TS("Instructor intends to access own response, should be accessible");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        assertEquals(instructor1OfCourse1.getEmail(), typicalResponse3.getGiver());

        String[] instructorAccessOwnPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(typicalResponse3.getId()),
        };

        verifyCanAccess(instructorAccessOwnPersonParams);

        ______TS("Unknown intent, should not be accessible");

        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        String[] unknownIntentParams = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(typicalResponse3.getId()),
        };

        assertThrows(InvalidHttpParameterException.class, () -> getAction(unknownIntentParams).checkAccessControl());
    }

}
