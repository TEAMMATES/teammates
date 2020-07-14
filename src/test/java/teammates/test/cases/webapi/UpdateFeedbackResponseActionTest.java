package teammates.test.cases.webapi;

import java.util.ArrayList;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.test.driver.AssertHelper;
import teammates.ui.webapi.action.UpdateFeedbackResponseAction;
import teammates.ui.webapi.request.FeedbackResponseUpdateRequest;
import teammates.ui.webapi.request.Intent;

/**
 * SUT: {@link UpdateFeedbackResponseAction}.
 */
public class UpdateFeedbackResponseActionTest extends BaseActionTest<UpdateFeedbackResponseAction> {

    private StudentAttributes student1InCourse1;
    private InstructorAttributes instructor1OfCourse1;
    private FeedbackResponseAttributes student1ResponseToStudent1;
    private FeedbackResponseAttributes instructor1ResponseToAll;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    protected void prepareTestData() {
        removeAndRestoreTypicalDataBundle();
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(
                session.getFeedbackSessionName(), session.getCourseId(), 1);

        String giverEmail = student1InCourse1.getEmail();
        String receiverEmail = student1InCourse1.getEmail();
        student1ResponseToStudent1 = logic.getFeedbackResponse(question.getId(),
                giverEmail, receiverEmail);

        FeedbackQuestionAttributes question3 = logic.getFeedbackQuestion(
                session.getFeedbackSessionName(), session.getCourseId(), 3);
        instructor1ResponseToAll = logic.getFeedbackResponse(question3.getId(),
                instructor1OfCourse1.getEmail(), "%GENERAL%");
    }

    @Override
    protected void testExecute() throws Exception {
        // See individual test cases below
    }

    @Test
    protected void testExecute_invalidParams_httpParameterFailure() {

        ______TS("missing intent response parameters");

        loginAsStudent(student1InCourse1.getGoogleId());

        String[] missingIntentParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(student1ResponseToStudent1.getId()),
        };

        verifyHttpParameterFailure(missingIntentParams);

        ______TS("missing response id parameters");

        String[] missingResponseIdParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyHttpParameterFailure(missingResponseIdParams);

        ______TS("unencrypted response id parameters");

