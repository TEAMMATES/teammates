package teammates.test.cases.webapi;

import java.util.ArrayList;

import org.apache.http.HttpStatus;
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
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.webapi.action.CreateFeedbackResponseAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackResponseData;
import teammates.ui.webapi.request.FeedbackResponseCreateRequest;
import teammates.ui.webapi.request.Intent;

/**
 * SUT: {@link CreateFeedbackResponseAction}.
 */
public class CreateFeedbackResponseActionTest extends BaseActionTest<CreateFeedbackResponseAction> {
    private FeedbackQuestionAttributes qn1InSession1InCourse1;
    private StudentAttributes student1InCourse1;
    private InstructorAttributes instructor1OfCourse1;
    private InstructorAttributes instructor2OfCourse1;
    private FeedbackQuestionAttributes qn2InGracePeriodInCourse1;
    private InstructorAttributes instructor1OfCourse2;
    private FeedbackQuestionAttributes qn1InClosedSessionInCourse1;
    private StudentAttributes student2InCourse1;
    private StudentAttributes student5InCourse1;
    private FeedbackQuestionAttributes qn2InSession1InCourse1;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    protected void prepareTestData() {
        removeAndRestoreTypicalDataBundle();
        FeedbackSessionAttributes gracePeriodSession = typicalBundle.feedbackSessions.get("gracePeriodSession");
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSessionAttributes closedSession = typicalBundle.feedbackSessions.get("closedSession");
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 1);
        qn2InGracePeriodInCourse1 = logic.getFeedbackQuestion(
                gracePeriodSession.getFeedbackSessionName(), gracePeriodSession.getCourseId(), 2);
        qn1InClosedSessionInCourse1 = logic.getFeedbackQuestion(
                closedSession.getFeedbackSessionName(), closedSession.getCourseId(), 1);
        instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
        student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        student5InCourse1 = typicalBundle.students.get("student5InCourse1");
        instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        qn2InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 2);
    }

    @Test
    @Override
    protected void testExecute() throws Exception {

        ______TS("not enough attributes");
        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString());
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId());

        ______TS("typical case for student");
        loginAsStudent(student2InCourse1.getGoogleId());
        String[] paramsQn2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        FeedbackResponseCreateRequest createRequest = getResponseRequest(student1InCourse1.getEmail());
        CreateFeedbackResponseAction typicalAction = getAction(createRequest, paramsQn2);
        JsonResult typicalResult = getJsonResult(typicalAction);

        assertEquals(HttpStatus.SC_OK, typicalResult.getStatusCode());
        FeedbackResponseData typicalData = (FeedbackResponseData) typicalResult.getOutput();
        FeedbackResponseAttributes responseAddedForStudent =
                logic.getFeedbackResponse(qn2InSession1InCourse1.getId(),
                        student2InCourse1.getEmail(), student1InCourse1.getEmail());
        assertNotNull(responseAddedForStudent);
        assertEquals("This is the text", typicalData.getResponseDetails().getAnswerString());
        assertEquals(student1InCourse1.getEmail(), typicalData.getRecipientIdentifier());
        assertNotNull(typicalData.getFeedbackResponseId());
        assertEquals(student2InCourse1.getEmail(), typicalData.getGiverIdentifier());

        ______TS("typical case for instructor");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        String[] paramsQn2Grace = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        FeedbackResponseCreateRequest requestForInstructor = getResponseRequest(instructor2OfCourse1.getEmail());
        CreateFeedbackResponseAction typicalActionForInstructor = getAction(requestForInstructor, paramsQn2Grace);
        JsonResult typicalResultInstructor = getJsonResult(typicalActionForInstructor);

        assertEquals(HttpStatus.SC_OK, typicalResultInstructor.getStatusCode());
        FeedbackResponseData typicalDataInstructor = (FeedbackResponseData) typicalResultInstructor.getOutput();
        FeedbackResponseAttributes responseAddedForInstructor =
                logic.getFeedbackResponse(qn2InGracePeriodInCourse1.getId(),
                        instructor2OfCourse1.getEmail(), instructor2OfCourse1.getEmail());
        assertNotNull(responseAddedForInstructor);
        assertEquals("This is the text", typicalDataInstructor.getResponseDetails().getAnswerString());
        assertNotNull(typicalDataInstructor.getFeedbackResponseId());
        assertEquals(instructor2OfCourse1.getEmail(), typicalDataInstructor.getGiverIdentifier());
        assertEquals(instructor2OfCourse1.getEmail(), typicalDataInstructor.getRecipientIdentifier());

        ______TS("response already exists");
        //show that this FeedbackResponse already exists
        FeedbackResponseAttributes existingFeedbackResponse =
                logic.getFeedbackResponse(qn2InSession1InCourse1.getId(), student2InCourse1.getEmail(),
                        student5InCourse1.getEmail());
        assertNotNull(existingFeedbackResponse);

        String[] paramsQ2S1C1 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        loginAsStudent(student2InCourse1.getGoogleId());
        FeedbackResponseCreateRequest createRequestAlreadyExists = getResponseRequest(student5InCourse1.getEmail());
        CreateFeedbackResponseAction typicalActionAlreadyExists = getAction(createRequestAlreadyExists, paramsQ2S1C1);
        assertThrows(InvalidHttpRequestBodyException.class, () -> getJsonResult(typicalActionAlreadyExists));

        ______TS("invalid intent");
        String[] invalidIntentParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyHttpParameterFailure(invalidIntentParams);
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

        // send create request
        FeedbackResponseCreateRequest createRequest = new FeedbackResponseCreateRequest();
        createRequest.setQuestionType(FeedbackQuestionType.MCQ);
        createRequest.setRecipientIdentifier(studentAttributes.getEmail());
        FeedbackMcqResponseDetails feedbackMcqResponseDetails = new FeedbackMcqResponseDetails();
        feedbackMcqResponseDetails.setAnswer("TEAM_NOT_EXIST");
        createRequest.setResponseDetails(feedbackMcqResponseDetails);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fqa.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        InvalidHttpRequestBodyException e = assertThrows(InvalidHttpRequestBodyException.class, () -> {
            loginAsStudent(studentAttributes.getGoogleId());
            CreateFeedbackResponseAction a = getAction(createRequest, params);
            getJsonResult(a);
        });
        AssertHelper.assertContains(Const.FeedbackQuestion.MCQ_ERROR_INVALID_OPTION, e.getMessage());
    }

    @Test
    public void testExecute_instructorFeedbackSubmissionMcqGenerateOptionsForTeams_shouldValidateAnswer() throws Exception {
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructorAttributes = typicalBundle.instructors.get("instructor1OfCourse1");

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

        // send create request
        FeedbackResponseCreateRequest createRequest = new FeedbackResponseCreateRequest();
        createRequest.setQuestionType(FeedbackQuestionType.MCQ);
        createRequest.setRecipientIdentifier(instructorAttributes.getEmail());
        FeedbackMcqResponseDetails feedbackMcqResponseDetails = new FeedbackMcqResponseDetails();
        feedbackMcqResponseDetails.setAnswer("TEAM_NOT_EXIST");
        createRequest.setResponseDetails(feedbackMcqResponseDetails);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fqa.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        InvalidHttpRequestBodyException e = assertThrows(InvalidHttpRequestBodyException.class, () -> {
            loginAsInstructor(instructorAttributes.getGoogleId());
            CreateFeedbackResponseAction a = getAction(createRequest, params);
            getJsonResult(a);
        });
        AssertHelper.assertContains(Const.FeedbackQuestion.MCQ_ERROR_INVALID_OPTION, e.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {

        ______TS("non-exist feedback question");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] nonExistFeedbackQuestionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "randomNonExist",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        assertThrows(EntityNotFoundException.class,
                () -> getAction(nonExistFeedbackQuestionParams).checkAccessControl());

        ______TS("feedback session is closed");
        String[] closedFeedbackSessionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InClosedSessionInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyCannotAccess(closedFeedbackSessionParams);

        ______TS("in preview request");
        String[] previewParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, student1InCourse1.getEmail(),
        };
        verifyCannotAccess(previewParams);

        ______TS("not answerable for students");
        String[] notAnswerableForStudents = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        //verify not answerable to students
        assertEquals(FeedbackParticipantType.INSTRUCTORS, qn2InGracePeriodInCourse1.getGiverType());
        verifyCannotAccess(notAnswerableForStudents);

        ______TS("not answerable to instructors");
        String[] notAnswerableForInstructors = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        assertEquals(FeedbackParticipantType.STUDENTS, qn1InSession1InCourse1.getGiverType());
        verifyCannotAccess(notAnswerableForInstructors);

        ______TS("invalid HTTP parameters");
        String[] invalidParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidParams).checkAccessControl());
        invalidParams[3] = Intent.INSTRUCTOR_RESULT.toString();
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidParams).checkAccessControl());

        ______TS("invalid recipient for student");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] invalidRecipientForStudent = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        assertThrows(UnauthorizedAccessException.class, () -> {
            FeedbackResponseCreateRequest createRequest = getResponseRequest(student2InCourse1.getEmail());
            getAction(createRequest, invalidRecipientForStudent).checkAccessControl();
        });

        ______TS("invalid recipient for instructor");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] invalidRecipientForInstructor = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        assertThrows(UnauthorizedAccessException.class, () -> {
            FeedbackResponseCreateRequest createRequest = getResponseRequest(instructor1OfCourse2.getEmail());
            getAction(createRequest, invalidRecipientForInstructor).checkAccessControl();
        });

    }

    private FeedbackResponseCreateRequest getResponseRequest(String recipientEmail) {
        FeedbackResponseCreateRequest createRequest = new FeedbackResponseCreateRequest();
        createRequest.setQuestionType(FeedbackQuestionType.TEXT);
        createRequest.setRecipientIdentifier(recipientEmail);
        FeedbackResponseDetails responseDetails = new FeedbackTextResponseDetails("This is the text");
        createRequest.setResponseDetails(responseDetails);
        return createRequest;
    }
}
