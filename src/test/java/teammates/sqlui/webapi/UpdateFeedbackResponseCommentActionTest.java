package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;
import teammates.ui.request.Intent;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.UpdateFeedbackResponseCommentAction;

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
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = getTypicalRequestBody();

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateFeedbackResponseComment(any(Long.class), any(FeedbackResponseCommentUpdateRequest.class),
                any(String.class))).thenReturn(updatedComment);

        UpdateFeedbackResponseCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData response = (FeedbackResponseCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_typicalCaseStudentSubmission_success() throws Exception {
        FeedbackResponseComment typicalComment = getTypicalCommentFromStudent();
        FeedbackResponseComment updatedComment = getUpdatedCommentFromStudent();

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = getTypicalRequestBody();

        loginAsStudent(typicalStudent.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);
        when(mockLogic.updateFeedbackResponseComment(any(Long.class), any(FeedbackResponseCommentUpdateRequest.class),
                any(String.class))).thenReturn(updatedComment);

        UpdateFeedbackResponseCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData response = (FeedbackResponseCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_typicalCaseInstructorSubmission_success() throws Exception {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructorAsParticipant();
        FeedbackResponseComment updatedComment = getUpdatedCommentFromInstructorAsParticipant();

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = getTypicalRequestBody();

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateFeedbackResponseComment(any(Long.class), any(FeedbackResponseCommentUpdateRequest.class),
                any(String.class))).thenReturn(updatedComment);

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
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = new FeedbackResponseCommentUpdateRequest(
                "updated comment", new ArrayList<>(), new ArrayList<>());

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateFeedbackResponseComment(any(Long.class), any(FeedbackResponseCommentUpdateRequest.class),
                any(String.class))).thenReturn(updatedComment);

        UpdateFeedbackResponseCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData response = (FeedbackResponseCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_differentVisibilitySetting_success() throws Exception {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();
        FeedbackResponseComment updatedComment = getUpdatedCommentFromInstructor();
        updatedComment.setShowCommentTo(Arrays.asList(FeedbackParticipantType.STUDENTS));
        updatedComment.setShowGiverNameTo(new ArrayList<>());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = new FeedbackResponseCommentUpdateRequest(
                "updated comment", Arrays.asList(CommentVisibilityType.STUDENTS), new ArrayList<>());

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateFeedbackResponseComment(any(Long.class), any(FeedbackResponseCommentUpdateRequest.class),
                any(String.class))).thenReturn(updatedComment);

        UpdateFeedbackResponseCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData response = (FeedbackResponseCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_nonExistentFeedbackResponseComment_throwsEntityNotFoundException() {
        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123",
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
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = getTypicalRequestBody();

        loginAsInstructor(differentInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), differentInstructor.getGoogleId()))
                .thenReturn(differentInstructor);
        when(mockLogic.updateFeedbackResponseComment(any(Long.class), any(FeedbackResponseCommentUpdateRequest.class),
                any(String.class))).thenReturn(updatedComment);

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
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = getTypicalRequestBody();

        loginAsInstructor(typicalInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.updateFeedbackResponseComment(any(Long.class), any(FeedbackResponseCommentUpdateRequest.class),
                any(String.class))).thenReturn(updatedComment);

        UpdateFeedbackResponseCommentAction action = getAction(updateRequest, params);
        JsonResult r = getJsonResult(action);
        FeedbackResponseCommentData response = (FeedbackResponseCommentData) r.getOutput();

        checkJsonResponse(updatedComment, response);
    }

    @Test
    void testExecute_emptyCommentText_throwsInvalidHttpRequestBodyException() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
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
    void testExecute_invalidIntent_throwsInvalidHttpParameterException() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);

        verifyHttpParameterFailure(params);
    }

    @Test
    void testAccessControl_instructorWithPrivileges_canAccess() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();

        Instructor instructorWithPrivileges = getTypicalInstructor();
        instructorWithPrivileges.setEmail("helper@teammates.tmt");
        instructorWithPrivileges.setPrivileges(new InstructorPrivileges(INSTRUCTOR_PERMISSION_ROLE_COOWNER));

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
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
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
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
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(any(String.class), any(String.class)))
                .thenReturn(typicalInstructor);

        loginAsAdmin();
        verifyCanMasquerade(typicalInstructor.getGoogleId(), params);
    }

    @Test
    void testAccessControl_invalidIntent_throwsInvalidHttpParameterException() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);

        verifyHttpParameterFailureAcl(params);
    }

    @Test
    void testAccessControl_differentStudentFromSameCourse_cannotAccessStudentComment() {
        typicalFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();

        Student differentStudentFromSameCourse = getTypicalStudent();
        differentStudentFromSameCourse.setEmail("differentStudent@teammates.tmt");

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), differentStudentFromSameCourse.getGoogleId()))
                .thenReturn(differentStudentFromSameCourse);
        when(mockLogic.getDeadlineForUser(typicalFeedbackSession, differentStudentFromSameCourse))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(15)));

        loginAsStudent(differentStudentFromSameCourse.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_differentStudentFromDifferentTeam_cannotAccessTeamComment() {
        typicalFeedbackQuestion.setGiverType(FeedbackParticipantType.TEAMS);
        FeedbackResponseComment typicalComment = getTypicalCommentFromTeam();

        Student differentStudentFromDifferentTeam = getTypicalStudent();
        differentStudentFromDifferentTeam.setTeam(new Team(
                new Section(typicalCourse, "Section C"),
                "different team"));

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), differentStudentFromDifferentTeam.getGoogleId()))
                .thenReturn(differentStudentFromDifferentTeam);
        when(mockLogic.getDeadlineForUser(typicalFeedbackSession, differentStudentFromDifferentTeam))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(15)));

        loginAsStudent(differentStudentFromDifferentTeam.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_differentStudentFromSameTeam_canAccessTeamComment() {
        typicalFeedbackQuestion.setGiverType(FeedbackParticipantType.TEAMS);
        FeedbackResponseComment typicalComment = getTypicalCommentFromTeam();

        Student differentStudentFromSameTeam = getTypicalStudent();
        differentStudentFromSameTeam.setTeam(new Team(
                new Section(typicalCourse, "Section C"),
                "first team"));

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), differentStudentFromSameTeam.getGoogleId()))
                .thenReturn(differentStudentFromSameTeam);
        when(mockLogic.getDeadlineForUser(typicalFeedbackSession, differentStudentFromSameTeam))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(15)));

        loginAsStudent(differentStudentFromSameTeam.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_differentInstructorInSameCourse_cannotAccessInstructorAsParticipantComment() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructorAsParticipant();

        Instructor differentInstructorInSameCourse = getTypicalInstructor();
        differentInstructorInSameCourse.setEmail("differentInstructor@teammates.tmt");

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), differentInstructorInSameCourse.getGoogleId()))
                .thenReturn(differentInstructorInSameCourse);
        when(mockLogic.getDeadlineForUser(typicalFeedbackSession, differentInstructorInSameCourse))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(15)));

        loginAsInstructor(differentInstructorInSameCourse.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_nonExistentFeedbackResponseComment_throwsEntityNotFoundException() {
        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123",
        };

        verifyEntityNotFoundAcl(params);
    }

    @Test
    void testAccessControl_instructorWithCorrectPrivilege_canAccessCrossSectionComment() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromTeam();

        Instructor instructorWithPrivilege = getTypicalInstructor();
        instructorWithPrivilege.setEmail("instructorWithPrivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section A",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        privileges.updatePrivilege("Section B",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
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
        FeedbackResponseComment typicalComment = getTypicalCommentFromTeam();

        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setEmail("instructorWithPrivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section B",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithoutPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
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
        FeedbackResponseComment typicalComment = getTypicalCommentFromTeam();

        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setEmail("instructorWithPrivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section A",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithoutPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivilege.getGoogleId()))
                .thenReturn(instructorWithoutPrivilege);

        loginAsInstructor(instructorWithoutPrivilege.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorSubmissionPastEndTimeBeforeDeadline_canAccess() {
        FeedbackSession feedbackSessionPastEndTime = getFeedbackSessionPastEndTime();
        FeedbackQuestion feedbackQuestion = getTypicalFeedbackQuestionForSession(feedbackSessionPastEndTime);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(feedbackQuestion);
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructorAsParticipant();

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getDeadlineForUser(feedbackSessionPastEndTime, typicalInstructor))
                .thenReturn(Instant.now().minus(Duration.ofMinutes(10)));

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_instructorSubmissionPastDeadline_cannotAccess() {
        FeedbackSession feedbackSessionPastEndTime = getFeedbackSessionPastEndTime();
        FeedbackQuestion feedbackQuestion = getTypicalFeedbackQuestionForSession(feedbackSessionPastEndTime);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(feedbackQuestion);
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructorAsParticipant();

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId()))
                .thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getDeadlineForUser(feedbackSessionPastEndTime, typicalInstructor))
                .thenReturn(Instant.now().minus(Duration.ofHours(1)));

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_studentSubmissionPastEndTimeBeforeDeadline_canAccess() {
        FeedbackSession feedbackSessionPastEndTime = getFeedbackSessionPastEndTime();
        FeedbackQuestion feedbackQuestion = getTypicalFeedbackQuestionForSession(feedbackSessionPastEndTime);
        feedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(feedbackQuestion);
        FeedbackResponseComment typicalComment = getTypicalCommentFromStudent();

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);
        when(mockLogic.getDeadlineForUser(feedbackSessionPastEndTime, typicalStudent))
                .thenReturn(Instant.now().minus(Duration.ofMinutes(10)));

        loginAsStudent(typicalStudent.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_studentSubmissionPastDeadline_cannotAccess() {
        FeedbackSession feedbackSessionPastEndTime = getFeedbackSessionPastEndTime();
        FeedbackQuestion feedbackQuestion = getTypicalFeedbackQuestionForSession(feedbackSessionPastEndTime);
        feedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(feedbackQuestion);
        FeedbackResponseComment typicalComment = getTypicalCommentFromStudent();

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);
        when(mockLogic.getDeadlineForUser(feedbackSessionPastEndTime, typicalStudent))
                .thenReturn(Instant.now().minus(Duration.ofHours(1)));

        loginAsStudent(typicalStudent.getGoogleId());

        verifyCannotAccess(params);
    }

    private FeedbackResponseComment getTypicalCommentFromStudent() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalFeedbackResponse,
                typicalStudent.getEmail(),
                FeedbackParticipantType.STUDENTS,
                typicalFeedbackResponse.getGiverSection(),
                typicalFeedbackResponse.getRecipientSection(),
                "typical comment",
                true,
                true,
                Arrays.asList(FeedbackParticipantType.INSTRUCTORS),
                Arrays.asList(FeedbackParticipantType.INSTRUCTORS),
                typicalStudent.getEmail());
        feedbackResponseComment.setId((long) 1);
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getUpdatedCommentFromStudent() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalFeedbackResponse,
                typicalStudent.getEmail(),
                FeedbackParticipantType.STUDENTS,
                typicalFeedbackResponse.getGiverSection(),
                typicalFeedbackResponse.getRecipientSection(),
                "updated comment",
                true,
                true,
                Arrays.asList(FeedbackParticipantType.GIVER, FeedbackParticipantType.INSTRUCTORS),
                Arrays.asList(FeedbackParticipantType.GIVER, FeedbackParticipantType.INSTRUCTORS),
                typicalStudent.getEmail());
        feedbackResponseComment.setId((long) 1);
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getTypicalCommentFromInstructor() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalFeedbackResponse,
                typicalInstructor.getEmail(),
                FeedbackParticipantType.INSTRUCTORS,
                typicalFeedbackResponse.getGiverSection(),
                typicalFeedbackResponse.getRecipientSection(),
                "typical comment",
                false,
                false,
                Arrays.asList(FeedbackParticipantType.INSTRUCTORS),
                Arrays.asList(FeedbackParticipantType.INSTRUCTORS),
                typicalInstructor.getEmail());
        feedbackResponseComment.setId((long) 2);
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getUpdatedCommentFromInstructor() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalFeedbackResponse,
                typicalInstructor.getEmail(),
                FeedbackParticipantType.INSTRUCTORS,
                typicalFeedbackResponse.getGiverSection(),
                typicalFeedbackResponse.getRecipientSection(),
                "updated comment",
                false,
                false,
                Arrays.asList(FeedbackParticipantType.GIVER, FeedbackParticipantType.INSTRUCTORS),
                Arrays.asList(FeedbackParticipantType.GIVER, FeedbackParticipantType.INSTRUCTORS),
                typicalInstructor.getEmail());
        feedbackResponseComment.setId((long) 2);
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getTypicalCommentFromInstructorAsParticipant() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalFeedbackResponse,
                typicalInstructor.getEmail(),
                FeedbackParticipantType.INSTRUCTORS,
                typicalFeedbackResponse.getGiverSection(),
                typicalFeedbackResponse.getRecipientSection(),
                "typical comment",
                true,
                true,
                Arrays.asList(FeedbackParticipantType.INSTRUCTORS),
                Arrays.asList(FeedbackParticipantType.INSTRUCTORS),
                typicalInstructor.getEmail());
        feedbackResponseComment.setId((long) 3);
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getUpdatedCommentFromInstructorAsParticipant() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalFeedbackResponse,
                typicalInstructor.getEmail(),
                FeedbackParticipantType.INSTRUCTORS,
                typicalFeedbackResponse.getGiverSection(),
                typicalFeedbackResponse.getRecipientSection(),
                "updated comment",
                true,
                true,
                Arrays.asList(FeedbackParticipantType.GIVER, FeedbackParticipantType.INSTRUCTORS),
                Arrays.asList(FeedbackParticipantType.GIVER, FeedbackParticipantType.INSTRUCTORS),
                typicalInstructor.getEmail());
        feedbackResponseComment.setId((long) 3);
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getTypicalCommentFromTeam() {
        Section sectionA = new Section(typicalCourse, "Section A");
        Section sectionB = new Section(typicalCourse, "Section B");
        typicalFeedbackResponse = FeedbackResponse.makeResponse(typicalFeedbackQuestion, "Section A", sectionA,
                "Section B", sectionB, getTypicalFeedbackResponseDetails());
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalFeedbackResponse,
                "first team",
                FeedbackParticipantType.TEAMS,
                sectionA,
                sectionB,
                "typical comment",
                true,
                true,
                Arrays.asList(FeedbackParticipantType.INSTRUCTORS),
                Arrays.asList(FeedbackParticipantType.INSTRUCTORS),
                "first team");
        feedbackResponseComment.setId((long) 4);
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

    private FeedbackSession getFeedbackSessionPastEndTime() {
        return new FeedbackSession(
                typicalFeedbackSession.getName(),
                typicalFeedbackSession.getCourse(),
                typicalFeedbackSession.getCreatorEmail(),
                typicalFeedbackSession.getInstructions(),
                Instant.now().minus(Duration.ofHours(2)),
                Instant.now().minus(Duration.ofHours(1)),
                Instant.now().minus(Duration.ofHours(1)),
                Instant.now().minus(Duration.ofHours(1)),
                Duration.ofMinutes(15),
                false,
                false,
                false);
    }

    private void checkJsonResponse(FeedbackResponseComment updatedComment, FeedbackResponseCommentData response) {
        assertEquals(updatedComment.getCommentText(), response.getCommentText());
        assertEquals(updatedComment.getShowCommentTo().toString(), response.getShowCommentTo().toString());
        assertEquals(updatedComment.getShowGiverNameTo().toString(), response.getShowGiverNameTo().toString());
    }

}