        String[] unencryptedResponseId = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, student1ResponseToStudent1.getId(),
        };

        verifyHttpParameterFailure(unencryptedResponseId);
    }

    @Test
    public void testExecute_studentFeedbackSubmissionMcqGenerateOptionsForTeams_shouldValidateAnswer() throws Exception {
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");

        // create a question
        FeedbackMcqQuestionDetails feedbackMcqQuestionDetails = new FeedbackMcqQuestionDetails();
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS);
        FeedbackQuestionAttributes fqa = logic.createFeedbackQuestion(FeedbackQuestionAttributes.builder()
                .withCourseId(fsa.getCourseId())
                .withFeedbackSessionName(fsa.getFeedbackSessionName())
                .withNumberOfEntitiesToGiveFeedbackTo(2)
                .withQuestionDescription("test")
                .withQuestionNumber(1)
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withQuestionDetails(feedbackMcqQuestionDetails)
                .withShowResponsesTo(new ArrayList<>())
                .withShowGiverNameTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .build());

        // create a response
        FeedbackMcqResponseDetails feedbackMcqResponseDetails = new FeedbackMcqResponseDetails();
        feedbackMcqResponseDetails.setAnswer(studentAttributes.getTeam());
        FeedbackResponseAttributes feedbackResponse =
                FeedbackResponseAttributes
                        .builder(fqa.getId(), studentAttributes.getEmail(), studentAttributes.getEmail())
                        .withGiverSection(studentAttributes.getSection())
                        .withRecipientSection(studentAttributes.getSection())
                        .withCourseId(fqa.getCourseId())
                        .withFeedbackSessionName(fqa.getFeedbackSessionName())
                        .withResponseDetails(feedbackMcqResponseDetails)
                        .build();
        feedbackResponse = logic.createFeedbackResponse(feedbackResponse);

        FeedbackResponseUpdateRequest updateRequest = getUpdateRequest(studentAttributes.getEmail());

        String[] params = {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(feedbackResponse.getId()),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        InvalidHttpRequestBodyException e = assertThrows(InvalidHttpRequestBodyException.class, () -> {
            loginAsStudent(studentAttributes.getGoogleId());
            UpdateFeedbackResponseAction a = getAction(updateRequest, params);
            getJsonResult(a);
        });
        AssertHelper.assertContains(Const.FeedbackQuestion.MCQ_ERROR_INVALID_OPTION, e.getMessage());
    }

    @Test
    public void testExecute_instructorFeedbackSubmissionMcqGenerateOptionsForTeams_shouldValidateAnswer() throws Exception {
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructorAttributes = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");

        // create a question
        FeedbackMcqQuestionDetails feedbackMcqQuestionDetails = new FeedbackMcqQuestionDetails();
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS);
        FeedbackQuestionAttributes fqa = logic.createFeedbackQuestion(FeedbackQuestionAttributes.builder()
                .withCourseId(fsa.getCourseId())
                .withFeedbackSessionName(fsa.getFeedbackSessionName())
                .withNumberOfEntitiesToGiveFeedbackTo(2)
                .withQuestionDescription("test")
                .withQuestionNumber(1)
                .withGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withRecipientType(FeedbackParticipantType.INSTRUCTORS)
                .withQuestionDetails(feedbackMcqQuestionDetails)
                .withShowResponsesTo(new ArrayList<>())
                .withShowGiverNameTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .build());

        // create a response
        FeedbackMcqResponseDetails feedbackMcqResponseDetails = new FeedbackMcqResponseDetails();
        feedbackMcqResponseDetails.setAnswer(studentAttributes.getTeam());
        FeedbackResponseAttributes feedbackResponse =
                FeedbackResponseAttributes
                        .builder(fqa.getId(), instructorAttributes.getEmail(), instructorAttributes.getEmail())
                        .withGiverSection(Const.DEFAULT_SECTION)
                        .withRecipientSection(Const.DEFAULT_SECTION)
                        .withCourseId(fqa.getCourseId())
                        .withFeedbackSessionName(fqa.getFeedbackSessionName())
                        .withResponseDetails(feedbackMcqResponseDetails)
                        .build();
        feedbackResponse = logic.createFeedbackResponse(feedbackResponse);

        // send update request
        FeedbackResponseUpdateRequest updateRequest = getUpdateRequest(instructorAttributes.getEmail());

        String[] params = {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(feedbackResponse.getId()),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        InvalidHttpRequestBodyException e = assertThrows(InvalidHttpRequestBodyException.class, () -> {
            loginAsInstructor(instructorAttributes.getGoogleId());
            UpdateFeedbackResponseAction a = getAction(updateRequest, params);
            getJsonResult(a);
        });
        AssertHelper.assertContains(Const.FeedbackQuestion.MCQ_ERROR_INVALID_OPTION, e.getMessage());
    }

    private FeedbackResponseUpdateRequest getUpdateRequest(String recipientIdentifier) {
        FeedbackResponseUpdateRequest updateRequest = new FeedbackResponseUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.MCQ);
        updateRequest.setRecipientIdentifier(recipientIdentifier);
        FeedbackMcqResponseDetails newDetails = new FeedbackMcqResponseDetails();
        newDetails.setAnswer("TEAM_NOT_EXIST");
        updateRequest.setResponseDetails(newDetails);
        return updateRequest;
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        // See individual test cases below
    }

    @Test
    protected void testAccessControl_wrongGiver_inaccessible() {

        ______TS("wrong giver type");

        loginAsStudent(student1InCourse1.getGoogleId());

        String[] wrongGiverTypeParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(student1ResponseToStudent1.getId()),
        };

        verifyCannotAccess(wrongGiverTypeParams);
    }

    @Test
    protected void testAccessControl_previewMode_inaccessible() {

        ______TS("preview mode, cannot access");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] previewParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(student1ResponseToStudent1.getId()),
                Const.ParamsNames.PREVIEWAS, instructor1OfCourse1.email,
        };

        verifyCannotAccess(previewParams);

    }

    @Test
    protected void testAccessControl_responseSessionNotOpen_inaccessible() {

        ______TS("response in session not open, cannot access");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        FeedbackSessionAttributes closedSession = typicalBundle.feedbackSessions.get("closedSession");
        FeedbackQuestionAttributes question4 = logic.getFeedbackQuestion(
                closedSession.getFeedbackSessionName(), closedSession.getCourseId(), 1);
        FeedbackResponseAttributes responseInClosedSession = logic.getFeedbackResponse(question4.getId(),
                instructor1OfCourse1.getEmail(), instructor1OfCourse1.getEmail());

        assertFalse(closedSession.isOpened());

        String[] sessionNotOpenParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(responseInClosedSession.getId()),
        };

        verifyCannotAccess(sessionNotOpenParams);
    }

    @Test
    protected void testAccessControl_containsQuestionNotForInstructor_inaccessible() {

        ______TS("Responses not visible to instructors, should not be accessible to an instructor"
                + " even with course edit permissions.");

        StudentAttributes student4inCourse1 = typicalBundle.students.get("student4InCourse1");
        FeedbackSessionAttributes session2 = typicalBundle.feedbackSessions.get("session2InCourse1");
        FeedbackQuestionAttributes question2 = logic.getFeedbackQuestion(
                session2.getFeedbackSessionName(), session2.getCourseId(), 1);
        FeedbackResponseAttributes student4ResponseToTeam = logic.getFeedbackResponse(question2.getId(),
                student4inCourse1.getEmail(), "Team 1.2");

        loginAsInstructor(instructor1OfCourse1.googleId);

        assertFalse(logic.getFeedbackQuestion(student4ResponseToTeam.getFeedbackQuestionId())
                .getShowResponsesTo().contains(FeedbackParticipantType.INSTRUCTORS));

        String[] invalidModeratedInstructorSubmissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(student4ResponseToTeam.getId()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, student4inCourse1.getEmail(),
        };

        verifyCannotAccess(invalidModeratedInstructorSubmissionParams);
    }

    @Test
    protected void testAccessControl_noFeedbackResponse_inaccessible() {

        ______TS("non-existent feedback response");

        loginAsStudent(student1InCourse1.getGoogleId());

        assertNull(logic.getFeedbackResponse("randomNonExistId"));

        String[] nonExistParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt("randomNonExistId"),
        };

        assertThrows(EntityNotFoundException.class, () -> getAction(nonExistParams).checkAccessControl());
    }

    @Test
    protected void testAccessControl_unknownIntent_inaccessible() {

        ______TS("Unknown intent, should not be accessible");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] unknownIntentParams = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(instructor1ResponseToAll.getId()),
        };

        assertThrows(InvalidHttpParameterException.class, () -> getAction(unknownIntentParams).checkAccessControl());
    }

    @Test
    protected void testAccessControl_giverNotModeratedStudent_inaccessible() {

        ______TS("Instructor moderates student's response, but response not given by moderated student, "
                + "should not be accessible");

        StudentAttributes student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        StudentAttributes student5InCourse1 = typicalBundle.students.get("student5InCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes testModerateQuestion = logic.getFeedbackQuestion(
                session.getFeedbackSessionName(), session.getCourseId(), 2);
        FeedbackResponseAttributes testModerateResponse = logic.getFeedbackResponse(testModerateQuestion.getId(),
                student2InCourse1.getEmail(), student5InCourse1.getEmail());

        loginAsInstructor(instructor1OfCourse1.googleId);

        assertEquals(FeedbackParticipantType.STUDENTS,
                logic.getFeedbackQuestion(testModerateResponse.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(student1InCourse1.getEmail(), testModerateResponse.getGiver());

        FeedbackResponseUpdateRequest updateRequest = getUpdateRequest(instructor1OfCourse1.getEmail());

        String[] moderatedStudentSubmissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(testModerateResponse.getId()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, student1InCourse1.getEmail(),
        };

        verifyCannotAccess(updateRequest, moderatedStudentSubmissionParams);
    }

    @Test
    protected void testAccessControl_studentAccessOtherStudent_inaccessible() {

        ______TS("Student intends to access other person's response, should not be accessible");

        StudentAttributes student2InCourse1 = typicalBundle.students.get("student2InCourse1");

        loginAsStudent(student2InCourse1.getGoogleId());

        assertEquals(FeedbackParticipantType.STUDENTS,
                logic.getFeedbackQuestion(student1ResponseToStudent1.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(student2InCourse1.getEmail(), student1ResponseToStudent1.getGiver());

        FeedbackResponseUpdateRequest updateRequest = getUpdateRequest(student1ResponseToStudent1.getRecipient());

        String[] studentAccessOtherPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(student1ResponseToStudent1.getId()),
        };

        verifyCannotAccess(updateRequest, studentAccessOtherPersonParams);
    }

    @Test
    protected void testAccessControl_studentAccessOtherTeam_inaccessible() {

        ______TS("Student intends to access other team's response, should not be accessible");

        StudentAttributes student5InCourse1 = typicalBundle.students.get("student5InCourse1");
        StudentAttributes student4inCourse1 = typicalBundle.students.get("student4InCourse1");
        FeedbackSessionAttributes session2 = typicalBundle.feedbackSessions.get("session2InCourse1");
        FeedbackQuestionAttributes question2 = logic.getFeedbackQuestion(
                session2.getFeedbackSessionName(), session2.getCourseId(), 1);
        FeedbackResponseAttributes student4ResponseToTeam = logic.getFeedbackResponse(question2.getId(),
                student4inCourse1.getEmail(), "Team 1.2");

        loginAsStudent(student5InCourse1.getGoogleId());

        assertEquals(FeedbackParticipantType.TEAMS,
                logic.getFeedbackQuestion(student4ResponseToTeam.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(student5InCourse1.getTeam(), student4ResponseToTeam.getGiver());

        FeedbackResponseUpdateRequest updateRequest = getUpdateRequest(student5InCourse1.getEmail());

        String[] studentAccessOtherTeamParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(student4ResponseToTeam.getId()),
        };

        verifyCannotAccess(updateRequest, studentAccessOtherTeamParams);
    }

    @Test
    protected void testAccessControl_instructorAccessOtherGiver_inaccessible() {

        ______TS("Instructor intends to access other person's response, should not be accessible");

        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");

        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        assertEquals(FeedbackParticipantType.SELF,
                logic.getFeedbackQuestion(instructor1ResponseToAll.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(instructor2OfCourse1.getEmail(), instructor1ResponseToAll.getGiver());

        FeedbackResponseUpdateRequest updateRequest = getUpdateRequest(instructor2OfCourse1.getEmail());

        String[] instructorAccessOtherPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(instructor1ResponseToAll.getId()),
        };

        verifyCannotAccess(updateRequest, instructorAccessOtherPersonParams);
    }

    @Test
    protected void testAccessControl_instructorAccessOwnResponse_accessible() {

        ______TS("Instructor intends to access own response, should be accessible");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        assertEquals(instructor1OfCourse1.getEmail(), instructor1ResponseToAll.getGiver());

        FeedbackResponseUpdateRequest updateRequest = getUpdateRequest(instructor1ResponseToAll.getRecipient());

        String[] instructorAccessOwnPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(instructor1ResponseToAll.getId()),
        };

        verifyCanAccess(updateRequest, instructorAccessOwnPersonParams);
    }

    @Test
    protected void testAccessControl_instructorAccessIntentStudentLoggedIn_inaccessible() {

        ______TS("Instructor intends to access own response, but logged in as student, should be inaccessible");

        loginAsStudent(student1InCourse1.getGoogleId());

        assertEquals(instructor1OfCourse1.getEmail(), instructor1ResponseToAll.getGiver());

        FeedbackResponseUpdateRequest updateRequest = getUpdateRequest(instructor1ResponseToAll.getRecipient());

        String[] instructorAccessOwnPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(instructor1ResponseToAll.getId()),
        };

        verifyCannotAccess(updateRequest, instructorAccessOwnPersonParams);
    }

    @Test
    protected void testAccessControl_studentAccessSameTeam_accessible() {

        ______TS("Student intends to access same team's response, should be accessible");

        StudentAttributes student4inCourse1 = typicalBundle.students.get("student4InCourse1");
        FeedbackSessionAttributes session2 = typicalBundle.feedbackSessions.get("session2InCourse1");
        FeedbackQuestionAttributes question2 = logic.getFeedbackQuestion(
                session2.getFeedbackSessionName(), session2.getCourseId(), 1);
        FeedbackResponseAttributes student4ResponseToTeam = logic.getFeedbackResponse(question2.getId(),
                student4inCourse1.getEmail(), "Team 1.2");

        loginAsStudent(student1InCourse1.getGoogleId());

        assertEquals(FeedbackParticipantType.TEAMS,
                logic.getFeedbackQuestion(student4ResponseToTeam.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(student1InCourse1.getTeam(), student4ResponseToTeam.getGiver());

        FeedbackResponseUpdateRequest updateRequest = getUpdateRequest(student4ResponseToTeam.getRecipient());

        String[] studentAccessSameTeamParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(student4ResponseToTeam.getId()),
        };

        verifyCanAccess(updateRequest, studentAccessSameTeamParams);
    }

    @Test
    protected void testAccessControl_studentAccessOwnResponse_accessible() {

        ______TS("Student intends to access own response, should be accessible");

        loginAsStudent(student1InCourse1.getGoogleId());

        assertEquals(student1InCourse1.getEmail(), student1ResponseToStudent1.getGiver());

        FeedbackResponseUpdateRequest updateRequest = getUpdateRequest(student1ResponseToStudent1.getRecipient());

        String[] studentAccessOwnPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(student1ResponseToStudent1.getId()),
        };

        verifyCanAccess(updateRequest, studentAccessOwnPersonParams);
    }

    @Test
    protected void testAccessControl_instructorSubmitStudentResponse_accessible() {

        ______TS("Instructor attempts to edit student's response with appropriate permission, should be accessible");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        assertEquals(student1InCourse1.getEmail(), student1ResponseToStudent1.getGiver());

        FeedbackResponseUpdateRequest updateRequest = getUpdateRequest(student1ResponseToStudent1.getRecipient());

        String[] studentAccessOwnPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(student1ResponseToStudent1.getId()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, student1InCourse1.getEmail(),
        };

        verifyCanAccess(updateRequest, studentAccessOwnPersonParams);
    }

    @Test
    protected void testAccessControl_instructorSubmitStudentResponseNoPermission_inaccessible() {

        ______TS("Instructor attempts to edit student's response, but without appropriate permission, "
                + "should be inaccessible");

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");

        loginAsInstructor(helperOfCourse1.getGoogleId());

        assertEquals(student1InCourse1.getEmail(), student1ResponseToStudent1.getGiver());

        FeedbackResponseUpdateRequest updateRequest = getUpdateRequest(student1ResponseToStudent1.getRecipient());

        String[] studentAccessOwnPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(student1ResponseToStudent1.getId()),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, student1InCourse1.getEmail(),
        };

        verifyCannotAccess(updateRequest, studentAccessOwnPersonParams);
    }
}
