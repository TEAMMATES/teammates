package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.output.ResponseInstructorCommentData;
import teammates.ui.request.ResponseInstructorCommentCreateRequest;

/**
 * SUT: {@link CreateResponseInstructorCommentAction}.
 */
public class CreateResponseInstructorCommentActionTest extends BaseActionTest<CreateResponseInstructorCommentAction> {

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    private Course typicalCourse;
    private Instructor typicalInstructor;
    private Student typicalStudent;
    private FeedbackSession typicalFeedbackSession;
    private FeedbackResponse typicalFeedbackResponse;
    private ResponseInstructorCommentCreateRequest typicalRequestBody;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        mockHibernateUtil.when(HibernateUtil::flushSession).thenAnswer(Answers.RETURNS_DEFAULTS);

        typicalCourse = getTypicalCourse();
        typicalInstructor = getTypicalInstructor();
        typicalStudent = getTypicalStudent();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
        FeedbackQuestion typicalFeedbackQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(typicalFeedbackQuestion);
    }

    @AfterMethod
    public void tearDown() {
        mockHibernateUtil.close();
    }

    @Test
    void testExecute_emptyHttpParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_nullComment_throwsInvalidHttpRequestBodyException() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        typicalRequestBody = new ResponseInstructorCommentCreateRequest(
                null,
                Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS, CommentVisibilityType.GIVER));

        verifyHttpRequestBodyFailure(typicalRequestBody, params);
    }

    @Test
    void testExecute_emptyComment_throwsInvalidHttpRequestBodyException() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        typicalRequestBody = new ResponseInstructorCommentCreateRequest(
                "",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS, CommentVisibilityType.GIVER));

        verifyHttpRequestBodyFailure(typicalRequestBody, params);
    }

    @Test
    void testExecute_unpublishedSessionForInstructorResult_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        typicalRequestBody = new ResponseInstructorCommentCreateRequest(
                "Comment to first response",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS, CommentVisibilityType.GIVER));

        ResponseInstructorComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        mockCreateResponseInstructorComment(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateResponseInstructorCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData commentData = (ResponseInstructorCommentData) r.getOutput();

        assertEquals("Comment to first response", commentData.getCommentText());
    }

    @Test
    void testExecute_unpublishedSessionEmptyGiverPermission_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        typicalRequestBody = new ResponseInstructorCommentCreateRequest(
                "Empty giver permissions",
                new ArrayList<>(),
                new ArrayList<>());

        ResponseInstructorComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        mockCreateResponseInstructorComment(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateResponseInstructorCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData commentData = (ResponseInstructorCommentData) r.getOutput();

        assertEquals("Empty giver permissions", commentData.getCommentText());
    }

    @Test
    void testExecute_unpublishedSessionCommentShownToGiver_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        typicalRequestBody = new ResponseInstructorCommentCreateRequest(
                "Comment shown to giver",
                Arrays.asList(CommentVisibilityType.GIVER),
                new ArrayList<>());

        ResponseInstructorComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        mockCreateResponseInstructorComment(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateResponseInstructorCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData commentData = (ResponseInstructorCommentData) r.getOutput();

        assertEquals("Comment shown to giver", commentData.getCommentText());
    }

    @Test
    void testExecute_unpublishedSessionCommentShownToRecipient_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        typicalRequestBody = new ResponseInstructorCommentCreateRequest(
                "Comment shown to recipient",
                Arrays.asList(CommentVisibilityType.RECIPIENT),
                new ArrayList<>());

        ResponseInstructorComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        mockCreateResponseInstructorComment(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateResponseInstructorCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData commentData = (ResponseInstructorCommentData) r.getOutput();

        assertEquals("Comment shown to recipient", commentData.getCommentText());
    }

    @Test
    void testExecute_unpublishedSessionCommentShownToGiverTeam_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        typicalRequestBody = new ResponseInstructorCommentCreateRequest(
                "Comment shown to giver team",
                Arrays.asList(CommentVisibilityType.GIVER_TEAM_MEMBERS),
                new ArrayList<>());

        ResponseInstructorComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        mockCreateResponseInstructorComment(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateResponseInstructorCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData commentData = (ResponseInstructorCommentData) r.getOutput();

        assertEquals("Comment shown to giver team", commentData.getCommentText());
    }

    @Test
    void testExecute_unpublishedSessionCommentShownToRecipientTeam_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        typicalRequestBody = new ResponseInstructorCommentCreateRequest(
                "Comment shown to recipient team",
                Arrays.asList(CommentVisibilityType.RECIPIENT_TEAM_MEMBERS),
                new ArrayList<>());

        ResponseInstructorComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        mockCreateResponseInstructorComment(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateResponseInstructorCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData commentData = (ResponseInstructorCommentData) r.getOutput();

        assertEquals("Comment shown to recipient team", commentData.getCommentText());
    }

    @Test
    void testExecute_unpublishedSessionCommentShownToStudents_success() throws Exception {
        assertFalse(typicalFeedbackSession.isPublished());

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        typicalRequestBody = new ResponseInstructorCommentCreateRequest(
                "Comment shown to students",
                Arrays.asList(CommentVisibilityType.STUDENTS),
                new ArrayList<>());

        ResponseInstructorComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        mockCreateResponseInstructorComment(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateResponseInstructorCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData commentData = (ResponseInstructorCommentData) r.getOutput();

        assertEquals("Comment shown to students", commentData.getCommentText());
    }

    @Test
    void testExecute_publishedSessionForInstructorResult_success() throws Exception {
        typicalFeedbackSession.setResultsVisibleFromTime(Instant.now());
        assertTrue(typicalFeedbackSession.isPublished());

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        typicalRequestBody = new ResponseInstructorCommentCreateRequest(
                "Comment to first response, published session",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS, CommentVisibilityType.GIVER));

        ResponseInstructorComment typicalComment = getTypicalCommentForInstructorResult();

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        mockCreateResponseInstructorComment(typicalComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        CreateResponseInstructorCommentAction action = getAction(typicalRequestBody, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData commentData = (ResponseInstructorCommentData) r.getOutput();

        assertEquals("Comment to first response, published session", commentData.getCommentText());
    }

    @Test
    void testAccessControl_typicalCaseForInstructorResult_canAccess() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        loginAsInstructor(typicalInstructor.getGoogleId());

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_contributionQuestionResponse_instructorNotAllowedToAddComment() {
        FeedbackQuestion contributionQuestion = FeedbackQuestion.makeQuestion(
                2,
                "contribution question",
                QuestionGiverType.SESSION_CREATOR,
                QuestionRecipientType.TEAMS,
                2,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new FeedbackContributionQuestionDetails("test contribution question"));
        typicalFeedbackSession.addFeedbackQuestion(contributionQuestion);

        FeedbackResponse contributionResponse = FeedbackResponse.makeResponse(
                new ResponseGiver(typicalStudent),
                new ResponseRecipient(typicalStudent),
                new FeedbackContributionResponseDetails());
        contributionQuestion.addFeedbackResponse(contributionResponse);

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                contributionResponse.getId().toString(),
        };

        when(mockLogic.getFeedbackResponse(contributionResponse.getId())).thenReturn(contributionResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyHttpParameterFailureAcl(params);
    }

    @Test
    void testAccessControl_submitCommentForOthersResponse_cannotAccess() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        loginAsStudent(typicalStudent.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorWithoutSubmitSessionInSectionsPrivilege_cannotAccess() {
        Instructor instructorWithoutAccess = getTypicalInstructor();
        instructorWithoutAccess.setPrivileges(new InstructorPrivileges(INSTRUCTOR_PERMISSION_ROLE_OBSERVER));

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);

        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutAccess.getGoogleId()))
                .thenReturn(instructorWithoutAccess);

        loginAsInstructor(instructorWithoutAccess.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorWithOnlyEitherPrivilege_cannotAccessCrossSectionComment() {
        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setEmail("instructorWithPrivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section B",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithoutPrivilege.setPrivileges(privileges);

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivilege.getGoogleId()))
                .thenReturn(instructorWithoutPrivilege);

        loginAsInstructor(instructorWithoutPrivilege.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_logOut_cannotAccess() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);

        logoutUser();

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_studentAccessInstructorResponse_cannotAccess() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);

        loginAsStudent(typicalStudent.getGoogleId());

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorInSameCourse_canAccess() {
        Instructor instructorInSameCourse = getTypicalInstructor();
        instructorInSameCourse.setEmail("instructor2@teammates.tmt");

        loginAsInstructor(instructorInSameCourse.getGoogleId());

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorInSameCourse.getGoogleId()))
                .thenReturn(instructorInSameCourse);

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_adminToMasqueradeAsInstructor_canAccess() {
        when(mockLogic.getFeedbackResponse(typicalFeedbackResponse.getId())).thenReturn(typicalFeedbackResponse);
        when(mockLogic.getInstructorByGoogleId(any(String.class), any(String.class)))
                .thenReturn(typicalInstructor);

        loginAsAdmin();

        String[] params = new String[] { Const.ParamsNames.FEEDBACK_RESPONSE_ID,
                typicalFeedbackResponse.getId().toString(),
        };

        verifyCanMasquerade(typicalInstructor.getAccountId(), params);
    }

    private void mockCreateResponseInstructorComment(ResponseInstructorComment comment) throws Exception {
        when(mockLogic.createResponseInstructorComment(
                any(UUID.class), any(Instructor.class), any(String.class), any(), any()))
                .thenReturn(comment);
    }

    private ResponseInstructorComment getTypicalCommentForInstructorResult() {
        ResponseInstructorComment responseInstructorComment = new ResponseInstructorComment(
                typicalInstructor,
                typicalRequestBody.getCommentText(),
                typicalRequestBody.getShowCommentTo(),
                typicalRequestBody.getShowGiverNameTo(),
                typicalInstructor);
        typicalFeedbackResponse.addResponseInstructorComment(responseInstructorComment);
        responseInstructorComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000001"));
        responseInstructorComment.setCreatedAt(Instant.EPOCH);
        responseInstructorComment.setUpdatedAt(Instant.EPOCH);
        return responseInstructorComment;
    }

}
