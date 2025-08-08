package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetFeedbackResponsesAction;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.UnauthorizedAccessException;

/**
 * SUT: {@link GetFeedbackResponsesAction}.
 */
public class GetFeedbackResponsesActionTest extends BaseActionTest<GetFeedbackResponsesAction> {
    private Student stubStudent;
    private Instructor stubInstructor;
    private FeedbackQuestion stubFeedbackQuestion;
    private Course stubCourse;
    private List<FeedbackResponse> stubFeedbackResponsesNonNullComments;
    private List<FeedbackResponse> stubFeedbackResponsesNullComments;
    private FeedbackResponsesData stubFeedbackResponsesDataNullComments;
    private FeedbackResponsesData stubFeedbackResponsesDataNonNullComments;
    private FeedbackResponseComment stubFeedbackResponseComment;
    private FeedbackSession stubFeedbackSession;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        stubStudent = getTypicalStudent();
        stubInstructor = getTypicalInstructor();
        stubStudent.setAccount(getTypicalAccount());
        stubInstructor.setAccount(getTypicalAccount());
        stubCourse = getTypicalCourse();
        stubFeedbackSession = getTypicalFeedbackSessionForCourse(stubCourse);
        stubFeedbackQuestion = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        stubFeedbackResponseComment = getTypicalFeedbackResponseComment();

        // First stub FeedbackResponsesData
        FeedbackResponse feedbackResponse1 = getTypicalFeedbackResponseForQuestion(stubFeedbackQuestion);
        FeedbackResponse feedbackResponse2 = getTypicalFeedbackResponseForQuestion(stubFeedbackQuestion);
        feedbackResponse2.setGiver("giver-2");
        feedbackResponse2.setRecipient("recipient-2");
        FeedbackResponse feedbackResponse3 = getTypicalFeedbackResponseForQuestion(stubFeedbackQuestion);
        feedbackResponse3.setGiver("giver-3");
        feedbackResponse3.setRecipient("recipient-3");

        stubFeedbackResponsesNonNullComments = new ArrayList<>();
        stubFeedbackResponsesNonNullComments.add(feedbackResponse1);
        stubFeedbackResponsesNonNullComments.add(feedbackResponse2);
        stubFeedbackResponsesNonNullComments.add(feedbackResponse3);

        List<FeedbackResponseData> feedbackResponseDataListNonNullComments = new ArrayList<>();
        for (FeedbackResponse feedbackResponse : stubFeedbackResponsesNonNullComments) {
            feedbackResponseDataListNonNullComments.add(new FeedbackResponseData(feedbackResponse));
        }
        // Set comment for the first FeedbackResponseData
        feedbackResponseDataListNonNullComments.get(0)
                .setGiverComment(new FeedbackResponseCommentData(stubFeedbackResponseComment));
        stubFeedbackResponsesDataNonNullComments = new FeedbackResponsesData();
        stubFeedbackResponsesDataNonNullComments.setResponses(feedbackResponseDataListNonNullComments);

        // Second stub FeedbackResponsesData
        FeedbackResponse feedbackResponse4 = getTypicalFeedbackResponseForQuestion(stubFeedbackQuestion);
        FeedbackResponse feedbackResponse5 = getTypicalFeedbackResponseForQuestion(stubFeedbackQuestion);
        feedbackResponse4.setGiver("giver-4");
        feedbackResponse4.setRecipient("recipient-4");
        FeedbackResponse feedbackResponse6 = getTypicalFeedbackResponseForQuestion(stubFeedbackQuestion);
        feedbackResponse5.setGiver("giver-5");
        feedbackResponse5.setRecipient("recipient-5");

        stubFeedbackResponsesNullComments = new ArrayList<>();
        stubFeedbackResponsesNullComments.add(feedbackResponse4);
        stubFeedbackResponsesNullComments.add(feedbackResponse5);
        stubFeedbackResponsesNullComments.add(feedbackResponse6);

