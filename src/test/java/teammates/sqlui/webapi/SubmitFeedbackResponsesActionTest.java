package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.FeedbackResponsesRequest;
import teammates.ui.request.Intent;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.SubmitFeedbackResponsesAction;

/**
 * SUT: {@link SubmitFeedbackResponsesAction}.
 */
public class SubmitFeedbackResponsesActionTest extends BaseActionTest<SubmitFeedbackResponsesAction> {
    private Student stubStudent;
    private Instructor stubInstructor;
    private FeedbackQuestion spyFeedbackQuestion;
    private Course stubCourse;
    private FeedbackSession stubFeedbackSession;
    private Section stubSection;
    private Student recipientStudent1;
    private Student recipientStudent2;
    private Instructor recipientInstructor1;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        stubStudent = getTypicalStudent();
        stubStudent.setTeam(getTypicalTeam());
        stubInstructor = getTypicalInstructor();
        stubStudent.setAccount(getTypicalAccount());
        stubInstructor.setAccount(getTypicalAccount());
        stubCourse = getTypicalCourse();
        stubFeedbackSession = getTypicalFeedbackSessionForCourse(stubCourse);
        spyFeedbackQuestion = spy(getTypicalFeedbackQuestionForSession(stubFeedbackSession));
        stubSection = getTypicalSection();

        recipientStudent1 = getTypicalStudent();
        recipientStudent1.setEmail("recipient1@teammates.tmt");
        recipientStudent1.setName("Recipient 1");
        recipientStudent1.setCourse(stubCourse);
        recipientStudent1.setTeam(getTypicalTeam());

        recipientStudent2 = getTypicalStudent();
        recipientStudent2.setEmail("recipient2@teammates.tmt");
        recipientStudent2.setName("Recipient 2");
        recipientStudent2.setCourse(stubCourse);
        recipientStudent2.setTeam(getTypicalTeam());

        recipientInstructor1 = getTypicalInstructor();
        recipientInstructor1.setEmail("recipientinstructor1@teammates.tmt");
        recipientInstructor1.setName("Recipient Instructor 1");
        recipientInstructor1.setCourse(stubCourse);

        reset(mockLogic);

