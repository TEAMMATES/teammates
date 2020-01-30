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
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.UpdateFeedbackResponseAction;
import teammates.ui.webapi.request.FeedbackResponseUpdateRequest;

/**
 * SUT: {@link UpdateFeedbackResponseAction}.
 */
public class UpdateFeedbackResponseActionTest extends BaseActionTest<UpdateFeedbackResponseAction> {

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
        return PUT;
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
        // TODO
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

        FeedbackResponseUpdateRequest updateRequest = new FeedbackResponseUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.MCQ);
        updateRequest.setRecipientIdentifier(studentAttributes.getEmail());
        FeedbackMcqResponseDetails newDetails = new FeedbackMcqResponseDetails();
        newDetails.setAnswer("TEAM_NOT_EXIST");
        updateRequest.setResponseDetails(newDetails);

        String[] params = {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponse.getId(),
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
        FeedbackResponseUpdateRequest updateRequest = new FeedbackResponseUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.MCQ);
        updateRequest.setRecipientIdentifier(instructorAttributes.getEmail());
        FeedbackMcqResponseDetails newDetails = new FeedbackMcqResponseDetails();
        newDetails.setAnswer("TEAM_NOT_EXIST");
        updateRequest.setResponseDetails(newDetails);

        String[] params = {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponse.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        InvalidHttpRequestBodyException e = assertThrows(InvalidHttpRequestBodyException.class, () -> {
            loginAsInstructor(instructorAttributes.getGoogleId());
            UpdateFeedbackResponseAction a = getAction(updateRequest, params);
            getJsonResult(a);
        });
        AssertHelper.assertContains(Const.FeedbackQuestion.MCQ_ERROR_INVALID_OPTION, e.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {

        ______TS("wrong giver type");

        loginAsStudent(student1InCourse1.getGoogleId());

        String[] wrongGiverTypeParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, typicalResponse.getId(),
        };

        verifyCannotAccess(wrongGiverTypeParams);

        ______TS("preview mode, cannot access");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] previewParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, typicalResponse.getId(),
                Const.ParamsNames.PREVIEWAS, instructor1OfCourse1.email,
        };

        verifyCannotAccess(previewParams);

        ______TS("response in session not open, cannot access");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        assertFalse(closedSession.isOpened());

        String[] sessionNotOpenParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, responseInClosedSession.getId(),
        };

        verifyCannotAccess(sessionNotOpenParams);

        ______TS("Response contains question not intended shown to instructor, "
                + "moderated instructor should not be accessible");

        loginAsInstructor(instructor1OfCourse1.googleId);

        assertFalse(logic.getFeedbackQuestion(typicalResponse2.getFeedbackQuestionId())
                .getShowResponsesTo().contains(FeedbackParticipantType.INSTRUCTORS));

        String[] invalidModeratedInstructorSubmissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, typicalResponse2.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, instructor1OfCourse1.getEmail(),
        };

        verifyCannotAccess(invalidModeratedInstructorSubmissionParams);

        ______TS("non-existent feedback response");

        loginAsStudent(student1InCourse1.getGoogleId());

        String[] nonExistParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, "randomNonExistId",
        };

        assertThrows(EntityNotFoundException.class, () -> getAction(nonExistParams).checkAccessControl());

        ______TS("Unknown intent, should not be accessible");

        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        String[] unknownIntentParams = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, typicalResponse3.getId(),
        };

        assertThrows(InvalidHttpParameterException.class, () -> getAction(unknownIntentParams).checkAccessControl());
    }

    @Test
    protected void testAccessControl_giverNotModeratedStudent_inaccessible() {

        ______TS("Instructor moderates student's response, but response not given by moderated student, "
                + "should not be accessible");

        loginAsInstructor(instructor1OfCourse1.googleId);

        assertEquals(FeedbackParticipantType.STUDENTS,
                logic.getFeedbackQuestion(testModerateResponse.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(student1InCourse1.getEmail(), testModerateResponse.getGiver());

        // send update request
        FeedbackResponseUpdateRequest updateRequest = new FeedbackResponseUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.MCQ);
        updateRequest.setRecipientIdentifier(instructor1OfCourse1.getEmail());
        FeedbackMcqResponseDetails newDetails = new FeedbackMcqResponseDetails();
        newDetails.setAnswer("TEAM_NOT_EXIST");
        updateRequest.setResponseDetails(newDetails);

        String[] moderatedStudentSubmissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, testModerateResponse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, student1InCourse1.getEmail(),
        };

        UpdateFeedbackResponseAction a = getAction(updateRequest, moderatedStudentSubmissionParams);
        assertThrows(UnauthorizedAccessException.class, a::checkAccessControl);
    }

    @Test
    protected void testAccessControl_studentAccessOtherStudent_inaccessible() throws Exception {

        ______TS("Student intends to access other person's response, should not be accessible");

        loginAsStudent(student2InCourse1.getGoogleId());

        assertEquals(FeedbackParticipantType.STUDENTS,
                logic.getFeedbackQuestion(typicalResponse.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(student2InCourse1.getEmail(), typicalResponse.getGiver());

        // send update request
        FeedbackResponseUpdateRequest updateRequest = new FeedbackResponseUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.MCQ);
        updateRequest.setRecipientIdentifier(typicalResponse.getRecipient());
        FeedbackMcqResponseDetails newDetails = new FeedbackMcqResponseDetails();
        newDetails.setAnswer("TEAM_NOT_EXIST");
        updateRequest.setResponseDetails(newDetails);

        String[] studentAccessOtherPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, typicalResponse.getId(),
        };

        UpdateFeedbackResponseAction a = getAction(updateRequest, studentAccessOtherPersonParams);
        assertThrows(UnauthorizedAccessException.class, a::checkAccessControl);
    }

    @Test
    protected void testAccessControl_studentAccessOtherTeam_inaccessible() {

        ______TS("Student intends to access other team's response, should not be accessible");

        loginAsStudent(student5InCourse1.getGoogleId());

        assertEquals(FeedbackParticipantType.TEAMS,
                logic.getFeedbackQuestion(typicalResponse2.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(student5InCourse1.getTeam(), typicalResponse2.getGiver());

        // send update request
        FeedbackResponseUpdateRequest updateRequest = new FeedbackResponseUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.MCQ);
        updateRequest.setRecipientIdentifier(student5InCourse1.getEmail());
        FeedbackMcqResponseDetails newDetails = new FeedbackMcqResponseDetails();
        newDetails.setAnswer("TEAM_NOT_EXIST");
        updateRequest.setResponseDetails(newDetails);

        String[] studentAccessOtherTeamParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, typicalResponse2.getId(),
        };

        UpdateFeedbackResponseAction a = getAction(updateRequest, studentAccessOtherTeamParams);
        assertThrows(UnauthorizedAccessException.class, a::checkAccessControl);
    }

    @Test
    protected void testAccessControl_instructorAccessOtherGiver_inaccessible() {

        ______TS("Instructor intends to access other person's response, should not be accessible");

        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        assertEquals(FeedbackParticipantType.SELF,
                logic.getFeedbackQuestion(typicalResponse3.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(instructor2OfCourse1.getEmail(), typicalResponse3.getGiver());

        // send update request
        FeedbackResponseUpdateRequest updateRequest = new FeedbackResponseUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.MCQ);
        updateRequest.setRecipientIdentifier(instructor2OfCourse1.getEmail());
        FeedbackMcqResponseDetails newDetails = new FeedbackMcqResponseDetails();
        newDetails.setAnswer("TEAM_NOT_EXIST");
        updateRequest.setResponseDetails(newDetails);

        String[] instructorAccessOtherPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, typicalResponse3.getId(),
        };

        UpdateFeedbackResponseAction a = getAction(updateRequest, instructorAccessOtherPersonParams);
        assertThrows(UnauthorizedAccessException.class, a::checkAccessControl);
    }

    @Test
    protected void testAccessControl_instructorAccessOwnResponse_accessible() {

        ______TS("Instructor intends to access own response, should be accessible");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        assertEquals(instructor1OfCourse1.getEmail(), typicalResponse3.getGiver());

        // send update request
        FeedbackResponseUpdateRequest updateRequest = new FeedbackResponseUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.MCQ);
        updateRequest.setRecipientIdentifier(typicalResponse3.getRecipient());
        FeedbackMcqResponseDetails newDetails = new FeedbackMcqResponseDetails();
        newDetails.setAnswer("TEAM_NOT_EXIST");
        updateRequest.setResponseDetails(newDetails);

        String[] instructorAccessOwnPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, typicalResponse3.getId(),
        };

        UpdateFeedbackResponseAction a = getAction(updateRequest, instructorAccessOwnPersonParams);
        a.checkSpecificAccessControl();
    }

    @Test
    protected void testAccessControl_studentAccessSameTeam_accessible() {

        ______TS("Student intends to access same team's response, should be accessible");

        loginAsStudent(student1InCourse1.getGoogleId());

        assertEquals(FeedbackParticipantType.TEAMS,
                logic.getFeedbackQuestion(typicalResponse2.getFeedbackQuestionId()).getGiverType());
        assertNotEquals(student1InCourse1.getTeam(), typicalResponse2.getGiver());

        // send update request
        FeedbackResponseUpdateRequest updateRequest = new FeedbackResponseUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.MCQ);
        updateRequest.setRecipientIdentifier(typicalResponse2.getRecipient());
        FeedbackMcqResponseDetails newDetails = new FeedbackMcqResponseDetails();
        newDetails.setAnswer("TEAM_NOT_EXIST");
        updateRequest.setResponseDetails(newDetails);

        String[] studentAccessOSameTeamParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, typicalResponse2.getId(),
        };

        UpdateFeedbackResponseAction a = getAction(updateRequest, studentAccessOSameTeamParams);
        a.checkSpecificAccessControl();
    }

    @Test
    protected void testAccessControl_studentAccessOwnResponse_accessible() {

        ______TS("Student intends to access own response, should be accessible");

        loginAsStudent(student1InCourse1.getGoogleId());

        assertEquals(student1InCourse1.getEmail(), typicalResponse.getGiver());

        // send update request
        FeedbackResponseUpdateRequest updateRequest = new FeedbackResponseUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.MCQ);
        updateRequest.setRecipientIdentifier(typicalResponse.getRecipient());
        FeedbackMcqResponseDetails newDetails = new FeedbackMcqResponseDetails();
        newDetails.setAnswer("TEAM_NOT_EXIST");
        updateRequest.setResponseDetails(newDetails);

        String[] studentAccessOwnPersonParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, typicalResponse.getId(),
        };

        UpdateFeedbackResponseAction a = getAction(updateRequest, studentAccessOwnPersonParams);
        a.checkSpecificAccessControl();
    }
}
