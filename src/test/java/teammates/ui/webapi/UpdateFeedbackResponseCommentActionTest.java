package teammates.ui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;
import teammates.ui.request.Intent;

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
        when(mockLogic.updateFeedbackResponseComment(any(UUID.class), any(FeedbackResponseCommentUpdateRequest.class),
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
        when(mockLogic.updateFeedbackResponseComment(any(UUID.class), any(FeedbackResponseCommentUpdateRequest.class),
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
        when(mockLogic.updateFeedbackResponseComment(any(UUID.class), any(FeedbackResponseCommentUpdateRequest.class),
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
        when(mockLogic.updateFeedbackResponseComment(any(UUID.class), any(FeedbackResponseCommentUpdateRequest.class),
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
        updatedComment.setShowCommentTo(Arrays.asList(ViewerType.STUDENTS));
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
        when(mockLogic.updateFeedbackResponseComment(any(UUID.class), any(FeedbackResponseCommentUpdateRequest.class),
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
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalComment.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest updateRequest = getTypicalRequestBody();

        loginAsInstructor(differentInstructor.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalComment.getId())).thenReturn(typicalComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), differentInstructor.getGoogleId()))
                .thenReturn(differentInstructor);
        when(mockLogic.updateFeedbackResponseComment(any(UUID.class), any(FeedbackResponseCommentUpdateRequest.class),
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
        when(mockLogic.updateFeedbackResponseComment(any(UUID.class), any(FeedbackResponseCommentUpdateRequest.class),
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
        typicalFeedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);
        FeedbackResponseComment typicalComment = getTypicalCommentFromInstructor();

        Student differentStudentFromSameCourse = getTypicalStudent();
        differentStudentFromSameCourse.setEmail("differentstudent@teammates.tmt");

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
        typicalFeedbackQuestion.setGiverType(QuestionGiverType.TEAMS);
        FeedbackResponseComment typicalComment = getTypicalCommentFromTeam();

        Student differentStudentFromDifferentTeam = getTypicalStudent();
        Section section = new Section("Section C");
        typicalCourse.addSection(section);
        Team team = new Team("different team");
        section.addTeam(team);
        differentStudentFromDifferentTeam.setTeam(team);

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
        typicalFeedbackQuestion.setGiverType(QuestionGiverType.TEAMS);
        FeedbackResponseComment typicalComment = getTypicalCommentFromTeam();

        Student differentStudentFromSameTeam = getTypicalStudent();
        Section section = new Section("Section C");
        typicalCourse.addSection(section);
        Team team = new Team("first team");
        section.addTeam(team);
        differentStudentFromSameTeam.setTeam(team);

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
        differentInstructorInSameCourse.setEmail("differentinstructor@teammates.tmt");

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
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "00000000-0000-4000-8000-000000009999",
        };

        verifyEntityNotFoundAcl(params);
    }

    @Test
    void testAccessControl_instructorWithCorrectPrivilege_canAccessCrossSectionComment() {
        FeedbackResponseComment typicalComment = getTypicalCommentFromTeam();

        Instructor instructorWithPrivilege = getTypicalInstructor();
        instructorWithPrivilege.setEmail("instructorwithprivilege@teammates.tmt");
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
        instructorWithoutPrivilege.setEmail("instructorwithprivilege@teammates.tmt");
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
        instructorWithoutPrivilege.setEmail("instructorwithprivilege@teammates.tmt");
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
        feedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);
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
        feedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);
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
                typicalStudent.getEmail(),
                "typical comment",
                true,
                true,
                Arrays.asList(ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.INSTRUCTORS),
                typicalStudent.getEmail());
        typicalFeedbackResponse.addFeedbackResponseComment(feedbackResponseComment);
        feedbackResponseComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000001"));
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getUpdatedCommentFromStudent() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalStudent.getEmail(),
                "updated comment",
                true,
                true,
                Arrays.asList(ViewerType.GIVER, ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.GIVER, ViewerType.INSTRUCTORS),
                typicalStudent.getEmail());
        typicalFeedbackResponse.addFeedbackResponseComment(feedbackResponseComment);
        feedbackResponseComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000001"));
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getTypicalCommentFromInstructor() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalInstructor.getEmail(),
                "typical comment",
                false,
                false,
                Arrays.asList(ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.INSTRUCTORS),
                typicalInstructor.getEmail());
        typicalFeedbackResponse.addFeedbackResponseComment(feedbackResponseComment);
        feedbackResponseComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000002"));
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getUpdatedCommentFromInstructor() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalInstructor.getEmail(),
                "updated comment",
                false,
                false,
                Arrays.asList(ViewerType.GIVER, ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.GIVER, ViewerType.INSTRUCTORS),
                typicalInstructor.getEmail());
        typicalFeedbackResponse.addFeedbackResponseComment(feedbackResponseComment);
        feedbackResponseComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000002"));
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getTypicalCommentFromInstructorAsParticipant() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalInstructor.getEmail(),
                "typical comment",
                true,
                true,
                Arrays.asList(ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.INSTRUCTORS),
                typicalInstructor.getEmail());
        typicalFeedbackResponse.addFeedbackResponseComment(feedbackResponseComment);
        feedbackResponseComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000003"));
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getUpdatedCommentFromInstructorAsParticipant() {
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                typicalInstructor.getEmail(),
                "updated comment",
                true,
                true,
                Arrays.asList(ViewerType.GIVER, ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.GIVER, ViewerType.INSTRUCTORS),
                typicalInstructor.getEmail());
        typicalFeedbackResponse.addFeedbackResponseComment(feedbackResponseComment);
        feedbackResponseComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000003"));
        feedbackResponseComment.setCreatedAt(Instant.EPOCH);
        feedbackResponseComment.setUpdatedAt(Instant.EPOCH);
        return feedbackResponseComment;
    }

    private FeedbackResponseComment getTypicalCommentFromTeam() {
        Section sectionA = new Section("Section A");
        typicalCourse.addSection(sectionA);
        Section sectionB = new Section("Section B");
        typicalCourse.addSection(sectionB);
        typicalFeedbackResponse = FeedbackResponse.makeResponse("Section A", sectionA,
                "Section B", sectionB, getTypicalFeedbackResponseDetails());
        typicalFeedbackQuestion.addFeedbackResponse(typicalFeedbackResponse);
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(
                "first team",
                "typical comment",
                true,
                true,
                Arrays.asList(ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.INSTRUCTORS),
                "first team");
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

    private FeedbackSession getFeedbackSessionPastEndTime() {
        FeedbackSession feedbackSession = new FeedbackSession(
                typicalFeedbackSession.getName(),
                typicalFeedbackSession.getCreatorEmail(),
                typicalFeedbackSession.getInstructions(),
                Instant.now().minus(Duration.ofHours(2)),
                Instant.now().minus(Duration.ofHours(1)),
                Instant.now().minus(Duration.ofHours(1)),
                Instant.now().minus(Duration.ofHours(1)),
                Duration.ofMinutes(15),
                false,
                false);
        typicalFeedbackSession.getCourse().addFeedbackSession(feedbackSession);
        return feedbackSession;
    }

    private void checkJsonResponse(FeedbackResponseComment updatedComment, FeedbackResponseCommentData response) {
        assertEquals(updatedComment.getCommentText(), response.getCommentText());
        assertEquals(updatedComment.getShowCommentTo().toString(), response.getShowCommentTo().toString());
        assertEquals(updatedComment.getShowGiverNameTo().toString(), response.getShowGiverNameTo().toString());
    }

}
