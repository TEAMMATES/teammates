package teammates.ui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackVisibilityType;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.request.FeedbackQuestionUpdateRequest;

/**
 * SUT: {@link UpdateFeedbackQuestionAction}.
 */
public class UpdateFeedbackQuestionActionTest extends BaseActionTest<UpdateFeedbackQuestionAction> {

    private Instructor typicalInstructor;
    private Course typicalCourse;
    private FeedbackSession typicalFeedbackSession;
    private FeedbackQuestion typicalFeedbackQuestion;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.QUESTION;
    }

    @Override
    String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        typicalInstructor = getTypicalInstructor();
        typicalCourse = typicalInstructor.getCourse();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
        typicalFeedbackQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
    }

    @Test
    void testExecute_typicalCase_success() throws Exception {
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        FeedbackQuestion updatedQuestion = getUpdatedFeedbackQuestion();
        FeedbackQuestionUpdateRequest updateRequest = getTypicalTextQuestionUpdateRequest();

        when(mockLogic.updateFeedbackQuestionCascade(any(UUID.class), any(FeedbackQuestionUpdateRequest.class)))
                .thenReturn(updatedQuestion);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        UpdateFeedbackQuestionAction a = getAction(updateRequest, params);
        JsonResult r = getJsonResult(a);
        FeedbackQuestionData response = (FeedbackQuestionData) r.getOutput();

        assertEquals(updatedQuestion.getQuestionNumber().intValue(), response.getQuestionNumber());
        assertEquals(2, updatedQuestion.getQuestionNumber().intValue());

        assertEquals(updatedQuestion.getQuestionDetailsCopy().getQuestionText(), response.getQuestionBrief());
        assertEquals("this is the brief", updatedQuestion.getQuestionDetailsCopy().getQuestionText());

        assertEquals(updatedQuestion.getQuestionType(), response.getQuestionType());
        assertEquals(FeedbackQuestionType.TEXT, updatedQuestion.getQuestionType());

        assertEquals(JsonUtils.toJson(updatedQuestion.getQuestionDetailsCopy()),
                JsonUtils.toJson(response.getQuestionDetails()));
        assertEquals(800, ((FeedbackTextQuestionDetails)
                updatedQuestion.getQuestionDetailsCopy()).getRecommendedLength().intValue());

        assertEquals(updatedQuestion.getGiverType(), response.getGiverType());
        assertEquals(QuestionGiverType.STUDENTS, updatedQuestion.getGiverType());

        assertEquals(updatedQuestion.getRecipientType(), response.getRecipientType());
        assertEquals(QuestionRecipientType.INSTRUCTORS, updatedQuestion.getRecipientType());

        assertEquals(NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
                response.getNumberOfEntitiesToGiveFeedbackToSetting());
        assertEquals(2, updatedQuestion.getNumOfEntitiesToGiveFeedbackTo().intValue());

        assertTrue(response.getShowResponsesTo().isEmpty());
        assertTrue(updatedQuestion.getShowResponsesTo().isEmpty());
        assertTrue(response.getShowGiverNameTo().isEmpty());
        assertTrue(updatedQuestion.getShowGiverNameTo().isEmpty());
        assertTrue(response.getShowRecipientNameTo().isEmpty());
        assertTrue(updatedQuestion.getShowRecipientNameTo().isEmpty());
    }

    @Test
    void testExecute_changeVisibility_success() throws Exception {
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        FeedbackQuestion updatedQuestion = getUpdatedFeedbackQuestion();
        updatedQuestion.setGiverType(QuestionGiverType.INSTRUCTORS);
        updatedQuestion.setRecipientType(QuestionRecipientType.TEAMS);
        updatedQuestion.setShowResponsesTo(Arrays.asList(ViewerType.RECEIVER));
        updatedQuestion.setShowGiverNameTo(Arrays.asList(ViewerType.RECEIVER));
        updatedQuestion.setShowRecipientNameTo(Arrays.asList(ViewerType.RECEIVER));

        when(mockLogic.updateFeedbackQuestionCascade(any(UUID.class), any(FeedbackQuestionUpdateRequest.class)))
                .thenReturn(updatedQuestion);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        FeedbackQuestionUpdateRequest updateRequest = getTypicalTextQuestionUpdateRequest();
        updateRequest.setGiverType(QuestionGiverType.INSTRUCTORS);
        updateRequest.setRecipientType(QuestionRecipientType.TEAMS);
        updateRequest.setShowResponsesTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));
        updateRequest.setShowGiverNameTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));
        updateRequest.setShowRecipientNameTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));

        UpdateFeedbackQuestionAction a = getAction(updateRequest, params);
        JsonResult r = getJsonResult(a);
        FeedbackQuestionData response = (FeedbackQuestionData) r.getOutput();

        assertEquals(updatedQuestion.getGiverType(), response.getGiverType());
        assertEquals(QuestionGiverType.INSTRUCTORS, updatedQuestion.getGiverType());

        assertEquals(updatedQuestion.getRecipientType(), response.getRecipientType());
        assertEquals(QuestionRecipientType.TEAMS, updatedQuestion.getRecipientType());

        assertEquals(Arrays.asList(FeedbackVisibilityType.RECIPIENT), response.getShowResponsesTo());
        assertEquals(Arrays.asList(ViewerType.RECEIVER), updatedQuestion.getShowResponsesTo());

        assertEquals(Arrays.asList(FeedbackVisibilityType.RECIPIENT), response.getShowGiverNameTo());
        assertEquals(Arrays.asList(ViewerType.RECEIVER), updatedQuestion.getShowGiverNameTo());

        assertEquals(Arrays.asList(FeedbackVisibilityType.RECIPIENT), response.getShowRecipientNameTo());
        assertEquals(Arrays.asList(ViewerType.RECEIVER), updatedQuestion.getShowRecipientNameTo());
    }

    @Test
    void testExecute_invalidQuestionNumber_throwsInvalidHttpRequestBodyException() {
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);
        FeedbackQuestionUpdateRequest updateRequest = getTypicalTextQuestionUpdateRequest();
        updateRequest.setQuestionNumber(-1);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        verifyHttpRequestBodyFailure(updateRequest, params);
    }

    @Test
    void testExecute_invalidGiverRecipientType_throwsInvalidHttpRequestBodyException() throws Exception {
        FeedbackContributionQuestionDetails questionDetails = new FeedbackContributionQuestionDetails();
        FeedbackQuestion contributionQuestion = FeedbackQuestion.makeQuestion(2,
                "contribution question", QuestionGiverType.STUDENTS,
                QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 4,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), questionDetails);
        typicalFeedbackSession.addFeedbackQuestion(contributionQuestion);

        when(mockLogic.getFeedbackQuestion(contributionQuestion.getId())).thenReturn(contributionQuestion);

        FeedbackQuestionUpdateRequest updateRequest = getTypicalTextQuestionUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.CONTRIB);
        updateRequest.setGiverType(QuestionGiverType.INSTRUCTORS);
        updateRequest.setRecipientType(QuestionRecipientType.INSTRUCTORS);

        when(mockLogic.updateFeedbackQuestionCascade(any(UUID.class), any(FeedbackQuestionUpdateRequest.class)))
                .thenThrow(InvalidParametersException.class);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, contributionQuestion.getId().toString(),
        };

        verifyHttpRequestBodyFailure(updateRequest, params);
    }

    @Test
    void testExecute_missingParameters_throwsInvalidHttpParameterException() {
        String[] params = {Const.ParamsNames.FEEDBACK_QUESTION_ID, null,
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testSpecificAccessControl_nonExistentFeedbackQuestion_cannotAccess() {
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(null);
        String[] submissionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        verifyCannotAccess(submissionParams);
    }

    @Test
    void testSpecificAccessControl_withModifySessionPrivilege_canAccess() {
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getFeedbackSession(typicalFeedbackQuestion.getFeedbackSession().getName(),
                typicalFeedbackQuestion.getCourseId())).thenReturn(typicalFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        String[] submissionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        loginAsInstructor(typicalInstructor.getGoogleId());
        verifyCanAccess(submissionParams);
    }

    @Test
    void testSpecificAccessControl_withoutModifySessionPrivilege_cannotAccess() {
        // create instructor without modify session privilege
        Instructor instructorWithoutAccess = getTypicalInstructor();
        instructorWithoutAccess.setPrivileges(new InstructorPrivileges(INSTRUCTOR_PERMISSION_ROLE_OBSERVER));

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getFeedbackSession(typicalFeedbackQuestion.getFeedbackSession().getName(),
                typicalFeedbackQuestion.getCourseId())).thenReturn(typicalFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(instructorWithoutAccess);

        String[] submissionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        loginAsInstructor(instructorWithoutAccess.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    private FeedbackQuestionUpdateRequest getTypicalTextQuestionUpdateRequest() {
        FeedbackQuestionUpdateRequest updateRequest = new FeedbackQuestionUpdateRequest();
        updateRequest.setQuestionNumber(2);
        updateRequest.setQuestionBrief("this is the brief");
        updateRequest.setQuestionDescription("this is the description");
        FeedbackTextQuestionDetails textQuestionDetails = new FeedbackTextQuestionDetails();
        textQuestionDetails.setRecommendedLength(800);
        updateRequest.setQuestionDetails(textQuestionDetails);
        updateRequest.setQuestionType(FeedbackQuestionType.TEXT);
        updateRequest.setGiverType(QuestionGiverType.STUDENTS);
        updateRequest.setRecipientType(QuestionRecipientType.INSTRUCTORS);
        updateRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM);
        updateRequest.setCustomNumberOfEntitiesToGiveFeedbackTo(2);

        updateRequest.setShowResponsesTo(new ArrayList<>());
        updateRequest.setShowGiverNameTo(new ArrayList<>());
        updateRequest.setShowRecipientNameTo(new ArrayList<>());

        return updateRequest;
    }

    private FeedbackQuestion getUpdatedFeedbackQuestion() {
        FeedbackTextQuestionDetails textQuestionDetails =
                new FeedbackTextQuestionDetails("this is the brief");
        textQuestionDetails.setRecommendedLength(800);

        FeedbackQuestion updatedQuestion = FeedbackQuestion.makeQuestion(2,
                "this is the description", QuestionGiverType.STUDENTS,
                QuestionRecipientType.INSTRUCTORS, 2, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(),
                textQuestionDetails);
        typicalFeedbackSession.addFeedbackQuestion(updatedQuestion);

        updatedQuestion.setId(typicalFeedbackQuestion.getId());
        return updatedQuestion;
    }

}
