package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM;

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
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;

/**
 * SUT: {@link UpdateFeedbackResponseCommentAction}.
 */
public class UpdateFeedbackResponseCommentActionTest extends BaseActionTest<UpdateFeedbackResponseCommentAction> {

    private Course typicalCourse;
    private Instructor typicalInstructor;
    private Student typicalStudent;
    private FeedbackSession typicalFeedbackSession;
    private FeedbackQuestion typicalFeedbackQuestion;
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
        typicalStudent = getTypicalStudent();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
        typicalFeedbackSession.setStartTime(Instant.now().minusSeconds(100));
        typicalFeedbackSession.setEndTime(Instant.now());
        typicalFeedbackSession.setSessionVisibleFromTime(Instant.now());
        typicalFeedbackQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(typicalFeedbackQuestion);
    }

    @Test
    void testExecute_emptyHttpParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_typicalCaseInstructorResult_success() throws Exception {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();
        FeedbackResponseComment updatedComment = getUpdatedCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = getTypicalRequestBody();

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateFeedbackResponseComment(any(UUID.class), any(FeedbackResponseCommentUpdateRequest.class),
                any(ResponseGiver.class))).thenReturn(updatedComment);

        UpdateFeedbackResponseCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData response = (FeedbackResponseCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_emptyVisibilitySettings_success() throws Exception {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();
        FeedbackResponseComment updatedComment = getUpdatedCommentFromInstructor();
        updatedComment.setShowCommentTo(new ArrayList<>());
        updatedComment.setShowGiverNameTo(new ArrayList<>());

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = new FeedbackResponseCommentUpdateRequest(
                "updated comment", new ArrayList<>(), new ArrayList<>());

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateFeedbackResponseComment(any(UUID.class), any(FeedbackResponseCommentUpdateRequest.class),
                any(ResponseGiver.class))).thenReturn(updatedComment);

        UpdateFeedbackResponseCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData response = (FeedbackResponseCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_differentVisibilitySetting_success() throws Exception {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();
        FeedbackResponseComment updatedComment = getUpdatedCommentFromInstructor();
        updatedComment.setShowCommentTo(Arrays.asList(ViewerType.STUDENTS));
        updatedComment.setShowGiverNameTo(new ArrayList<>());

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = new FeedbackResponseCommentUpdateRequest(
                "updated comment", Arrays.asList(CommentVisibilityType.STUDENTS), new ArrayList<>());

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateFeedbackResponseComment(any(UUID.class), any(FeedbackResponseCommentUpdateRequest.class),
                any(ResponseGiver.class))).thenReturn(updatedComment);

        UpdateFeedbackResponseCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData response = (FeedbackResponseCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_nonExistentFeedbackResponseComment_throwsEntityNotFoundException() {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "00000000-0000-4000-8000-000000009999",
        };

        FeedbackResponseCommentUpdateRequest updateRequest = getTypicalRequestBody();

        verifyEntityNotFound(updateRequest, params);
    }

    @Test
    void testExecute_instructorNotCommentGiver_success() throws Exception {
        Instructor differentInstructor = getTypicalInstructor();
        differentInstructor.setName("different instructor");
        differentInstructor.setEmail("differentinstructor@teammates.tmt");
        differentInstructor.setGoogleId("different google id");

        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();
        FeedbackResponseComment updatedComment = getUpdatedCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = getTypicalRequestBody();

        loginAsInstructor(differentInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), differentInstructor.getGoogleId()))
                .thenReturn(differentInstructor);
        when(mockLogic.updateFeedbackResponseComment(any(UUID.class), any(FeedbackResponseCommentUpdateRequest.class),
                any(ResponseGiver.class))).thenReturn(updatedComment);

        UpdateFeedbackResponseCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData response = (FeedbackResponseCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_typicalCasePublishedSession_success() throws Exception {
        typicalFeedbackSession.setResultsVisibleFromTime(Instant.now());
        assertTrue(typicalFeedbackSession.isPublished());

        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();
        FeedbackResponseComment updatedComment = getUpdatedCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = getTypicalRequestBody();

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateFeedbackResponseComment(any(UUID.class), any(FeedbackResponseCommentUpdateRequest.class),
                any(ResponseGiver.class))).thenReturn(updatedComment);

        UpdateFeedbackResponseCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData response = (FeedbackResponseCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_emptyCommentText_throwsInvalidHttpRequestBodyException() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = new FeedbackResponseCommentUpdateRequest(
                "", new ArrayList<>(), new ArrayList<>());

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        verifyHttpRequestBodyFailure(updateRequest, params);
    }

    @Test
    void testAccessControl_instructorWithPrivileges_canAccess() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();

        Instructor instructorWithPrivileges = getTypicalInstructor();
        instructorWithPrivileges.setEmail("helper@teammates.tmt");
        instructorWithPrivileges.setPrivileges(new InstructorPrivileges(INSTRUCTOR_PERMISSION_ROLE_COOWNER));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithPrivileges.getGoogleId()))
                .thenReturn(instructorWithPrivileges);

        loginAsInstructor(instructorWithPrivileges.getGoogleId());
        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_instructorWithoutPrivileges_cannotAccess() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();

        Instructor instructorWithoutPrivileges = getTypicalInstructor();
        instructorWithoutPrivileges.setEmail("helper@teammates.tmt");
        instructorWithoutPrivileges.setPrivileges(new InstructorPrivileges(INSTRUCTOR_PERMISSION_ROLE_CUSTOM));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivileges.getGoogleId()))
                .thenReturn(instructorWithoutPrivileges);

        loginAsInstructor(instructorWithoutPrivileges.getGoogleId());
        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_adminToMasqueradeAsInstructor_canAccess() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(any(String.class), any(String.class)))
                .thenReturn(typicalInstructor);

        loginAsAdmin();
        verifyCanMasquerade(typicalInstructor.getGoogleId(), params);
    }

    @Test
    void testAccessControl_nonExistentFeedbackResponseComment_throwsEntityNotFoundException() {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "00000000-0000-4000-8000-000000009999",
        };

        verifyEntityNotFoundAcl(params);
    }

    @Test
    void testAccessControl_instructorWithCorrectPrivilege_canAccessCrossSectionComment() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromTeam(typicalStudent.getTeam());

        Instructor instructorWithPrivilege = getTypicalInstructor();
        instructorWithPrivilege.setEmail("instructorwithprivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section A",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        privileges.updatePrivilege("Section B",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithPrivilege.getGoogleId()))
                .thenReturn(instructorWithPrivilege);

        loginAsInstructor(instructorWithPrivilege.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_instructorWithoutGiverSectionPrivilege_cannotAccessCrossSectionComment() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromTeam(typicalStudent.getTeam());

        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setEmail("instructorwithprivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section B",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithoutPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivilege.getGoogleId()))
                .thenReturn(instructorWithoutPrivilege);

        loginAsInstructor(instructorWithoutPrivilege.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorWithoutRecipientSectionPrivilege_cannotAccessCrossSectionComment() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromTeam(typicalStudent.getTeam());

        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setEmail("instructorwithprivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section A",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithoutPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivilege.getGoogleId()))
                .thenReturn(instructorWithoutPrivilege);

        loginAsInstructor(instructorWithoutPrivilege.getGoogleId());

        verifyCannotAccess(params);
    }

    private FeedbackResponseComment getTypicalCommentFromInstructor() {
        ResponseGiver giver = new ResponseGiver(typicalInstructor);
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                giver,
                "typical comment",
                false,
                false,
                Arrays.asList(ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.INSTRUCTORS),
                giver);
        typicalFeedbackResponse.addFeedbackResponseComment(feedbackResponseComment);
        feedbackResponseComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000002"));
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getUpdatedCommentFromInstructor() {
        ResponseGiver giver = new ResponseGiver(typicalInstructor);
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                giver,
                "updated comment",
                false,
                false,
                Arrays.asList(ViewerType.GIVER, ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.GIVER, ViewerType.INSTRUCTORS),
                giver);
        typicalFeedbackResponse.addFeedbackResponseComment(feedbackResponseComment);
        feedbackResponseComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000002"));
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getTypicalCommentFromTeam(Team team) {
        Section sectionA = new Section("Section A");
        typicalCourse.addSection(sectionA);
        Section sectionB = new Section("Section B");
        typicalCourse.addSection(sectionB);
        Team giverTeam = new Team("Section A");
        giverTeam.setSection(sectionA);
        Team recipientTeam = new Team("Section B");
        recipientTeam.setSection(sectionB);
        typicalFeedbackResponse = FeedbackResponse.makeResponse(
                new ResponseGiver(giverTeam), new ResponseRecipient(recipientTeam), getTypicalFeedbackResponseDetails());
        typicalFeedbackQuestion.addFeedbackResponse(typicalFeedbackResponse);
        ResponseGiver giver = new ResponseGiver(team);
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                giver,
                "typical comment",
                false,
                false,
                Arrays.asList(ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.INSTRUCTORS),
                giver);
        typicalFeedbackResponse.addFeedbackResponseComment(feedbackResponseComment);
        feedbackResponseComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000004"));
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseCommentUpdateRequest getTypicalRequestBody() {
        return new FeedbackResponseCommentUpdateRequest(
                "updated comment",
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS));
    }

    private void checkJsonResponse(FeedbackResponseComment updatedComment, FeedbackResponseCommentData response) {
        assertEquals(updatedComment.getCommentText(), response.getCommentText());
        assertEquals(updatedComment.getShowCommentTo().toString(), response.getShowCommentTo().toString());
        assertEquals(updatedComment.getShowGiverNameTo().toString(), response.getShowGiverNameTo().toString());
    }

}