        doNothing().when(mockLogic).populateFieldsToGenerateInQuestion(
                any(FeedbackQuestion.class), anyString(), anyString(), anyString());

        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(stubInstructor);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getFeedbackQuestion(spyFeedbackQuestion.getId())).thenReturn(spyFeedbackQuestion);
        when(mockLogic.getDeadlineForUser(any(FeedbackSession.class), any()))
                .thenAnswer(invocation -> ((FeedbackSession) invocation.getArgument(0)).getEndTime());
    }

    @Test
    void testExecute_insufficientParams_throwsInvalidHttpParameterException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params1 = {};
        verifyHttpParameterFailure(params1);

        String[] params2 = {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        verifyHttpParameterFailure(params2);
    }

    @Test
    void testExecute_invalidIntent_throwsInvalidHttpParameterException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] paramsStudentResult = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };
        verifyHttpParameterFailure(paramsStudentResult);

        String[] paramsInstructorResult = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        verifyHttpParameterFailure(paramsInstructorResult);

        String[] paramsFullDetail = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyHttpParameterFailure(paramsFullDetail);
    }

    @Test
    void testExecute_noRequestBody_throwsInvalidHttpRequestBodyException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyHttpRequestBodyFailure(null, params);
    }

    @Test
    void testExecute_nullRecipient_throwsInvalidOperationException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());
        Map<String, FeedbackQuestionRecipient> recipients = Map.of();
        when(mockLogic.getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent))
                .thenReturn(recipients);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                null, new FeedbackTextResponseDetails("test")));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        verifyInvalidOperation(requestBody, params);
    }

    @Test
    void testExecute_emptyRecipient_throwsInvalidOperationException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());
        Map<String, FeedbackQuestionRecipient> recipients = Map.of();
        when(mockLogic.getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent))
                .thenReturn(recipients);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                "", new FeedbackTextResponseDetails("test")));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        verifyInvalidOperation(requestBody, params);
    }

    @Test
    void testExecute_invalidRecipient_throwsInvalidOperationException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        Map<String, FeedbackQuestionRecipient> recipients = Map.of(
                recipientStudent1.getEmail(),
                new FeedbackQuestionRecipient(recipientStudent1.getName(), recipientStudent1.getEmail()));
        when(mockLogic.getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent))
                .thenReturn(recipients);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                "invalid@email.com", new FeedbackTextResponseDetails("test")));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        verifyInvalidOperation(requestBody, params);
    }

    @Test
    void testExecute_studentSubmissionNoExistingResponses_success() throws Exception {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        Map<String, FeedbackQuestionRecipient> recipients = Map.of(
                recipientStudent1.getEmail(),
                new FeedbackQuestionRecipient(recipientStudent1.getName(), recipientStudent1.getEmail()));
        when(mockLogic.getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        FeedbackResponse createdResponse = FeedbackResponse.makeResponse(
                spyFeedbackQuestion, stubStudent.getEmail(), stubSection,
                recipientStudent1.getEmail(), stubSection,
                new FeedbackTextResponseDetails("Response for " + recipientStudent1.getEmail()));
        when(mockLogic.createFeedbackResponse(any(FeedbackResponse.class)))
                .thenReturn(createdResponse);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("Response for " + recipientStudent1.getEmail())));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();

        assertEquals(1, result.getResponses().size());
        FeedbackResponseData responseData = result.getResponses().get(0);
        assertEquals(stubStudent.getEmail(), responseData.getGiverIdentifier());
        assertEquals(recipientStudent1.getEmail(), responseData.getRecipientIdentifier());

        verify(mockLogic).getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent);
        verify(mockLogic).getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent);
        verify(mockLogic).populateFieldsToGenerateInQuestion(
                spyFeedbackQuestion, stubCourse.getId(), stubStudent.getEmail(), stubStudent.getTeamName());
        verify(mockLogic).createFeedbackResponse(argThat(response ->
                response.getGiver().equals(stubStudent.getEmail())
                        && response.getRecipient().equals(recipientStudent1.getEmail())));
    }

    @Test
    void testExecute_instructorSubmissionNoExistingResponses_success() throws Exception {
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromInstructorForQuestion(spyFeedbackQuestion, stubInstructor))
                .thenReturn(List.of());

        Map<String, FeedbackQuestionRecipient> recipients = Map.of(
                recipientInstructor1.getEmail(),
                new FeedbackQuestionRecipient(recipientInstructor1.getName(), recipientInstructor1.getEmail()));
        when(mockLogic.getRecipientsOfQuestion(spyFeedbackQuestion, stubInstructor, null))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        FeedbackResponse createdResponse = FeedbackResponse.makeResponse(
                spyFeedbackQuestion, stubInstructor.getEmail(), stubSection,
                recipientInstructor1.getEmail(), stubSection,
                new FeedbackTextResponseDetails("Response for " + recipientInstructor1.getEmail()));
        when(mockLogic.createFeedbackResponse(any(FeedbackResponse.class)))
                .thenReturn(createdResponse);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientInstructor1.getEmail(),
                new FeedbackTextResponseDetails("Response for " + recipientInstructor1.getEmail())));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();

        assertEquals(1, result.getResponses().size());
        FeedbackResponseData responseData = result.getResponses().get(0);
        assertEquals(stubInstructor.getEmail(), responseData.getGiverIdentifier());
        assertEquals(recipientInstructor1.getEmail(), responseData.getRecipientIdentifier());

        verify(mockLogic).getFeedbackResponsesFromInstructorForQuestion(spyFeedbackQuestion, stubInstructor);
        verify(mockLogic).getRecipientsOfQuestion(spyFeedbackQuestion, stubInstructor, null);
        verify(mockLogic).populateFieldsToGenerateInQuestion(
                spyFeedbackQuestion, stubCourse.getId(), stubInstructor.getEmail(), null);
        verify(mockLogic).createFeedbackResponse(argThat(response ->
                response.getGiver().equals(stubInstructor.getEmail())
                        && response.getRecipient().equals(recipientInstructor1.getEmail())));
    }

    @Test
    void testExecute_withExistingResponses_success() throws Exception {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        FeedbackResponse existingResponse1 = FeedbackResponse.makeResponse(
                spyFeedbackQuestion, stubStudent.getEmail(), stubSection,
                recipientStudent1.getEmail(), stubSection,
                new FeedbackTextResponseDetails("Old response 1"));
        existingResponse1.setId(UUID.randomUUID());

        FeedbackResponse existingResponse2 = FeedbackResponse.makeResponse(
                spyFeedbackQuestion, stubStudent.getEmail(), stubSection,
                recipientStudent2.getEmail(), stubSection,
                new FeedbackTextResponseDetails("Old response 2"));
        existingResponse2.setId(UUID.randomUUID());

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of(existingResponse1, existingResponse2));

        Map<String, FeedbackQuestionRecipient> recipients = Map.of(
                recipientStudent1.getEmail(),
                new FeedbackQuestionRecipient(recipientStudent1.getName(), recipientStudent1.getEmail()),
                recipientStudent2.getEmail(),
                new FeedbackQuestionRecipient(recipientStudent2.getName(), recipientStudent2.getEmail()));
        when(mockLogic.getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        FeedbackResponse updatedResponse = FeedbackResponse.makeResponse(
                spyFeedbackQuestion, stubStudent.getEmail(), stubSection,
                recipientStudent1.getEmail(), stubSection,
                new FeedbackTextResponseDetails("Updated response 1"));
        when(mockLogic.updateFeedbackResponseCascade(any(FeedbackResponse.class)))
                .thenReturn(updatedResponse);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("Updated response 1")));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();

        assertEquals(1, result.getResponses().size());
        verify(mockLogic).updateFeedbackResponseCascade(any(FeedbackResponse.class));
        verify(mockLogic).deleteFeedbackResponsesAndCommentsCascade(existingResponse2);
        verify(mockLogic).getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent);
        verify(mockLogic).getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent);
        verify(mockLogic).updateFeedbackResponseCascade(argThat(response ->
                response.getGiver().equals(stubStudent.getEmail())
                        && response.getRecipient().equals(recipientStudent1.getEmail())));
        verify(mockLogic).deleteFeedbackResponsesAndCommentsCascade(existingResponse2);
    }

    @Test
    void testExecute_teamGiverType_success() throws Exception {
        loginAsStudent(stubStudent.getGoogleId());
        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.TEAMS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        Map<String, FeedbackQuestionRecipient> recipients = Map.of(
                recipientStudent1.getEmail(),
                new FeedbackQuestionRecipient(recipientStudent1.getName(), recipientStudent1.getEmail()));
        when(mockLogic.getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        when(mockLogic.createFeedbackResponse(any(FeedbackResponse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("Team response")));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();

        assertEquals(1, result.getResponses().size());
        assertEquals(stubStudent.getTeamName(), result.getResponses().get(0).getGiverIdentifier());
        verify(mockLogic).createFeedbackResponse(argThat(response ->
                response.getGiver() != null && response.getGiver().equals(stubStudent.getTeamName())));
    }

    @Test
    void testExecute_questionSpecificValidationFails_throwsInvalidHttpRequestBodyException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        Map<String, FeedbackQuestionRecipient> recipients = Map.of(
                recipientStudent1.getEmail(),
                new FeedbackQuestionRecipient(recipientStudent1.getName(), recipientStudent1.getEmail()));
        when(mockLogic.getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent))
                .thenReturn(recipients);

        FeedbackTextQuestionDetails mockDetails = mock(FeedbackTextQuestionDetails.class);
        when(mockDetails.validateResponsesDetails(anyList(), anyInt())).thenReturn(List.of("Validation error"));
        when(spyFeedbackQuestion.getQuestionDetailsCopy()).thenReturn(mockDetails);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("test")));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        verifyHttpRequestBodyFailure(requestBody, params);
    }

    @Test
    void testExecute_tooManyRecipients_success() throws Exception {
        loginAsStudent(stubStudent.getGoogleId());
        spyFeedbackQuestion.setNumOfEntitiesToGiveFeedbackTo(1);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        Map<String, FeedbackQuestionRecipient> recipients = Map.of(
                recipientStudent1.getEmail(),
                new FeedbackQuestionRecipient(recipientStudent1.getName(), recipientStudent1.getEmail()),
                recipientStudent2.getEmail(),
                new FeedbackQuestionRecipient(recipientStudent2.getName(), recipientStudent2.getEmail()));
        when(mockLogic.getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);
        when(mockLogic.createFeedbackResponse(any(FeedbackResponse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("Response 1")));
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent2.getEmail(),
                new FeedbackTextResponseDetails("Response 2")));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();

        assertEquals(2, result.getResponses().size());
    }

    @Test
    void testExecute_multipleRecipients_success() throws Exception {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        Map<String, FeedbackQuestionRecipient> recipients = Map.of(
                recipientStudent1.getEmail(),
                new FeedbackQuestionRecipient(recipientStudent1.getName(), recipientStudent1.getEmail()),
                recipientStudent2.getEmail(),
                new FeedbackQuestionRecipient(recipientStudent2.getName(), recipientStudent2.getEmail()));
        when(mockLogic.getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        when(mockLogic.createFeedbackResponse(any(FeedbackResponse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("Response for " + recipientStudent1.getEmail())));
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent2.getEmail(),
                new FeedbackTextResponseDetails("Response for " + recipientStudent2.getEmail())));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();

        assertEquals(2, result.getResponses().size());
    }

    @Test
    void testExecute_teamBasedRecipients_success() throws Exception {
        loginAsStudent(stubStudent.getGoogleId());
        spyFeedbackQuestion.setRecipientType(FeedbackParticipantType.TEAMS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        String teamName = "Recipient Team";
        Map<String, FeedbackQuestionRecipient> recipients = Map.of(
                teamName,
                new FeedbackQuestionRecipient(teamName, teamName));
        when(mockLogic.getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        when(mockLogic.createFeedbackResponse(any(FeedbackResponse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                teamName,
                new FeedbackTextResponseDetails("Team recipient response")));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();

        assertEquals(1, result.getResponses().size());
        assertEquals(teamName, result.getResponses().get(0).getRecipientIdentifier());
    }

    @Test
    void testExecute_idCannotBeConvertedToUuid_throwsEntityNotFoundException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "invalid-uuid",
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("test")));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        EntityNotFoundException enfe = assertThrows(EntityNotFoundException.class, action::execute);
        assertEquals("The feedback question does not exist.", enfe.getMessage());
    }

    @Test
    void testExecute_maxPossibleRecipients_success() throws Exception {
        loginAsStudent(stubStudent.getGoogleId());
        spyFeedbackQuestion.setNumOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        Map<String, FeedbackQuestionRecipient> recipients = Map.of(
                recipientStudent1.getEmail(),
                new FeedbackQuestionRecipient(recipientStudent1.getName(), recipientStudent1.getEmail()),
                recipientStudent2.getEmail(),
                new FeedbackQuestionRecipient(recipientStudent2.getName(), recipientStudent2.getEmail()));
        when(mockLogic.getRecipientsOfQuestion(spyFeedbackQuestion, null, stubStudent))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);
        when(mockLogic.createFeedbackResponse(any(FeedbackResponse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("Response 1")));
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent2.getEmail(),
                new FeedbackTextResponseDetails("Response 2")));

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();

        assertEquals(2, result.getResponses().size());
    }

    @Test
    void testSpecificAccessControl_questionDoesNotExist_throwsEntityNotFoundException() {
        loginAsStudent(stubStudent.getGoogleId());

        when(mockLogic.getFeedbackQuestion(spyFeedbackQuestion.getId())).thenReturn(null);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        SubmitFeedbackResponsesAction action = getAction(params);
        EntityNotFoundException enfe = assertThrows(EntityNotFoundException.class, action::checkAccessControl);
        assertEquals("The feedback question does not exist.", enfe.getMessage());
    }

    @Test
    void testSpecificAccessControl_notAnswerableForStudent_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        for (FeedbackParticipantType type : FeedbackParticipantType.values()) {
            if (type == FeedbackParticipantType.STUDENTS || type == FeedbackParticipantType.TEAMS) {
                continue;
            }
            spyFeedbackQuestion.setGiverType(type);
            verifyCannotAccess(params);
        }
    }

    @Test
    void testSpecificAccessControl_notAnswerableForInstructor_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        for (FeedbackParticipantType type : FeedbackParticipantType.values()) {
            if (type == FeedbackParticipantType.INSTRUCTORS || type == FeedbackParticipantType.SELF) {
                continue;
            }
            spyFeedbackQuestion.setGiverType(type);
            verifyCannotAccess(params);
        }
    }

    @Test
    void testSpecificAccessControl_instructorWithoutSubmitPrivilege_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now().minus(Duration.ofDays(2)));
        stubFeedbackSession.setStartTime(Instant.now().minus(Duration.ofDays(1)));
        stubFeedbackSession.setEndTime(Instant.now().plus(Duration.ofDays(1)));

        InstructorPrivileges privileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, false);
        stubInstructor.setPrivileges(privileges);

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.INSTRUCTORS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_studentEntityDoesNotExist_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());

        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(null);

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorEntityDoesNotExist_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());

        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId())).thenReturn(null);

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.INSTRUCTORS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_sessionNotOpen_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());
        stubFeedbackSession.setStartTime(Instant.now().plusSeconds(86400));
        stubFeedbackSession.setEndTime(Instant.now().plusSeconds(172800));

        when(mockLogic.getDeadlineForUser(stubFeedbackSession, stubStudent))
                .thenReturn(stubFeedbackSession.getEndTime());

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_sessionClosed_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now().minusSeconds(172800));
        stubFeedbackSession.setStartTime(Instant.now().minusSeconds(86400));
        stubFeedbackSession.setEndTime(Instant.now().minusSeconds(3600));

        when(mockLogic.getDeadlineForUser(stubFeedbackSession, stubStudent))
                .thenReturn(stubFeedbackSession.getEndTime());

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_submissionWithinGracePeriod_canAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now().minus(Duration.ofDays(2)));
        stubFeedbackSession.setStartTime(Instant.now().minus(Duration.ofDays(1)));
        stubFeedbackSession.setEndTime(Instant.now().minus(Duration.ofMinutes(10)));
        stubFeedbackSession.setGracePeriod(Duration.ofMinutes(15));

        when(mockLogic.getDeadlineForUser(stubFeedbackSession, stubStudent))
                .thenReturn(stubFeedbackSession.getEndTime());

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_submissionAfterDeadlineExtension_canAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now().minus(Duration.ofDays(2)));
        stubFeedbackSession.setStartTime(Instant.now().minus(Duration.ofDays(1)));
        stubFeedbackSession.setEndTime(Instant.now().minus(Duration.ofMinutes(10)));
        stubFeedbackSession.setGracePeriod(Duration.ZERO);

        when(mockLogic.getDeadlineForUser(stubFeedbackSession, stubStudent))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(10)));

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_previewMode_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.INSTRUCTORS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, stubInstructor.getEmail(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_typicalStudentAccess_canAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now().minus(Duration.ofDays(2)));
        stubFeedbackSession.setStartTime(Instant.now().minus(Duration.ofDays(1)));
        stubFeedbackSession.setEndTime(Instant.now().plus(Duration.ofDays(1)));

        when(mockLogic.getDeadlineForUser(stubFeedbackSession, stubStudent))
                .thenReturn(stubFeedbackSession.getEndTime());

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_typicalInstructorAccess_canAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now().minus(Duration.ofDays(2)));
        stubFeedbackSession.setStartTime(Instant.now().minus(Duration.ofDays(1)));
        stubFeedbackSession.setEndTime(Instant.now().plus(Duration.ofDays(1)));

        when(mockLogic.getDeadlineForUser(stubFeedbackSession, stubInstructor))
                .thenReturn(stubFeedbackSession.getEndTime());

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.INSTRUCTORS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithModeratorPrivilege_canAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        spyFeedbackQuestion.setShowGiverNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        spyFeedbackQuestion.setShowRecipientNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        spyFeedbackQuestion.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));

        when(mockLogic.getInstructorForEmail(stubCourse.getId(), stubInstructor.getEmail()))
                .thenReturn(stubInstructor);

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.INSTRUCTORS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubInstructor.getEmail(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_adminMasqueradeAsStudent_canAccess() {
        loginAsAdmin();
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now().minus(Duration.ofDays(2)));
        stubFeedbackSession.setStartTime(Instant.now().minus(Duration.ofDays(1)));
        stubFeedbackSession.setEndTime(Instant.now().plus(Duration.ofDays(1)));

        when(mockLogic.getDeadlineForUser(stubFeedbackSession, stubStudent))
                .thenReturn(stubFeedbackSession.getEndTime());

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.USER_ID, stubStudent.getGoogleId(),
        };

        verifyCanMasquerade(stubStudent.getGoogleId(), params);
    }

    @Test
    void testSpecificAccessControl_studentModerationAttempt_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, "some-email",
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_notLoggedIn_cannotAccess() {
        logoutUser();

        spyFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, spyFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCannotAccess(params);
    }
}
