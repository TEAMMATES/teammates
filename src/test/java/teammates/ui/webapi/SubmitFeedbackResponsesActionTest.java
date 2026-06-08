package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.FeedbackQuestionResponsesData;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.request.FeedbackResponsesRequest;
import teammates.ui.request.Intent;

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
    void setUp() throws Exception {
        stubStudent = getTypicalStudent();
        stubStudent.setTeam(getTypicalTeam());
        stubInstructor = getTypicalInstructor();
        stubStudent.setCourse(getTypicalCourse());
        stubInstructor.setCourse(stubStudent.getCourse());
        stubStudent.setAccount(getTypicalAccount());
        stubInstructor.setAccount(getTypicalAccount());
        stubCourse = stubStudent.getCourse();
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

        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        when(mockLogic.getAccount(stubStudent.getAccountId())).thenReturn(stubStudent.getAccount());
        when(mockLogic.getAccount(stubInstructor.getAccountId())).thenReturn(stubInstructor.getAccount());
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(stubInstructor);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId())).thenReturn(stubFeedbackSession);
        when(mockLogic.getFeedbackQuestion(spyFeedbackQuestion.getId())).thenReturn(spyFeedbackQuestion);
        when(mockLogic.getStudentsForCourse(stubCourse.getId()))
                .thenReturn(List.of(stubStudent, recipientStudent1, recipientStudent2));
        when(mockLogic.getInstructorsByCourse(stubCourse.getId()))
                .thenReturn(List.of(stubInstructor, recipientInstructor1));
        when(mockLogic.getDeadlineForUser(any(FeedbackSession.class), any()))
                .thenAnswer(invocation -> ((FeedbackSession) invocation.getArgument(0)).getEndTime());
        when(mockLogic.submitFeedbackResponsesFromStudent(
                any(FeedbackSession.class), any(Student.class), any(FeedbackResponsesRequest.class)))
                .thenAnswer(invocation -> getSubmittedResponses(
                        getResponseGiver(spyFeedbackQuestion, invocation.getArgument(1)),
                        invocation.getArgument(2)));
        when(mockLogic.submitFeedbackResponsesFromInstructor(
                any(FeedbackSession.class), any(Instructor.class), any(FeedbackResponsesRequest.class)))
                .thenAnswer(invocation -> getSubmittedResponses(
                        new ResponseGiver((Instructor) invocation.getArgument(1)),
                        invocation.getArgument(2)));
    }

    private ResponseGiver getResponseGiver(FeedbackQuestion feedbackQuestion, Student student) {
        return feedbackQuestion.getGiverType() == QuestionGiverType.TEAMS
                ? new ResponseGiver(student.getTeam())
                : new ResponseGiver(student);
    }

    private FeedbackResponsesRequest getRequestBody(
            List<FeedbackResponsesRequest.FeedbackResponseRequest> responses) {
        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setQuestionResponses(Map.of(spyFeedbackQuestion.getId(), responses));
        return requestBody;
    }

    private List<FeedbackResponse> getSubmittedResponses(
            ResponseGiver responseGiver, FeedbackResponsesRequest submitRequest) {
        List<FeedbackResponse> responses = new ArrayList<>();
        for (List<FeedbackResponsesRequest.FeedbackResponseRequest> responseRequests
                : submitRequest.getQuestionResponses().values()) {
            for (FeedbackResponsesRequest.FeedbackResponseRequest responseRequest : responseRequests) {
                FeedbackResponse response = FeedbackResponse.makeResponse(
                        responseGiver,
                        getResponseRecipient(responseRequest.getRecipient()),
                        responseRequest.getResponseDetails());
                spyFeedbackQuestion.addFeedbackResponse(response);
                responses.add(response);
            }
        }
        return responses;
    }

    private ResponseRecipient getResponseRecipient(String recipient) {
        if (recipientStudent1.getEmail().equals(recipient)) {
            return new ResponseRecipient(recipientStudent1);
        }
        if (recipientStudent2.getEmail().equals(recipient)) {
            return new ResponseRecipient(recipientStudent2);
        }
        if (recipientInstructor1.getEmail().equals(recipient)) {
            return new ResponseRecipient(recipientInstructor1);
        }
        if (stubStudent.getTeamName().equals(recipient)) {
            return new ResponseRecipient(stubStudent.getTeam());
        }
        return new ResponseRecipient();
    }

    private List<FeedbackResponseData> flattenResponses(FeedbackQuestionResponsesData result) {
        return result.getQuestionResponses().values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
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
    void testExecute_noRequestBody_throwsInvalidHttpRequestBodyException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyHttpRequestBodyFailure(null, params);
    }

    @Test
    void testExecute_nullResponsesForQuestion_throwsInvalidHttpRequestBodyException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        Map<UUID, List<FeedbackResponsesRequest.FeedbackResponseRequest>> questionResponses = new HashMap<>();
        questionResponses.put(spyFeedbackQuestion.getId(), null);
        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setQuestionResponses(questionResponses);

        verifyHttpRequestBodyFailure(requestBody, params);
    }

    @Test
    void testExecute_nullRecipient_throwsInvalidHttpRequestBodyException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                null, new FeedbackTextResponseDetails("test")));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        verifyHttpRequestBodyFailure(requestBody, params);
    }

    @Test
    void testExecute_emptyRecipient_throwsInvalidHttpRequestBodyException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                "", new FeedbackTextResponseDetails("test")));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        verifyHttpRequestBodyFailure(requestBody, params);
    }

    @Test
    void testExecute_duplicateResponseId_throwsInvalidHttpRequestBodyException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        UUID duplicateResponseId = UUID.randomUUID();
        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                duplicateResponseId, recipientStudent1.getEmail(), new FeedbackTextResponseDetails("test"), null));
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                duplicateResponseId, recipientStudent2.getEmail(), new FeedbackTextResponseDetails("test"), null));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        verifyHttpRequestBodyFailure(requestBody, params);
    }

    @Test
    void testExecute_duplicateRecipient_throwsInvalidHttpRequestBodyException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                UUID.randomUUID(), recipientStudent1.getEmail(), new FeedbackTextResponseDetails("test"), null));
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                UUID.randomUUID(), recipientStudent1.getEmail(), new FeedbackTextResponseDetails("test"), null));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        verifyHttpRequestBodyFailure(requestBody, params);
    }

    @Test
    void testExecute_invalidRecipient_throwsInvalidOperationException() throws Exception {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.submitFeedbackResponsesFromStudent(
                any(FeedbackSession.class), any(Student.class), any(FeedbackResponsesRequest.class)))
                .thenThrow(new InvalidOperationException(
                        "The recipient invalid@email.com is not a valid recipient of the question"));

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                "invalid@email.com", new FeedbackTextResponseDetails("test")));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        verifyInvalidOperation(requestBody, params);
    }

    @Test
    void testExecute_studentSubmissionNoExistingResponses_success() throws Exception {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        Set<ResponseRecipient> recipients = Set.of(new ResponseRecipient(recipientStudent1));
        when(mockLogic.getRecipientsOfQuestion(any(FeedbackQuestion.class), any(ResponseGiver.class)))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        FeedbackResponse createdResponse = FeedbackResponse.makeResponse(
                new ResponseGiver(stubStudent),
                new ResponseRecipient(recipientStudent1),
                new FeedbackTextResponseDetails("Response for " + recipientStudent1.getEmail()));
        spyFeedbackQuestion.addFeedbackResponse(createdResponse);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("Response for " + recipientStudent1.getEmail())));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackQuestionResponsesData result = (FeedbackQuestionResponsesData) getJsonResult(action).getOutput();
        List<FeedbackResponseData> submittedResponses = flattenResponses(result);

        assertEquals(1, submittedResponses.size());
        FeedbackResponseData responseData = submittedResponses.get(0);
        assertEquals(stubStudent.getEmail(), responseData.getGiverIdentifier());
        assertEquals(recipientStudent1.getEmail(), responseData.getRecipientIdentifier());

        verify(mockLogic).submitFeedbackResponsesFromStudent(
                any(FeedbackSession.class), any(Student.class), any(FeedbackResponsesRequest.class));
    }

    @Test
    void testExecute_instructorSubmissionNoExistingResponses_success() throws Exception {
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromInstructorForQuestion(spyFeedbackQuestion, stubInstructor))
                .thenReturn(List.of());

        Set<ResponseRecipient> recipients = Set.of(new ResponseRecipient(recipientInstructor1));
        when(mockLogic.getRecipientsOfQuestion(any(FeedbackQuestion.class), any(ResponseGiver.class)))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        FeedbackResponse createdResponse = FeedbackResponse.makeResponse(
                new ResponseGiver(stubInstructor),
                new ResponseRecipient(recipientInstructor1),
                new FeedbackTextResponseDetails("Response for " + recipientInstructor1.getEmail()));
        spyFeedbackQuestion.addFeedbackResponse(createdResponse);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientInstructor1.getEmail(),
                new FeedbackTextResponseDetails("Response for " + recipientInstructor1.getEmail())));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackQuestionResponsesData result = (FeedbackQuestionResponsesData) getJsonResult(action).getOutput();
        List<FeedbackResponseData> submittedResponses = flattenResponses(result);

        assertEquals(1, submittedResponses.size());
        FeedbackResponseData responseData = submittedResponses.get(0);
        assertEquals(stubInstructor.getEmail(), responseData.getGiverIdentifier());
        assertEquals(recipientInstructor1.getEmail(), responseData.getRecipientIdentifier());

        verify(mockLogic).submitFeedbackResponsesFromInstructor(
                any(FeedbackSession.class), any(Instructor.class), any(FeedbackResponsesRequest.class));
    }

    @Test
    void testExecute_withExistingResponses_success() throws Exception {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        FeedbackResponse existingResponse1 = FeedbackResponse.makeResponse(
                new ResponseGiver(stubStudent),
                new ResponseRecipient(recipientStudent1),
                new FeedbackTextResponseDetails("Old response 1"));
        spyFeedbackQuestion.addFeedbackResponse(existingResponse1);
        existingResponse1.setId(UUID.randomUUID());

        FeedbackResponse existingResponse2 = FeedbackResponse.makeResponse(
                new ResponseGiver(stubStudent),
                new ResponseRecipient(recipientStudent2),
                new FeedbackTextResponseDetails("Old response 2"));
        spyFeedbackQuestion.addFeedbackResponse(existingResponse2);
        existingResponse2.setId(UUID.randomUUID());

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of(existingResponse1, existingResponse2));

        Set<ResponseRecipient> recipients = Set.of(
                new ResponseRecipient(recipientStudent1),
                new ResponseRecipient(recipientStudent2));
        when(mockLogic.getRecipientsOfQuestion(any(FeedbackQuestion.class), any(ResponseGiver.class)))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        FeedbackResponse updatedResponse = FeedbackResponse.makeResponse(
                new ResponseGiver(stubStudent),
                new ResponseRecipient(recipientStudent1),
                new FeedbackTextResponseDetails("Updated response 1"));
        spyFeedbackQuestion.addFeedbackResponse(updatedResponse);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("Updated response 1")));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackQuestionResponsesData result = (FeedbackQuestionResponsesData) getJsonResult(action).getOutput();
        List<FeedbackResponseData> submittedResponses = flattenResponses(result);

        assertEquals(1, submittedResponses.size());
        verify(mockLogic).submitFeedbackResponsesFromStudent(
                any(FeedbackSession.class), any(Student.class), any(FeedbackResponsesRequest.class));
    }

    @Test
    void testExecute_teamGiverType_success() throws Exception {
        loginAsStudent(stubStudent.getGoogleId());
        spyFeedbackQuestion.setGiverType(QuestionGiverType.TEAMS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        Set<ResponseRecipient> recipients = Set.of(new ResponseRecipient(recipientStudent1));
        when(mockLogic.getRecipientsOfQuestion(any(FeedbackQuestion.class), any(ResponseGiver.class)))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("Team response")));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackQuestionResponsesData result = (FeedbackQuestionResponsesData) getJsonResult(action).getOutput();
        List<FeedbackResponseData> submittedResponses = flattenResponses(result);

        assertEquals(1, submittedResponses.size());
        assertEquals(stubStudent.getTeamName(), submittedResponses.get(0).getGiverIdentifier());
        verify(mockLogic).submitFeedbackResponsesFromStudent(
                any(FeedbackSession.class), any(Student.class), any(FeedbackResponsesRequest.class));
    }

    @Test
    void testExecute_tooManyRecipients_success() {
        loginAsStudent(stubStudent.getGoogleId());
        spyFeedbackQuestion.setNumOfEntitiesToGiveFeedbackTo(1);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        Set<ResponseRecipient> recipients = Set.of(
                new ResponseRecipient(recipientStudent1),
                new ResponseRecipient(recipientStudent2));
        when(mockLogic.getRecipientsOfQuestion(any(FeedbackQuestion.class), any(ResponseGiver.class)))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("Response 1")));
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent2.getEmail(),
                new FeedbackTextResponseDetails("Response 2")));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackQuestionResponsesData result = (FeedbackQuestionResponsesData) getJsonResult(action).getOutput();
        List<FeedbackResponseData> submittedResponses = flattenResponses(result);

        assertEquals(2, submittedResponses.size());
    }

    @Test
    void testExecute_multipleRecipients_success() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        Set<ResponseRecipient> recipients = Set.of(
                new ResponseRecipient(recipientStudent1),
                new ResponseRecipient(recipientStudent2));
        when(mockLogic.getRecipientsOfQuestion(any(FeedbackQuestion.class), any(ResponseGiver.class)))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("Response for " + recipientStudent1.getEmail())));
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent2.getEmail(),
                new FeedbackTextResponseDetails("Response for " + recipientStudent2.getEmail())));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackQuestionResponsesData result = (FeedbackQuestionResponsesData) getJsonResult(action).getOutput();
        List<FeedbackResponseData> submittedResponses = flattenResponses(result);

        assertEquals(2, submittedResponses.size());
    }

    @Test
    void testExecute_teamBasedRecipients_success() {
        loginAsStudent(stubStudent.getGoogleId());
        spyFeedbackQuestion.setRecipientType(QuestionRecipientType.TEAMS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        String teamName = stubStudent.getTeamName();
        Set<ResponseRecipient> recipients = Set.of(new ResponseRecipient(stubStudent.getTeam()));
        when(mockLogic.getRecipientsOfQuestion(any(FeedbackQuestion.class), any(ResponseGiver.class)))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                teamName,
                new FeedbackTextResponseDetails("Team recipient response")));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackQuestionResponsesData result = (FeedbackQuestionResponsesData) getJsonResult(action).getOutput();
        List<FeedbackResponseData> submittedResponses = flattenResponses(result);

        assertEquals(1, submittedResponses.size());
        assertEquals(teamName, submittedResponses.get(0).getRecipientIdentifier());
    }

    @Test
    void testExecute_idCannotBeConvertedToUuid_throwsInvalidHttpParameterException() {
        loginAsStudent(stubStudent.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, "invalid-uuid",
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("test")));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        InvalidHttpParameterException ihpe = assertThrows(InvalidHttpParameterException.class, action::execute);
        assertEquals("Expected UUID value for fsid parameter, but found: [invalid-uuid]", ihpe.getMessage());
    }

    @Test
    void testExecute_maxPossibleRecipients_success() {
        loginAsStudent(stubStudent.getGoogleId());
        spyFeedbackQuestion.setNumOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(spyFeedbackQuestion, stubStudent))
                .thenReturn(List.of());

        Set<ResponseRecipient> recipients = Set.of(
                new ResponseRecipient(recipientStudent1),
                new ResponseRecipient(recipientStudent2));
        when(mockLogic.getRecipientsOfQuestion(any(FeedbackQuestion.class), any(ResponseGiver.class)))
                .thenReturn(recipients);

        when(mockLogic.getDefaultSectionOrCreate(stubCourse.getId())).thenReturn(stubSection);

        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent1.getEmail(),
                new FeedbackTextResponseDetails("Response 1")));
        responses.add(new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipientStudent2.getEmail(),
                new FeedbackTextResponseDetails("Response 2")));

        FeedbackResponsesRequest requestBody = getRequestBody(responses);

        SubmitFeedbackResponsesAction action = getAction(requestBody, params);
        FeedbackQuestionResponsesData result = (FeedbackQuestionResponsesData) getJsonResult(action).getOutput();
        List<FeedbackResponseData> submittedResponses = flattenResponses(result);

        assertEquals(2, submittedResponses.size());
    }

    @Test
    void testSpecificAccessControl_sessionDoesNotExist_throwsEntityNotFoundException() {
        loginAsStudent(stubStudent.getGoogleId());

        when(mockLogic.getFeedbackSession(stubFeedbackSession.getId())).thenReturn(null);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        SubmitFeedbackResponsesAction action = getAction(params);
        EntityNotFoundException enfe = assertThrows(EntityNotFoundException.class, action::checkAccessControl);
        assertEquals("The feedback session does not exist.", enfe.getMessage());
    }

    @Test
    void testSpecificAccessControl_instructorWithoutSubmitPrivilege_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now().minus(Duration.ofDays(2)));
        stubFeedbackSession.setStartTime(Instant.now().minus(Duration.ofDays(1)));
        stubFeedbackSession.setEndTime(Instant.now().plus(Duration.ofDays(1)));

        InstructorPrivileges privileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.CUSTOM);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, false);
        stubInstructor.setRole(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        stubInstructor.setPrivileges(privileges);

        spyFeedbackQuestion.setGiverType(QuestionGiverType.INSTRUCTORS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_studentEntityDoesNotExist_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());

        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(null);

        spyFeedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorEntityDoesNotExist_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());

        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId())).thenReturn(null);

        spyFeedbackQuestion.setGiverType(QuestionGiverType.INSTRUCTORS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
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

        spyFeedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
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

        spyFeedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
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

        spyFeedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
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

        spyFeedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_previewMode_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());

        spyFeedbackQuestion.setGiverType(QuestionGiverType.INSTRUCTORS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, stubInstructor.getId().toString(),
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

        spyFeedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
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

        spyFeedbackQuestion.setGiverType(QuestionGiverType.INSTRUCTORS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
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

        spyFeedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCanMasquerade(stubStudent.getAccountId(), params);
    }

    @Test
    void testSpecificAccessControl_notLoggedIn_cannotAccess() {
        logoutUser();

        spyFeedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, stubFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCannotAccess(params);
    }
}
