package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.InstructorPermissionRoleNames.COOWNER;
import static teammates.common.util.Const.InstructorPermissionRoleNames.CUSTOM;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.output.ResponseInstructorCommentData;
import teammates.ui.request.ResponseInstructorCommentUpdateRequest;

/**
 * SUT: {@link UpdateResponseInstructorCommentAction}.
 */
public class UpdateResponseInstructorCommentActionTest extends BaseActionTest<UpdateResponseInstructorCommentAction> {

    private Course typicalCourse;
    private Instructor typicalInstructor;
    private FeedbackSession typicalFeedbackSession;
    private FeedbackResponse typicalFeedbackResponse;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUpMethod() {
        typicalCourse = getTypicalCourse();
        typicalInstructor = getTypicalInstructor();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
        typicalFeedbackSession.setStartTime(Instant.now().minusSeconds(100));
        typicalFeedbackSession.setEndTime(Instant.now());
        typicalFeedbackSession.setSessionVisibleFromTime(Instant.now());
        FeedbackQuestion typicalFeedbackQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(typicalFeedbackQuestion);
    }

    @Test
    void testExecute_emptyHttpParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_typicalCaseInstructorResult_success() throws Exception {
        ResponseInstructorComment typicalComment = getTypicalCommentFromInstructor();
        ResponseInstructorComment updatedComment = getUpdatedCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        ResponseInstructorCommentUpdateRequest updateRequest = getTypicalRequestBody();

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getResponseInstructorComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateResponseInstructorComment(any(UUID.class), any(ResponseInstructorCommentUpdateRequest.class),
                any(Instructor.class))).thenReturn(updatedComment);

        UpdateResponseInstructorCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData response = (ResponseInstructorCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_emptyVisibilitySettings_success() throws Exception {
        ResponseInstructorComment typicalComment = getTypicalCommentFromInstructor();
        ResponseInstructorComment updatedComment = getUpdatedCommentFromInstructor();
        updatedComment.setShowCommentTo(new ArrayList<>());
        updatedComment.setShowGiverNameTo(new ArrayList<>());

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        ResponseInstructorCommentUpdateRequest updateRequest = new ResponseInstructorCommentUpdateRequest(
                "updated comment", new ArrayList<>(), new ArrayList<>());

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getResponseInstructorComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateResponseInstructorComment(any(UUID.class), any(ResponseInstructorCommentUpdateRequest.class),
                any(Instructor.class))).thenReturn(updatedComment);

        UpdateResponseInstructorCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData response = (ResponseInstructorCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_differentVisibilitySetting_success() throws Exception {
        ResponseInstructorComment typicalComment = getTypicalCommentFromInstructor();
        ResponseInstructorComment updatedComment = getUpdatedCommentFromInstructor();
        updatedComment.setShowCommentTo(Arrays.asList(ViewerType.STUDENTS));
        updatedComment.setShowGiverNameTo(new ArrayList<>());

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        ResponseInstructorCommentUpdateRequest updateRequest = new ResponseInstructorCommentUpdateRequest(
                "updated comment", Arrays.asList(CommentVisibilityType.STUDENTS), new ArrayList<>());

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getResponseInstructorComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateResponseInstructorComment(any(UUID.class), any(ResponseInstructorCommentUpdateRequest.class),
                any(Instructor.class))).thenReturn(updatedComment);

        UpdateResponseInstructorCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData response = (ResponseInstructorCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_nonExistentResponseInstructorComment_throwsEntityNotFoundException() {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "00000000-0000-4000-8000-000000009999",
        };

        ResponseInstructorCommentUpdateRequest updateRequest = getTypicalRequestBody();

        verifyEntityNotFound(updateRequest, params);
    }

    @Test
    void testExecute_instructorNotCommentGiver_success() throws Exception {
        Instructor differentInstructor = getTypicalInstructor();
        differentInstructor.setName("different instructor");
        differentInstructor.setEmail("differentinstructor@teammates.tmt");
        differentInstructor.setGoogleId("different google id");

        ResponseInstructorComment typicalComment = getTypicalCommentFromInstructor();
        ResponseInstructorComment updatedComment = getUpdatedCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        ResponseInstructorCommentUpdateRequest updateRequest = getTypicalRequestBody();

        loginAsInstructor(differentInstructor.getGoogleId());

        when(mockLogic.getResponseInstructorComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), differentInstructor.getGoogleId()))
                .thenReturn(differentInstructor);
        when(mockLogic.updateResponseInstructorComment(any(UUID.class), any(ResponseInstructorCommentUpdateRequest.class),
                any(Instructor.class))).thenReturn(updatedComment);

        UpdateResponseInstructorCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData response = (ResponseInstructorCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_typicalCasePublishedSession_success() throws Exception {
        typicalFeedbackSession.setResultsVisibleFromTime(Instant.now());
        assertTrue(typicalFeedbackSession.isPublished());

        ResponseInstructorComment typicalComment = getTypicalCommentFromInstructor();
        ResponseInstructorComment updatedComment = getUpdatedCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        ResponseInstructorCommentUpdateRequest updateRequest = getTypicalRequestBody();

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getResponseInstructorComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateResponseInstructorComment(any(UUID.class), any(ResponseInstructorCommentUpdateRequest.class),
                any(Instructor.class))).thenReturn(updatedComment);

        UpdateResponseInstructorCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        ResponseInstructorCommentData response = (ResponseInstructorCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_emptyCommentText_throwsInvalidHttpRequestBodyException() {
        ResponseInstructorComment typicalComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        ResponseInstructorCommentUpdateRequest updateRequest = new ResponseInstructorCommentUpdateRequest(
                "", new ArrayList<>(), new ArrayList<>());

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getResponseInstructorComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        verifyHttpRequestBodyFailure(updateRequest, params);
    }

    @Test
    void testAccessControl_instructorWithPrivileges_canAccess() {
        ResponseInstructorComment typicalComment = getTypicalCommentFromInstructor();

        Instructor instructorWithPrivileges = getTypicalInstructor();
        instructorWithPrivileges.setEmail("helper@teammates.tmt");
        instructorWithPrivileges.setPrivileges(new InstructorPrivileges(COOWNER));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithPrivileges.getGoogleId()))
                .thenReturn(instructorWithPrivileges);

        loginAsInstructor(instructorWithPrivileges.getGoogleId());
        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_instructorWithoutPrivileges_cannotAccess() {
        ResponseInstructorComment typicalComment = getTypicalCommentFromInstructor();

        Instructor instructorWithoutPrivileges = getTypicalInstructor();
        instructorWithoutPrivileges.setEmail("helper@teammates.tmt");
        instructorWithoutPrivileges.setPrivileges(new InstructorPrivileges(CUSTOM));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivileges.getGoogleId()))
                .thenReturn(instructorWithoutPrivileges);

        loginAsInstructor(instructorWithoutPrivileges.getGoogleId());
        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_adminToMasqueradeAsInstructor_canAccess() {
        ResponseInstructorComment typicalComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(any(String.class), any(String.class)))
                .thenReturn(typicalInstructor);

        loginAsAdmin();
        verifyCanMasquerade(typicalInstructor.getAccountId(), params);
    }

