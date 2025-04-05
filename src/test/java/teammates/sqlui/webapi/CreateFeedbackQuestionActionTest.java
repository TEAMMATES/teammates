package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.request.FeedbackQuestionCreateRequest;
import teammates.ui.webapi.CreateFeedbackQuestionAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateFeedbackQuestionAction}.
 */
public class CreateFeedbackQuestionActionTest extends BaseActionTest<CreateFeedbackQuestionAction> {

    private Instructor typicalInstructor;
    private Course typicalCourse;
    private FeedbackSession typicalFeedbackSession;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.QUESTION;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        typicalInstructor = getTypicalInstructor();
        typicalCourse = typicalInstructor.getCourse();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
    }

    @Test
    void testExecute_typicalCase_success() throws Exception {
        FeedbackQuestion createdQuestion = getCreatedFeedbackQuestion();

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.createFeedbackQuestion(any(FeedbackQuestion.class))).thenReturn(createdQuestion);

        String[] params = {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        FeedbackQuestionCreateRequest createRequest = getTypicalTextQuestionCreateRequest();
        CreateFeedbackQuestionAction a = getAction(createRequest, params);
        JsonResult r = getJsonResult(a);

        FeedbackQuestionData response = (FeedbackQuestionData) r.getOutput();

        assertEquals(response.getQuestionNumber(), createdQuestion.getQuestionNumber().intValue());
        assertEquals(response.getQuestionNumber(), 2);

        assertEquals(response.getQuestionDescription(), "this is the description");
        assertEquals(response.getQuestionDescription(), createdQuestion.getDescription());

        assertEquals(response.getQuestionBrief(), "this is the brief");
        assertEquals(response.getQuestionBrief(), createdQuestion.getQuestionDetailsCopy().getQuestionText());

        assertEquals(response.getQuestionType(), FeedbackQuestionType.TEXT);
        assertEquals(response.getQuestionType(), createdQuestion.getQuestionDetailsCopy().getQuestionType());

        assertEquals(response.getGiverType(), FeedbackParticipantType.STUDENTS);
        assertEquals(response.getGiverType(), createdQuestion.getGiverType());

        assertEquals(response.getRecipientType(), FeedbackParticipantType.INSTRUCTORS);
        assertEquals(response.getRecipientType(), createdQuestion.getRecipientType());

        assertEquals(response.getNumberOfEntitiesToGiveFeedbackToSetting(),
                NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED);

        assertTrue(response.getShowResponsesTo().isEmpty());
        assertTrue(createdQuestion.getShowResponsesTo().isEmpty());

        assertTrue(response.getShowGiverNameTo().isEmpty());
        assertTrue(createdQuestion.getShowGiverNameTo().isEmpty());

        assertTrue(response.getShowRecipientNameTo().isEmpty());
        assertTrue(createdQuestion.getShowRecipientNameTo().isEmpty());
    }

    @Test
    void testExecute_missingParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingCourseId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingFeedbackSessionName_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_nullQuestionType_throwsInvalidHttpRequestBodyException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        FeedbackQuestionCreateRequest createRequest = getTypicalTextQuestionCreateRequest();
        createRequest.setQuestionType(null);
        verifyHttpRequestBodyFailure(createRequest, params);
    }

    @Test
    void testExecute_invalidQuestionNumber_throwsInvalidHttpRequestBodyException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        FeedbackQuestionCreateRequest createRequest = getTypicalTextQuestionCreateRequest();
        createRequest.setQuestionNumber(-1);

        verifyHttpRequestBodyFailure(createRequest, params);
    }

    @Test
    void testExecute_invalidGiverType_throwsInvalidHttpRequestBodyException() {
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);

        String[] params = {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        FeedbackQuestionCreateRequest createRequest = getTypicalTextQuestionCreateRequest();
        createRequest.setQuestionType(FeedbackQuestionType.CONTRIB);
        createRequest.setQuestionDetails(new FeedbackContributionQuestionDetails());
        createRequest.setGiverType(FeedbackParticipantType.NONE);

        verifyHttpRequestBodyFailure(createRequest, params);
    }

    @Test
    void testExecute_invalidRecommendedLength_throwsInvalidHttpRequestBodyException() {
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);

        String[] params = {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        FeedbackQuestionCreateRequest createRequest = getTypicalTextQuestionCreateRequest();
        FeedbackTextQuestionDetails textQuestionDetails = new FeedbackTextQuestionDetails();
        textQuestionDetails.setRecommendedLength(-1);
        createRequest.setQuestionDetails(textQuestionDetails);

        verifyHttpRequestBodyFailure(createRequest, params);
    }

    @Test
    void testExecute_emptyQuestionBrief_throwsInvalidHttpRequestBodyException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        FeedbackQuestionCreateRequest createRequest = getTypicalTextQuestionCreateRequest();
        createRequest.setQuestionBrief("");

        verifyHttpRequestBodyFailure(createRequest, params);
    }

    @Test
    void testExecute_nonExistentFeedbackSession_throwsEntityNotFoundException() {
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId())).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };
        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyEntityNotFoundAcl(params);
    }

    @Test
    void testAccessControl() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);

        verifyInaccessibleWithoutModifySessionPrivilege(typicalCourse, params);
        verifyAccessibleWithModifySessionPrivilege(typicalCourse, params);
    }

    private FeedbackQuestionCreateRequest getTypicalTextQuestionCreateRequest() {
        FeedbackQuestionCreateRequest createRequest = new FeedbackQuestionCreateRequest();
        createRequest.setQuestionNumber(2);
        createRequest.setQuestionBrief("this is the brief");
        createRequest.setQuestionDescription("this is the description");
        FeedbackTextQuestionDetails textQuestionDetails = new FeedbackTextQuestionDetails();
        textQuestionDetails.setRecommendedLength(800);
        createRequest.setQuestionDetails(textQuestionDetails);
        createRequest.setQuestionType(FeedbackQuestionType.TEXT);
        createRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        createRequest.setRecipientType(FeedbackParticipantType.INSTRUCTORS);
        createRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED);

        createRequest.setShowResponsesTo(new ArrayList<>());
        createRequest.setShowGiverNameTo(new ArrayList<>());
        createRequest.setShowRecipientNameTo(new ArrayList<>());

        return createRequest;
    }

    private FeedbackQuestion getCreatedFeedbackQuestion() {
        FeedbackTextQuestionDetails textQuestionDetails =
                new FeedbackTextQuestionDetails("this is the brief");
        textQuestionDetails.setRecommendedLength(800);

        return FeedbackQuestion.makeQuestion(typicalFeedbackSession, 2,
                "this is the description", FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.INSTRUCTORS, Const.MAX_POSSIBLE_RECIPIENTS, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), textQuestionDetails);
    }

}
