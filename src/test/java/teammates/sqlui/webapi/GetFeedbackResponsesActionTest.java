package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.reset;
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
import teammates.ui.webapi.GetFeedbackResponsesAction;
import teammates.ui.webapi.InvalidHttpParameterException;

/**
 * SUT: {@link GetFeedbackResponsesAction}.
 */
public class GetFeedbackResponsesActionTest extends BaseActionTest<GetFeedbackResponsesAction> {
    private Student stubStudent;
    private Instructor stubInstructor;
    private FeedbackQuestion stubFeedbackQuestion;
    private Course stubCourse;
    private List<FeedbackResponse> stubFeedbackResponses;
    private FeedbackResponsesData stubFeedbackResponsesData;
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

        FeedbackResponse feedbackResponse1 = getTypicalFeedbackResponseForQuestion(stubFeedbackQuestion);
        FeedbackResponse feedbackResponse2 = getTypicalFeedbackResponseForQuestion(stubFeedbackQuestion);
        feedbackResponse2.setGiver("giver-2");
        feedbackResponse2.setRecipient("recipient-2");
        FeedbackResponse feedbackResponse3 = getTypicalFeedbackResponseForQuestion(stubFeedbackQuestion);
        feedbackResponse3.setGiver("giver-3");
        feedbackResponse3.setRecipient("recipient-3");

        stubFeedbackResponses = new ArrayList<>();
        stubFeedbackResponses.add(feedbackResponse1);
        stubFeedbackResponses.add(feedbackResponse2);
        stubFeedbackResponses.add(feedbackResponse3);

        List<FeedbackResponseData> feedbackResponseDataList = new ArrayList<>();
        for (FeedbackResponse feedbackResponse : stubFeedbackResponses) {
            feedbackResponseDataList.add(new FeedbackResponseData(feedbackResponse));
        }
        // Only set comments for the first FeedbackResponseData
        feedbackResponseDataList.get(0).setGiverComment(new FeedbackResponseCommentData(stubFeedbackResponseComment));

        stubFeedbackResponsesData = new FeedbackResponsesData();
        stubFeedbackResponsesData.setResponses(feedbackResponseDataList);