        List<FeedbackResponseData> feedbackResponseDataListNullComments = new ArrayList<>();
        for (FeedbackResponse feedbackResponse : stubFeedbackResponsesNullComments) {
            feedbackResponseDataListNullComments.add(new FeedbackResponseData(feedbackResponse));
        }
        stubFeedbackResponsesDataNullComments = new FeedbackResponsesData();
        stubFeedbackResponsesDataNullComments.setResponses(feedbackResponseDataListNullComments);
        reset(mockLogic);
    }

    /**
     * Enum for the type of user.
     */
    enum EntityType {
        STUDENT,
        INSTRUCTOR
    }

    void prepareGeneralMocks(EntityType type, boolean isBasicParams, boolean isCommentInResponse) {
        reset(mockLogic);
        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(stubFeedbackQuestion);
        switch (type) {
        case STUDENT:
            if (isBasicParams) {
                when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
            } else {
                when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);
            }

            if (isCommentInResponse) {
                when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(
                        argThat(argument -> argument.getId().equals(stubFeedbackQuestion.getId())),
                        argThat(student -> student.getGoogleId().equals(stubStudent.getGoogleId())
                                && student.getName().equals(stubStudent.getName())
                                && student.getCourse().equals(stubStudent.getCourse()))))
                        .thenReturn(stubFeedbackResponsesNonNullComments);
            } else {
                when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(
                        argThat(argument -> argument.getId().equals(stubFeedbackQuestion.getId())),
                        argThat(student -> student.getGoogleId().equals(stubStudent.getGoogleId())
                                && student.getName().equals(stubStudent.getName())
                                && student.getCourse().equals(stubStudent.getCourse()))))
                        .thenReturn(stubFeedbackResponsesNullComments);
            }
            break;
        case INSTRUCTOR:
            if (isBasicParams) {
                when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                        .thenReturn(stubInstructor);
            } else {
                when(mockLogic.getInstructorForEmail(stubCourse.getId(), stubInstructor.getEmail()))
                        .thenReturn(stubInstructor);
            }

            if (isCommentInResponse) {
                when(mockLogic.getFeedbackResponsesFromInstructorForQuestion(
                        argThat(argument -> argument.getId().equals(stubFeedbackQuestion.getId())),
                        argThat(instructor -> instructor.getGoogleId().equals(stubInstructor.getGoogleId())
                                && instructor.getName().equals(stubInstructor.getName())
                                && instructor.getCourse().equals(stubInstructor.getCourse()))))
                        .thenReturn(stubFeedbackResponsesNonNullComments);
            } else {
                when(mockLogic.getFeedbackResponsesFromInstructorForQuestion(
                        argThat(argument -> argument.getId().equals(stubFeedbackQuestion.getId())),
                        argThat(instructor -> instructor.getGoogleId().equals(stubInstructor.getGoogleId())
                                && instructor.getName().equals(stubInstructor.getName())
                                && instructor.getCourse().equals(stubInstructor.getCourse()))))
                        .thenReturn(stubFeedbackResponsesNullComments);
            }
            break;
        default:
            throw new IllegalArgumentException("Invalid type");
        }
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

        String[] params3 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
        };
        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(stubFeedbackQuestion);
        verifyHttpParameterFailure(params3);

    }

    @Test
    void testExecute_invalidIntent_throwsInvalidHttpParameterException() {
        loginAsStudent(stubStudent.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(stubFeedbackQuestion);
        GetFeedbackResponsesAction action = getAction(params);
        InvalidHttpParameterException ihte = assertThrows(InvalidHttpParameterException.class, action::execute);
        assertEquals("Unknown intent " + Intent.FULL_DETAIL, ihte.getMessage());
    }

    @Test
    void testExecute_studentSubmissionNoPreviewAsNoModeratedPersonNullComments_successfullyGetResponses() {
        loginAsStudent(stubStudent.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        prepareGeneralMocks(EntityType.STUDENT, true, false);
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(
                stubFeedbackResponsesNonNullComments.get(0).getId())).thenReturn(null);
        GetFeedbackResponsesAction action = getAction(params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesDataNullComments, result);
    }

    @Test
    void testExecute_studentSubmissionNoPreviewAsNoModeratedPersonNonNullComments_successfullyGetResponses() {
        loginAsStudent(stubStudent.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        prepareGeneralMocks(EntityType.STUDENT, true, true);
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(
                stubFeedbackResponsesNonNullComments.get(0).getId())).thenReturn(stubFeedbackResponseComment);
        GetFeedbackResponsesAction action = getAction(params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesDataNonNullComments, result);
    }

    @Test
    void testExecute_instructorSubmissionNoPreviewAsNoModeratedPersonNullComments_successfullyGetResponses() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        prepareGeneralMocks(EntityType.INSTRUCTOR, true, false);
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(
                stubFeedbackResponsesNonNullComments.get(0).getId())).thenReturn(null);
        GetFeedbackResponsesAction action = getAction(params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesDataNullComments, result);
    }

    @Test
    void testExecute_instructorSubmissionNoPreviewAsNoModeratedPersonNonNullComments_successfullyGetResponses() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        prepareGeneralMocks(EntityType.INSTRUCTOR, true, true);
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(
                stubFeedbackResponsesNonNullComments.get(0).getId())).thenReturn(stubFeedbackResponseComment);
        GetFeedbackResponsesAction action = getAction(params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesDataNonNullComments, result);
    }

    @Test
    void testExecute_instructorSubmissionNoResponses_emptyResponseData() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(stubFeedbackQuestion);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId())).thenReturn(stubInstructor);
        when(mockLogic.getFeedbackResponsesFromInstructorForQuestion(
                argThat(argument -> argument.getId().equals(stubFeedbackQuestion.getId())),
                argThat(instructor -> instructor.getGoogleId().equals(stubInstructor.getGoogleId())
                        && instructor.getName().equals(stubInstructor.getName())
                        && instructor.getCourse().equals(stubInstructor.getCourse())))).thenReturn(List.of());
        GetFeedbackResponsesAction action = getAction(params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();
        assertEquals(0, result.getResponses().size());
    }

    @Test
    void testExecute_studentSubmissionNoResponses_emptyResponseData() {
        loginAsStudent(stubStudent.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(stubFeedbackQuestion);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
        when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(
                argThat(argument -> argument.getId().equals(stubFeedbackQuestion.getId())),
                argThat(student -> student.getGoogleId().equals(stubStudent.getGoogleId())
                        && student.getName().equals(stubStudent.getName())
                        && student.getCourse().equals(stubStudent.getCourse())))).thenReturn(List.of());
        GetFeedbackResponsesAction action = getAction(params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();
        assertEquals(0, result.getResponses().size());
    }

    @Test
    void testExecute_instructorSubmissionNoPreviewAsModeratedPersonNonNullComments_successfullyGetResponses() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubInstructor.getEmail(),
        };

        prepareGeneralMocks(EntityType.INSTRUCTOR, false, false);

        // Null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(
                stubFeedbackResponsesNonNullComments.get(0).getId())).thenReturn(null);
        GetFeedbackResponsesAction action1 = getAction(params);
        FeedbackResponsesData result1 = (FeedbackResponsesData) getJsonResult(action1).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesDataNullComments, result1);

        prepareGeneralMocks(EntityType.INSTRUCTOR, false, true);
        // Non-null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(
                stubFeedbackResponsesNonNullComments.get(0).getId())).thenReturn(stubFeedbackResponseComment);
        GetFeedbackResponsesAction action2 = getAction(params);
        FeedbackResponsesData result2 = (FeedbackResponsesData) getJsonResult(action2).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesDataNonNullComments, result2);
    }

    @Test
    void testExecute_idCannotBeConvertedToUuid_throwsEntityNotFoundException() {
        loginAsStudent(stubStudent.getGoogleId());
        String[] params1 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "random-invalid-id",
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        GetFeedbackResponsesAction action = getAction(params1);
        EntityNotFoundException enfe = assertThrows(EntityNotFoundException.class, action::execute);
        assertEquals("Feedback Question not found", enfe.getMessage());

        logoutUser();
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "random-invalid-id",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction action2 = getAction(params2);
        EntityNotFoundException enfe2 = assertThrows(EntityNotFoundException.class, action2::execute);
        assertEquals("Feedback Question not found", enfe2.getMessage());
    }

    private void verifyFeedbackResponsesEquals(FeedbackResponsesData expected, FeedbackResponsesData actual) {
        assertEquals(expected.getResponses().size(), actual.getResponses().size());
        List<FeedbackResponseData> feedbackResponsesList = expected.getResponses();
        List<FeedbackResponseData> actualFeedbackResponsesList = actual.getResponses();
        for (int i = 0; i < feedbackResponsesList.size(); i++) {
            // Check for comment nullity mismatch
            if (feedbackResponsesList.get(i).getGiverComment() == null
                    && actualFeedbackResponsesList.get(i).getGiverComment() != null) {
                throw new AssertionError("Expected comment was null, but actual comment was not null");
            }
            if (feedbackResponsesList.get(i).getGiverComment() != null
                    && actualFeedbackResponsesList.get(i).getGiverComment() == null) {
                throw new AssertionError("Expected comment was not null, but actual comment was null");
            }

            // If both comments are null
            if (feedbackResponsesList.get(i).getGiverComment() != null
                    && actualFeedbackResponsesList.get(i).getGiverComment() != null) {
                verifyFeedbackCommentEquals(feedbackResponsesList.get(i), actualFeedbackResponsesList.get(i));
            }
            FeedbackResponseData expectedFeedbackResponse = feedbackResponsesList.get(i);
            FeedbackResponseData actualFeedbackResponse = actualFeedbackResponsesList.get(i);
            assertEquals(expectedFeedbackResponse.getFeedbackResponseId(), actualFeedbackResponse.getFeedbackResponseId());
            assertEquals(expectedFeedbackResponse.getGiverIdentifier(), actualFeedbackResponse.getGiverIdentifier());
            assertEquals(expectedFeedbackResponse.getRecipientIdentifier(), actualFeedbackResponse.getRecipientIdentifier());
            assertEquals(expectedFeedbackResponse.getResponseDetails().getAnswerString(),
                    actualFeedbackResponse.getResponseDetails().getAnswerString());
            assertEquals(expectedFeedbackResponse.getResponseDetails().getQuestionType(),
                    actualFeedbackResponse.getResponseDetails().getQuestionType());
            assertEquals(JsonUtils.toJson(expectedFeedbackResponse.getResponseDetails()),
                    JsonUtils.toJson(actualFeedbackResponse.getResponseDetails()));
        }
    }

    private void verifyFeedbackCommentEquals(FeedbackResponseData expected, FeedbackResponseData actual) {
        FeedbackResponseCommentData expectedComment = expected.getGiverComment();
        FeedbackResponseCommentData actualComment = actual.getGiverComment();
        assert expectedComment != null;
        assert actualComment != null;
        assertEquals(expectedComment.getCommentGiver(), actualComment.getCommentGiver());
        assertEquals(expectedComment.getCommentText(), actualComment.getCommentText());
        assertEquals(expectedComment.getLastEditorEmail(), actualComment.getLastEditorEmail());
    }

    @Test
    void testSpecificAccessControl_notAnswerableForStudent_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());

        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(stubFeedbackQuestion);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        for (FeedbackParticipantType type : FeedbackParticipantType.values()) {
            if (type == FeedbackParticipantType.STUDENTS || type == FeedbackParticipantType.TEAMS) {
                continue;
            }
            stubFeedbackQuestion.setGiverType(type);
            GetFeedbackResponsesAction action = getAction(params);
            UnauthorizedAccessException uae = assertThrows(UnauthorizedAccessException.class,
                    action::checkAccessControl);
            assertEquals("Feedback question is not answerable for students", uae.getMessage());
        }
        verify(mockLogic, times(FeedbackParticipantType.values().length - 2))
                .getFeedbackQuestion(stubFeedbackQuestion.getId());

        // verify only FeedbackParticipantType.STUDENTS and TEAMS are accessible
        for (FeedbackParticipantType type : new FeedbackParticipantType[] { FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.TEAMS }) {
            stubFeedbackQuestion.setGiverType(type);
            verifyCanAccess(params);
        }
    }

    @Test
    void testSpecificAccessControl_notAnswerableForInstructor_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());

        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId()))
                .thenReturn(stubFeedbackQuestion);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId())).thenReturn(stubInstructor);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        for (FeedbackParticipantType type : FeedbackParticipantType.values()) {
            if (type == FeedbackParticipantType.INSTRUCTORS || type == FeedbackParticipantType.SELF) {
                continue;
            }
            stubFeedbackQuestion.setGiverType(type);
            GetFeedbackResponsesAction action = getAction(params);
            UnauthorizedAccessException uae = assertThrows(UnauthorizedAccessException.class,
                    action::checkAccessControl);
            assertEquals("Feedback question is not answerable for instructors", uae.getMessage());
        }
        verify(mockLogic, times(FeedbackParticipantType.values().length - 2))
                .getFeedbackQuestion(stubFeedbackQuestion.getId());

        // verify only FeedbackParticipantType.INSTRUCTORS and SELF are accessible
        for (FeedbackParticipantType type : new FeedbackParticipantType[] { FeedbackParticipantType.INSTRUCTORS,
                FeedbackParticipantType.SELF }) {
            stubFeedbackQuestion.setGiverType(type);
            verifyCanAccess(params);
        }
    }

    @Test
    void testSpecificAccessControl_invalidIntent_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(stubFeedbackQuestion);

        String[] unauthorizedIntentFullDetail = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        GetFeedbackResponsesAction action1 = getAction(unauthorizedIntentFullDetail);
        InvalidHttpParameterException e1 = assertThrows(InvalidHttpParameterException.class,
                action1::checkAccessControl);
        assertEquals("Unknown intent " + Intent.FULL_DETAIL, e1.getMessage());

        String[] unauthorizedIntentInstructorResult = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        GetFeedbackResponsesAction action2 = getAction(unauthorizedIntentInstructorResult);
        InvalidHttpParameterException e2 = assertThrows(InvalidHttpParameterException.class,
                action2::checkAccessControl);
        assertEquals("Unknown intent " + Intent.INSTRUCTOR_RESULT, e2.getMessage());

        String[] unauthorizedIntentStudentResult = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };
        GetFeedbackResponsesAction action3 = getAction(unauthorizedIntentStudentResult);
        InvalidHttpParameterException e3 = assertThrows(InvalidHttpParameterException.class,
                action3::checkAccessControl);
        assertEquals("Unknown intent " + Intent.STUDENT_RESULT, e3.getMessage());
    }

    @Test
    void testSpecificAccessControl_studentSubmissionAsInstructor_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        FeedbackQuestion questionAnswerableToStudent = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());
        questionAnswerableToStudent.setGiverType(FeedbackParticipantType.STUDENTS);
        when(mockLogic.getFeedbackQuestion(questionAnswerableToStudent.getId())).thenReturn(questionAnswerableToStudent);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId())).thenReturn(null);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToStudent.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction action = getAction(params);
        UnauthorizedAccessException uae = assertThrows(UnauthorizedAccessException.class, action::checkAccessControl);
        assertEquals("Trying to access system using a non-existent student entity", uae.getMessage());
    }

    @Test
    void testSpecificAccessControl_instructorSubmissionAsStudent_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(stubFeedbackQuestion);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(null);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction action = getAction(params);
        UnauthorizedAccessException uae = assertThrows(UnauthorizedAccessException.class, action::checkAccessControl);
        assertEquals("Trying to access system using a non-existent instructor entity", uae.getMessage());
    }

    @Test
    void testSpecificAccessControl_invalidQuestion_throwsEntityNotFoundException() {
        loginAsInstructor(stubInstructor.getGoogleId());
        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(null);
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction action = getAction(params);
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, action::checkAccessControl);
        assertEquals("Feedback Question not found", e.getMessage());
    }

    @Test
    void testSpecificAccessControl_typicalInstructorAccess_canAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        FeedbackQuestion questionAnswerableToInstructor = stubFeedbackQuestion;
        when(mockLogic.getFeedbackQuestion(questionAnswerableToInstructor.getId()))
                .thenReturn(questionAnswerableToInstructor);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId())).thenReturn(stubInstructor);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToInstructor.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithNoPrivileges_cannotAccess() {
        InstructorPrivileges customInstructorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        InstructorPermissionRole customRole = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        Instructor stubInstructorWithoutPrivileges = new Instructor(stubCourse, "instructor-1-name", "valid1@teammates.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, customRole, customInstructorPrivileges);
        loginAsInstructor(stubInstructorWithoutPrivileges.getGoogleId());

        FeedbackQuestion questionAnswerableToInstructor = stubFeedbackQuestion;
        when(mockLogic.getFeedbackQuestion(questionAnswerableToInstructor.getId()))
                .thenReturn(questionAnswerableToInstructor);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithoutPrivileges.getGoogleId()))
                .thenReturn(stubInstructorWithoutPrivileges);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);

        String[] params1 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToInstructor.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction action = getAction(params1);
        UnauthorizedAccessException uae = assertThrows(UnauthorizedAccessException.class, action::checkAccessControl);
        assertEquals("You don't have submission privilege", uae.getMessage());

        questionAnswerableToInstructor.setShowGiverNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionAnswerableToInstructor.setShowRecipientNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionAnswerableToInstructor.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        when(mockLogic.getInstructorForEmail(stubCourse.getId(), stubInstructorWithoutPrivileges.getEmail()))
                .thenReturn(stubInstructorWithoutPrivileges);

        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToInstructor.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubInstructorWithoutPrivileges.getEmail(),
        };
        GetFeedbackResponsesAction action2 = getAction(params2);
        UnauthorizedAccessException uae2 = assertThrows(UnauthorizedAccessException.class, action2::checkAccessControl);
        assertEquals("Feedback session [test-feedbacksession] is not accessible to instructor "
                + "[valid1@teammates.tmt] for privilege [canmodifysessioncommentinsection]", uae2.getMessage());
    }

    @Test
    void testSpecificAccessControl_typicalStudentAccess_canAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        FeedbackQuestion questionAnswerableToStudent = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());
        questionAnswerableToStudent.setGiverType(FeedbackParticipantType.STUDENTS);
        when(mockLogic.getFeedbackQuestion(questionAnswerableToStudent.getId())).thenReturn(questionAnswerableToStudent);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToStudent.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_previewAs_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        FeedbackQuestion questionAnswerableToInstructor = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        when(mockLogic.getFeedbackQuestion(questionAnswerableToInstructor.getId()))
                .thenReturn(questionAnswerableToInstructor);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);

        String[] params1 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToInstructor.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, stubInstructor.getEmail(),
        };
        GetFeedbackResponsesAction action = getAction(params1);
        UnauthorizedAccessException uae = assertThrows(UnauthorizedAccessException.class, action::checkAccessControl);
        assertEquals("You are not allowed to see responses when previewing", uae.getMessage());

        FeedbackQuestion questionThatCanBeModerated = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        questionThatCanBeModerated.setShowGiverNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionThatCanBeModerated.setShowRecipientNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionThatCanBeModerated.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        when(mockLogic.getFeedbackQuestion(questionThatCanBeModerated.getId())).thenReturn(questionThatCanBeModerated);

        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionThatCanBeModerated.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, stubInstructor.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubInstructor.getEmail(),
        };
        GetFeedbackResponsesAction action2 = getAction(params2);
        UnauthorizedAccessException uae2 = assertThrows(UnauthorizedAccessException.class, action2::checkAccessControl);
        assertEquals("You are not allowed to see responses when previewing", uae2.getMessage());

    }

    @Test
    void testSpecificAccessControl_questionModeratedInstructorSubmission_canAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        FeedbackQuestion questionThatCanBeModerated = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        questionThatCanBeModerated.setShowGiverNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionThatCanBeModerated.setShowRecipientNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionThatCanBeModerated.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        when(mockLogic.getFeedbackQuestion(questionThatCanBeModerated.getId())).thenReturn(questionThatCanBeModerated);
        when(mockLogic.getInstructorForEmail(stubCourse.getId(), stubInstructor.getEmail())).thenReturn(stubInstructor);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId())).thenReturn(stubInstructor);

        String[] params1 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionThatCanBeModerated.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubInstructor.getEmail(),
        };
        verifyCanAccess(params1);
    }

    @Test
    void testSpecificAccessControl_questionModeratedStudentSubmission_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        FeedbackQuestion questionAnswerableToStudent = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());
        questionAnswerableToStudent.setGiverType(FeedbackParticipantType.STUDENTS);
        questionAnswerableToStudent.setShowRecipientNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionAnswerableToStudent.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionAnswerableToStudent.setShowGiverNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        when(mockLogic.getFeedbackQuestion(questionAnswerableToStudent.getId())).thenReturn(questionAnswerableToStudent);
        when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId())).thenReturn(null);

        String[] params1 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToStudent.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubStudent.getEmail(),
        };
        GetFeedbackResponsesAction action1 = getAction(params1);
        UnauthorizedAccessException uae1 = assertThrows(UnauthorizedAccessException.class, action1::checkAccessControl);
        assertEquals("Trying to access system using a non-existent instructor entity", uae1.getMessage());

        FeedbackQuestion questionThatCannotBeModerated = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());
        questionThatCannotBeModerated.setGiverType(FeedbackParticipantType.STUDENTS);
        when(mockLogic.getFeedbackQuestion(questionThatCannotBeModerated.getId())).thenReturn(questionThatCannotBeModerated);

        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionThatCannotBeModerated.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubStudent.getEmail(),
        };
        GetFeedbackResponsesAction action2 = getAction(params2);
        UnauthorizedAccessException uae2 = assertThrows(UnauthorizedAccessException.class, action2::checkAccessControl);
        assertEquals("The question is not applicable for moderation", uae2.getMessage());
    }

    @Test
    void testSpecificAccessControl_questionCannotBeModeratedInstructorSubmission_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        FeedbackQuestion questionThatCannotBeModerated = stubFeedbackQuestion;

        when(mockLogic.getFeedbackQuestion(questionThatCannotBeModerated.getId())).thenReturn(questionThatCannotBeModerated);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionThatCannotBeModerated.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubInstructor.getEmail(),
        };
        GetFeedbackResponsesAction action = getAction(params);
        UnauthorizedAccessException uae = assertThrows(UnauthorizedAccessException.class, action::checkAccessControl);
        assertEquals("The question is not applicable for moderation", uae.getMessage());
    }

    @Test
    void testSpecificAccessControl_accessAcrossCourses_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        Course anotherCourse = getTypicalCourse();
        anotherCourse.setId("another-course-id");
        anotherCourse.setName("another-course-name");
        FeedbackSession anotherFeedbackSession = getTypicalFeedbackSessionForCourse(anotherCourse);
        FeedbackQuestion anotherFeedbackQuestion = getTypicalFeedbackQuestionForSession(anotherFeedbackSession);
        anotherFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);
        anotherFeedbackSession.setSessionVisibleFromTime(Instant.now());

        when(mockLogic.getFeedbackQuestion(anotherFeedbackQuestion.getId())).thenReturn(anotherFeedbackQuestion);
        when(mockLogic.getStudentByGoogleId(anotherCourse.getId(), stubStudent.getGoogleId())).thenReturn(null);
        when(mockLogic.getFeedbackSession(anotherFeedbackSession.getName(), anotherFeedbackSession.getCourseId()))
                .thenReturn(anotherFeedbackSession);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, anotherFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction action = getAction(params);
        UnauthorizedAccessException uae = assertThrows(UnauthorizedAccessException.class, action::checkAccessControl);
        assertEquals("Trying to access system using a non-existent student entity", uae.getMessage());
    }

    @Test
    void testSpecificAccessControl_idCannotBeConvertedToUuid_throwsEntityNotFoundException() {
        loginAsInstructor(stubInstructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "random-invalid-id",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction action = getAction(params);
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, action::checkAccessControl);
        assertEquals("Feedback Question not found", e.getMessage());
    }

    @Test
    void testSpecificAccessControl_entityDoesNotBelongInCourse_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(stubFeedbackQuestion);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId())).thenReturn(null);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);

        String[] params1 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction action1 = getAction(params1);
        UnauthorizedAccessException uae1 = assertThrows(UnauthorizedAccessException.class, action1::checkAccessControl);
        assertEquals("Trying to access system using a non-existent instructor entity", uae1.getMessage());

        logoutUser();
        loginAsStudent(stubStudent.getGoogleId());
        FeedbackQuestion questionAnswerableToStudent = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());
        questionAnswerableToStudent.setGiverType(FeedbackParticipantType.STUDENTS);
        when(mockLogic.getFeedbackQuestion(questionAnswerableToStudent.getId())).thenReturn(questionAnswerableToStudent);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(null);

        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToStudent.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction action2 = getAction(params2);
        UnauthorizedAccessException uae2 = assertThrows(UnauthorizedAccessException.class, action2::checkAccessControl);
        assertEquals("Trying to access system using a non-existent student entity", uae2.getMessage());
    }

    @Test
    void testSpecificAccessControl_notLoggedIn_cannotAccess() {
        logoutUser();
        // Instructor
        FeedbackQuestion questionThatCanBeModerated = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        questionThatCanBeModerated.setShowGiverNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionThatCanBeModerated.setShowRecipientNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionThatCanBeModerated.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        when(mockLogic.getFeedbackQuestion(questionThatCanBeModerated.getId())).thenReturn(questionThatCanBeModerated);
        when(mockLogic.getInstructorForEmail(stubCourse.getId(), stubInstructor.getEmail())).thenReturn(stubInstructor);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);

        String[] params1 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionThatCanBeModerated.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubInstructor.getEmail(),
        };
        GetFeedbackResponsesAction action1 = getAction(params1);
        UnauthorizedAccessException uae1 = assertThrows(UnauthorizedAccessException.class, action1::checkAccessControl);
        assertEquals("User is not logged in", uae1.getMessage());

        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), null)).thenReturn(null);
        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionThatCanBeModerated.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction action2 = getAction(params2);
        UnauthorizedAccessException uae2 = assertThrows(UnauthorizedAccessException.class, action2::checkAccessControl);
        assertEquals("Trying to access system using a non-existent instructor entity", uae2.getMessage());
        verify(mockLogic, never()).getInstructorByGoogleId(stubCourse.getId(), null);

        // Student
        FeedbackQuestion questionAnswerableToStudent = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());
        questionAnswerableToStudent.setGiverType(FeedbackParticipantType.STUDENTS);
        when(mockLogic.getFeedbackQuestion(questionAnswerableToStudent.getId())).thenReturn(questionAnswerableToStudent);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), null)).thenReturn(null);

        String[] params3 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToStudent.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction action3 = getAction(params3);
        UnauthorizedAccessException uae3 = assertThrows(UnauthorizedAccessException.class, action3::checkAccessControl);
        assertEquals("Trying to access system using a non-existent student entity", uae3.getMessage());

        questionAnswerableToStudent.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionAnswerableToStudent.setShowRecipientNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionAnswerableToStudent.setShowGiverNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);

        String[] params4 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToStudent.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubStudent.getEmail(),
        };
        GetFeedbackResponsesAction action4 = getAction(params4);
        UnauthorizedAccessException uae4 = assertThrows(UnauthorizedAccessException.class, action4::checkAccessControl);
        assertEquals("User is not logged in", uae4.getMessage());
    }

    @Test
    void testSpecificAccessControl_instructorWithOnlyModifySessionCommentPrivileges_canAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        Instructor instructorWithLimitedPrivileges =
                getInstructorWithLimitedPrivileges(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        instructorWithLimitedPrivileges.setAccount(getTypicalAccount());

        FeedbackQuestion questionThatCanBeModerated = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        questionThatCanBeModerated.setShowGiverNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionThatCanBeModerated.setShowRecipientNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionThatCanBeModerated.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));

        when(mockLogic.getFeedbackQuestion(questionThatCanBeModerated.getId())).thenReturn(questionThatCanBeModerated);
        when(mockLogic.getInstructorForEmail(stubCourse.getId(), instructorWithLimitedPrivileges.getEmail()))
                .thenReturn(instructorWithLimitedPrivileges);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), instructorWithLimitedPrivileges.getGoogleId()))
                .thenReturn(instructorWithLimitedPrivileges);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionThatCanBeModerated.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, instructorWithLimitedPrivileges.getEmail(),
        };
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithOnlySubmitSectionPrivileges_canAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        Instructor instructorWithLimitedPrivileges =
                getInstructorWithLimitedPrivileges(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
        instructorWithLimitedPrivileges.setAccount(getTypicalAccount());

        FeedbackQuestion questionAnswerableToInstructor = stubFeedbackQuestion;
        when(mockLogic.getFeedbackQuestion(questionAnswerableToInstructor.getId()))
                .thenReturn(questionAnswerableToInstructor);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId())).thenReturn(stubInstructor);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToInstructor.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyCanAccess(params);
    }

    private @NotNull Instructor getInstructorWithLimitedPrivileges(String privilege) {
        InstructorPermissionRole customRole = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        InstructorPrivileges submitSessionPrivilegesOnly =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        submitSessionPrivilegesOnly.updatePrivilege(stubFeedbackSession.getName(), privilege, true);
        return new Instructor(stubCourse, "instructor-2-name", "valid2@teammates.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, customRole, submitSessionPrivilegesOnly);
    }
}
