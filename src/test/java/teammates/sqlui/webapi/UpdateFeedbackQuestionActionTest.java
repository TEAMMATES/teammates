package teammates.sqlui.webapi;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackVisibilityType;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.request.FeedbackQuestionUpdateRequest;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.UpdateFeedbackQuestionAction;

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

        assertEquals(updatedQuestion.getQuestionDetailsCopy().getQuestionType(), response.getQuestionType());
        assertEquals(FeedbackQuestionType.TEXT, updatedQuestion.getQuestionDetailsCopy().getQuestionType());

        assertEquals(JsonUtils.toJson(updatedQuestion.getQuestionDetailsCopy()),
                JsonUtils.toJson(response.getQuestionDetails()));
        assertEquals(800, ((FeedbackTextQuestionDetails)
                updatedQuestion.getQuestionDetailsCopy()).getRecommendedLength().intValue());

        assertEquals(updatedQuestion.getGiverType(), response.getGiverType());
        assertEquals(FeedbackParticipantType.STUDENTS, updatedQuestion.getGiverType());

        assertEquals(updatedQuestion.getRecipientType(), response.getRecipientType());
        assertEquals(FeedbackParticipantType.INSTRUCTORS, updatedQuestion.getRecipientType());

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
        updatedQuestion.setGiverType(FeedbackParticipantType.INSTRUCTORS);
        updatedQuestion.setRecipientType(FeedbackParticipantType.TEAMS);
        updatedQuestion.setShowResponsesTo(Arrays.asList(FeedbackParticipantType.RECEIVER));
        updatedQuestion.setShowGiverNameTo(Arrays.asList(FeedbackParticipantType.RECEIVER));
        updatedQuestion.setShowRecipientNameTo(Arrays.asList(FeedbackParticipantType.RECEIVER));

        when(mockLogic.updateFeedbackQuestionCascade(any(UUID.class), any(FeedbackQuestionUpdateRequest.class)))
                .thenReturn(updatedQuestion);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        FeedbackQuestionUpdateRequest updateRequest = getTypicalTextQuestionUpdateRequest();
        updateRequest.setGiverType(FeedbackParticipantType.INSTRUCTORS);
        updateRequest.setRecipientType(FeedbackParticipantType.TEAMS);
        updateRequest.setShowResponsesTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));
        updateRequest.setShowGiverNameTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));
        updateRequest.setShowRecipientNameTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));

        UpdateFeedbackQuestionAction a = getAction(updateRequest, params);
        JsonResult r = getJsonResult(a);
        FeedbackQuestionData response = (FeedbackQuestionData) r.getOutput();

        assertEquals(updatedQuestion.getGiverType(), response.getGiverType());
        assertEquals(FeedbackParticipantType.INSTRUCTORS, updatedQuestion.getGiverType());

        assertEquals(updatedQuestion.getRecipientType(), response.getRecipientType());
        assertEquals(FeedbackParticipantType.TEAMS, updatedQuestion.getRecipientType());

        assertEquals(Arrays.asList(FeedbackVisibilityType.RECIPIENT), response.getShowResponsesTo());
        assertEquals(Arrays.asList(FeedbackParticipantType.RECEIVER), updatedQuestion.getShowResponsesTo());

        assertEquals(Arrays.asList(FeedbackVisibilityType.RECIPIENT), response.getShowGiverNameTo());
        assertEquals(Arrays.asList(FeedbackParticipantType.RECEIVER), updatedQuestion.getShowGiverNameTo());

        assertEquals(Arrays.asList(FeedbackVisibilityType.RECIPIENT), response.getShowRecipientNameTo());
        assertEquals(Arrays.asList(FeedbackParticipantType.RECEIVER), updatedQuestion.getShowRecipientNameTo());
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
        FeedbackQuestion contributionQuestion = FeedbackQuestion.makeQuestion(typicalFeedbackSession, 2,
                "contribution question", FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 4,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), questionDetails);

        when(mockLogic.getFeedbackQuestion(contributionQuestion.getId())).thenReturn(contributionQuestion);

        FeedbackQuestionUpdateRequest updateRequest = getTypicalTextQuestionUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.CONTRIB);
        updateRequest.setGiverType(FeedbackParticipantType.INSTRUCTORS);
        updateRequest.setRecipientType(FeedbackParticipantType.INSTRUCTORS);

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
        updateRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        updateRequest.setRecipientType(FeedbackParticipantType.INSTRUCTORS);
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

        FeedbackQuestion updatedQuestion = FeedbackQuestion.makeQuestion(typicalFeedbackSession, 2,
                "this is the description", FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.INSTRUCTORS, 2, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(),
                textQuestionDetails);
        updatedQuestion.setId(typicalFeedbackQuestion.getId());
        return updatedQuestion;
    }

}