        reset(mockLogic);
    }

    /**
     * Enum for the type of user.
     */
    enum EntityType {
        STUDENT,
        INSTRUCTOR
    }

    void prepareGeneralMocks(EntityType type, boolean isBasicParams) {
        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(stubFeedbackQuestion);
        switch (type) {
        case STUDENT:
            if (isBasicParams) {
                when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
            } else {
                when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);
            }
            when(mockLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(
                    argThat(argument -> argument.getId().equals(stubFeedbackQuestion.getId())),
                    argThat(student -> student.getGoogleId().equals(stubStudent.getGoogleId())
                            && student.getName().equals(stubStudent.getName())
                            && student.getCourse().equals(stubStudent.getCourse())))).thenReturn(stubFeedbackResponses);
            break;
        case INSTRUCTOR:
            if (isBasicParams) {
                when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                        .thenReturn(stubInstructor);
            } else {
                when(mockLogic.getInstructorForEmail(stubCourse.getId(), stubInstructor.getEmail()))
                        .thenReturn(stubInstructor);
            }
            when(mockLogic.getFeedbackResponsesFromInstructorForQuestion(
                    argThat(argument -> argument.getId().equals(stubFeedbackQuestion.getId())),
                    argThat(instructor -> instructor.getGoogleId().equals(stubInstructor.getGoogleId())
                            && instructor.getName().equals(stubInstructor.getName())
                            && instructor.getCourse().equals(stubInstructor.getCourse()))))
                    .thenReturn(stubFeedbackResponses);
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
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_studentSubmissionNoPreviewAsNoModeratedPersonNullComments_successfullyGetResponses() {
        loginAsStudent(stubStudent.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        prepareGeneralMocks(EntityType.STUDENT, true);
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(null);
        GetFeedbackResponsesAction action = getAction(params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result);
    }

    @Test
    void testExecute_studentSubmissionNoPreviewAsNoModeratedPersonNonNullComments_successfullyGetResponses() {
        loginAsStudent(stubStudent.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        prepareGeneralMocks(EntityType.STUDENT, true);
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(stubFeedbackResponseComment);
        GetFeedbackResponsesAction action = getAction(params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result);
    }

    @Test
    void testExecute_instructorSubmissionNoPreviewAsNoModeratedPersonNullComments_successfullyGetResponses() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        prepareGeneralMocks(EntityType.INSTRUCTOR, true);
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(null);
        GetFeedbackResponsesAction action = getAction(params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result);
    }

    @Test
    void testExecute_instructorSubmissionNoPreviewAsNoModeratedPersonNonNullComments_successfullyGetResponses() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        prepareGeneralMocks(EntityType.INSTRUCTOR, true);
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(stubFeedbackResponseComment);
        GetFeedbackResponsesAction action = getAction(params);
        FeedbackResponsesData result = (FeedbackResponsesData) getJsonResult(action).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result);
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
    void testExecute_studentSubmissionNoPreviewAsModeratedPerson_successfullyGetResponses() {
        loginAsStudent(stubStudent.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubStudent.getEmail(),
        };

        prepareGeneralMocks(EntityType.STUDENT, false);

        // Null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(null);
        GetFeedbackResponsesAction action1 = getAction(params);
        FeedbackResponsesData result1 = (FeedbackResponsesData) getJsonResult(action1).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result1);

        // Non-null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(stubFeedbackResponseComment);
        GetFeedbackResponsesAction action2 = getAction(params);
        FeedbackResponsesData result2 = (FeedbackResponsesData) getJsonResult(action2).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result2);

    }

    @Test
    void testExecute_studentSubmissionPreviewAsNoModeratedPerson_successfullyGetResponses() {
        loginAsStudent(stubStudent.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, stubStudent.getEmail(),
        };

        prepareGeneralMocks(EntityType.STUDENT, false);

        // Null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(null);
        GetFeedbackResponsesAction action1 = getAction(params);
        FeedbackResponsesData result1 = (FeedbackResponsesData) getJsonResult(action1).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result1);

        // Non-null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(stubFeedbackResponseComment);
        GetFeedbackResponsesAction action2 = getAction(params);
        FeedbackResponsesData result2 = (FeedbackResponsesData) getJsonResult(action2).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result2);
    }

    @Test
    void testExecute_studentSubmissionAllParams_successfullyGetResponses() {
        loginAsStudent(stubStudent.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, stubStudent.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubStudent.getEmail(),
        };

        prepareGeneralMocks(EntityType.STUDENT, false);

        // Null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(null);
        GetFeedbackResponsesAction action1 = getAction(params);
        FeedbackResponsesData result1 = (FeedbackResponsesData) getJsonResult(action1).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result1);

        // Non-null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(stubFeedbackResponseComment);
        GetFeedbackResponsesAction action2 = getAction(params);
        FeedbackResponsesData result2 = (FeedbackResponsesData) getJsonResult(action2).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result2);
    }

    @Test
    void testExecute_instructorSubmissionPreviewAsNoModeratedPerson_successfullyGetResponses() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, stubInstructor.getEmail(),
        };

        prepareGeneralMocks(EntityType.INSTRUCTOR, false);

        // Null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(null);
        GetFeedbackResponsesAction action1 = getAction(params);
        FeedbackResponsesData result1 = (FeedbackResponsesData) getJsonResult(action1).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result1);

        // Non-null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(stubFeedbackResponseComment);
        GetFeedbackResponsesAction action2 = getAction(params);
        FeedbackResponsesData result2 = (FeedbackResponsesData) getJsonResult(action2).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result2);
    }

    @Test
    void testExecute_instructorSubmissionNoPreviewAsModeratedPersonNonNullComments_successfullyGetResponses() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubInstructor.getEmail(),
        };

        prepareGeneralMocks(EntityType.INSTRUCTOR, false);

        // Null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(null);
        GetFeedbackResponsesAction action1 = getAction(params);
        FeedbackResponsesData result1 = (FeedbackResponsesData) getJsonResult(action1).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result1);

        // Non-null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(stubFeedbackResponseComment);
        GetFeedbackResponsesAction action2 = getAction(params);
        FeedbackResponsesData result2 = (FeedbackResponsesData) getJsonResult(action2).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result2);
    }

    @Test
    void testExecute_instructorSubmissionAllParams_successfullyGetResponses() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, stubInstructor.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubInstructor.getEmail(),
        };

        prepareGeneralMocks(EntityType.INSTRUCTOR, false);

        // Null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(null);
        GetFeedbackResponsesAction action1 = getAction(params);
        FeedbackResponsesData result1 = (FeedbackResponsesData) getJsonResult(action1).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result1);

        // Non-null comments
        when(mockLogic.getFeedbackResponseCommentForResponseFromParticipant(stubFeedbackResponses.get(0).getId()))
                .thenReturn(stubFeedbackResponseComment);
        GetFeedbackResponsesAction action2 = getAction(params);
        FeedbackResponsesData result2 = (FeedbackResponsesData) getJsonResult(action2).getOutput();
        verifyFeedbackResponsesEquals(stubFeedbackResponsesData, result2);
    }

    @Test
    void testExecute_idCannotBeConvertedToUuid_throwsEntityNotFoundException() {
        loginAsStudent(stubStudent.getGoogleId());
        String[] params1 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenThrow(InvalidHttpParameterException.class);
        verifyEntityNotFound(params1);

        logoutUser();
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyEntityNotFound(params2);
    }

    private void verifyFeedbackResponsesEquals(FeedbackResponsesData expected, FeedbackResponsesData actual) {
        assertEquals(expected.getResponses().size(), actual.getResponses().size());
        List<FeedbackResponseData> feedbackResponsesList = expected.getResponses();
        List<FeedbackResponseData> actualFeedbackResponsesList = actual.getResponses();
        for (int i = 0; i < feedbackResponsesList.size(); i++) {
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
    void testSpecificAccessControl_notAnswerable_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        FeedbackQuestion questionNotAnswerableToStudent = stubFeedbackQuestion;

        String[] params1 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionNotAnswerableToStudent.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        when(mockLogic.getFeedbackQuestion(questionNotAnswerableToStudent.getId()))
                .thenReturn(questionNotAnswerableToStudent);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
        verifyCannotAccess(params1);

        logoutUser();
        loginAsInstructor(stubInstructor.getGoogleId());
        FeedbackQuestion questionNotAnswerableToInstructor = getTypicalFeedbackQuestionForSession(stubFeedbackSession);

        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionNotAnswerableToInstructor.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        questionNotAnswerableToInstructor.setGiverType(FeedbackParticipantType.OWN_TEAM);
        when(mockLogic.getFeedbackQuestion(questionNotAnswerableToInstructor.getId()))
                .thenReturn(questionNotAnswerableToInstructor);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId())).thenReturn(stubInstructor);
        verifyCannotAccess(params2);
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
        verifyHttpParameterFailureAcl(unauthorizedIntentFullDetail);

        String[] unauthorizedIntentInstructorResult = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        verifyHttpParameterFailureAcl(unauthorizedIntentInstructorResult);

        String[] unauthorizedIntentStudentResult = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        verifyHttpParameterFailureAcl(unauthorizedIntentStudentResult);
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
        verifyCannotAccess(params);
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
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_invalidQuestion_throwsEntityNotFoundException() {
        loginAsInstructor(stubInstructor.getGoogleId());
        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(null);
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyEntityNotFoundAcl(params);
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
        verifyCannotAccess(params1);

        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToInstructor.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubInstructorWithoutPrivileges.getEmail(),
        };
        when(mockLogic.getInstructorForEmail(stubCourse.getId(), stubInstructorWithoutPrivileges.getEmail()))
                .thenReturn(stubInstructorWithoutPrivileges);
        verifyCannotAccess(params2);
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
        verifyCannotAccess(params1);

        FeedbackQuestion questionThatCanBeModerated = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        questionThatCanBeModerated.setShowGiverNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionThatCanBeModerated.setShowRecipientNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionThatCanBeModerated.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionThatCanBeModerated.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, stubInstructor.getEmail(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubInstructor.getEmail(),
        };
        when(mockLogic.getFeedbackQuestion(questionThatCanBeModerated.getId())).thenReturn(questionThatCanBeModerated);
        verifyCannotAccess(params2);
    }

    @Test
    void testSpecificAccessControl_questionCanBeModeratedWithParams_canAccess() {
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

        logoutUser();
        loginAsStudent(stubStudent.getGoogleId());
        FeedbackQuestion questionAnswerableToStudent = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());
        questionAnswerableToStudent.setGiverType(FeedbackParticipantType.STUDENTS);
        questionAnswerableToStudent.setShowRecipientNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionAnswerableToStudent.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionAnswerableToStudent.setShowGiverNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);
        when(mockLogic.getFeedbackQuestion(questionAnswerableToStudent.getId())).thenReturn(questionAnswerableToStudent);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
        when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);

        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToStudent.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubStudent.getEmail(),
        };
        verifyCanAccess(params2);
    }

    @Test
    void testSpecificAccessControl_questionCannotBeModeratedWithParams_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        FeedbackQuestion questionThatCannotBeModerated = stubFeedbackQuestion;
        when(mockLogic.getFeedbackQuestion(questionThatCannotBeModerated.getId())).thenReturn(questionThatCannotBeModerated);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);

        String[] params1 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionThatCannotBeModerated.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubInstructor.getEmail(),
        };
        verifyCannotAccess(params1);

        logoutUser();
        loginAsStudent(stubStudent.getGoogleId());
        FeedbackQuestion questionAnswerableToStudent = getTypicalFeedbackQuestionForSession(stubFeedbackSession);
        stubFeedbackSession.setSessionVisibleFromTime(Instant.now());
        questionAnswerableToStudent.setGiverType(FeedbackParticipantType.STUDENTS);
        when(mockLogic.getFeedbackQuestion(questionAnswerableToStudent.getId())).thenReturn(questionAnswerableToStudent);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);

        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToStudent.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubStudent.getEmail(),
        };
        verifyCannotAccess(params2);
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
        when(mockLogic.getStudentByGoogleId(anotherCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
        when(mockLogic.getFeedbackSession(anotherFeedbackSession.getName(), anotherFeedbackSession.getCourseId()))
                .thenReturn(anotherFeedbackSession);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, anotherFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_idCannotBeConvertedToUuid_throwsEntityNotFoundException() {
        loginAsInstructor(stubInstructor.getGoogleId());
        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenThrow(InvalidHttpParameterException.class);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyEntityNotFoundAcl(params);
    }

    @Test
    void testSpecificAccessControl_invalidEntity_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        when(mockLogic.getFeedbackQuestion(stubFeedbackQuestion.getId())).thenReturn(stubFeedbackQuestion);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId())).thenReturn(null);
        when(mockLogic.getFeedbackSession(stubFeedbackSession.getName(), stubFeedbackSession.getCourseId()))
                .thenReturn(stubFeedbackSession);

        String[] params1 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, stubFeedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyCannotAccess(params1);

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
        verifyCannotAccess(params2);
    }

    @Test
    void testSpecificAccessControl_notLoggedIn_cannotAccess() {
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
        verifyCannotAccess(params1);

        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), null)).thenReturn(null);
        String[] params2 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionThatCanBeModerated.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyCannotAccess(params2);

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
        verifyCannotAccess(params3);

        questionAnswerableToStudent.setShowResponsesTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionAnswerableToStudent.setShowRecipientNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        questionAnswerableToStudent.setShowGiverNameTo(List.of(FeedbackParticipantType.INSTRUCTORS));
        when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);

        String[] params4 = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionAnswerableToStudent.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, stubStudent.getEmail(),
        };
        verifyCannotAccess(params4);

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