    @Test
    void testAccessControl_nonExistentResponseInstructorComment_throwsEntityNotFoundException() {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "00000000-0000-4000-8000-000000009999",
        };

        verifyEntityNotFoundAcl(params);
    }

    private ResponseInstructorComment getTypicalCommentFromInstructor() {
        ResponseInstructorComment responseInstructorComment = new ResponseInstructorComment(
                typicalInstructor,
                "typical comment",
                Arrays.asList(ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.INSTRUCTORS),
                typicalInstructor);
        typicalFeedbackResponse.addResponseInstructorComment(responseInstructorComment);
        responseInstructorComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000002"));
        responseInstructorComment.setCreatedAt(Instant.EPOCH);
        responseInstructorComment.setUpdatedAt(Instant.EPOCH);
        return responseInstructorComment;
    }

    private ResponseInstructorComment getUpdatedCommentFromInstructor() {
        ResponseInstructorComment responseInstructorComment = new ResponseInstructorComment(
                typicalInstructor,
                "updated comment",
                Arrays.asList(ViewerType.GIVER, ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.GIVER, ViewerType.INSTRUCTORS),
                typicalInstructor);
        typicalFeedbackResponse.addResponseInstructorComment(responseInstructorComment);
        responseInstructorComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000002"));
        responseInstructorComment.setCreatedAt(Instant.EPOCH);
        responseInstructorComment.setUpdatedAt(Instant.EPOCH);
        return responseInstructorComment;
    }

    private ResponseInstructorCommentUpdateRequest getTypicalRequestBody() {
        return new ResponseInstructorCommentUpdateRequest(
                "updated comment",
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS));
    }

    private void checkJsonResponse(ResponseInstructorComment updatedComment, ResponseInstructorCommentData response) {
        assertEquals(updatedComment.getCommentText(), response.getCommentText());
        assertEquals(updatedComment.getShowCommentTo().toString(), response.getShowCommentTo().toString());
        assertEquals(updatedComment.getShowGiverNameTo().toString(), response.getShowGiverNameTo().toString());
    }

}
