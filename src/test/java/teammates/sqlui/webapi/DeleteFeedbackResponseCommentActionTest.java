package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM;

import java.time.Duration;
import java.time.Instant;
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
import teammates.ui.output.MessageOutput;
import teammates.ui.request.Intent;
import teammates.ui.webapi.DeleteFeedbackResponseCommentAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link DeleteFeedbackResponseCommentAction}.
 */
public class DeleteFeedbackResponseCommentActionTest extends BaseActionTest<DeleteFeedbackResponseCommentAction> {

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
        return DELETE;
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
    void testExecute_typicalCase_success() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromStudent();
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        DeleteFeedbackResponseCommentAction action = getAction(params);
        JsonResult r = getJsonResult(action);
        MessageOutput output = (MessageOutput) r.getOutput();

        assertEquals("Successfully deleted feedback response comment.", output.getMessage());
        verify(mockLogic).deleteFeedbackResponseComment(typicalFeedbackResponseComment.getId());
    }

    @Test
    void testExecute_nonExistentFeedbackResponseComment_failSilently() {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123456",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment((long) 123456))
                .thenReturn(null);

        loginAsInstructor(typicalInstructor.getGoogleId());

        DeleteFeedbackResponseCommentAction action = getAction(params);
        JsonResult r = getJsonResult(action);
        MessageOutput output = (MessageOutput) r.getOutput();

        assertEquals("Successfully deleted feedback response comment.", output.getMessage());
        verify(mockLogic, never()).deleteFeedbackResponseComment(null);
    }

    @Test
    void testAccessControl_instructorWithoutSubmitSessionInSectionsPrivilege_cannotAccessInstructorComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromInstructor();

        Instructor instructorWithoutAccess = getTypicalInstructor();
        instructorWithoutAccess.setEmail("helper@teammates.tmt");
        instructorWithoutAccess.setPrivileges(new InstructorPrivileges(INSTRUCTOR_PERMISSION_ROLE_CUSTOM));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutAccess.getGoogleId()))
                .thenReturn(instructorWithoutAccess);

        loginAsInstructor(instructorWithoutAccess.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_logOut_cannotAccessInstructorComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);

        logoutUser();

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_unregisteredUser_cannotAccessInstructorComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(any(String.class), any(String.class))).thenReturn(null);

        loginAsUnregistered("unreg.user");

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_studentsForInstructorComment_cannotAccessInstructorComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);

        loginAsStudent(typicalStudent.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorAsCommentGiver_canAccessInstructorComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_instructorInSameCourse_canAccessInstructorComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromInstructor();

        Instructor instructorInSameCourse = getTypicalInstructor();
        instructorInSameCourse.setEmail("instructor2@teammates.tmt");

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        loginAsInstructor(instructorInSameCourse.getGoogleId());

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorInSameCourse.getGoogleId()))
                .thenReturn(instructorInSameCourse);

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_adminToMasqueradeAsInstructor_canAccessInstructorComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(any(String.class), any(String.class)))
                .thenReturn(typicalInstructor);

        loginAsAdmin();

        verifyCanMasquerade(typicalInstructor.getGoogleId(), params);
    }

    @Test
    void testAccessControl_instructorWithWrongSectionPrivilege_cannotAccessInstructorComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromInstructor();

        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setEmail("instructorWithoutPrivilege@teammates.tmt");

        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("test-section1",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithoutPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivilege.getGoogleId()))
                .thenReturn(instructorWithoutPrivilege);

        loginAsInstructor(instructorWithoutPrivilege.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorAsCommentGiver_canAccessInstructorAsParticipantComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromInstructorAsParticipant();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getDeadlineForUser(typicalFeedbackSession, typicalInstructor))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(15)));

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_differentInstructorInSameCourse_cannotAccessInstructorAsParticipantComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromInstructorAsParticipant();

        Instructor differentInstructorInSameCourse = getTypicalInstructor();
        differentInstructorInSameCourse.setEmail("differentInstructor@teammates.tmt");

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), differentInstructorInSameCourse.getGoogleId()))
                .thenReturn(differentInstructorInSameCourse);
        when(mockLogic.getDeadlineForUser(typicalFeedbackSession, differentInstructorInSameCourse))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(15)));

        loginAsInstructor(differentInstructorInSameCourse.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_studentAsCommentGiver_canAccessStudentComment() {
        typicalFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);

        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromStudent();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);
        when(mockLogic.getDeadlineForUser(typicalFeedbackSession, typicalStudent))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(15)));

        loginAsStudent(typicalStudent.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_differentStudentFromSameCourse_cannotAccessStudentComment() {
        typicalFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);

        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromStudent();

        Student differentStudentFromSameCourse = getTypicalStudent();
        differentStudentFromSameCourse.setEmail("differentStudent@teammates.tmt");

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), differentStudentFromSameCourse.getGoogleId()))
                .thenReturn(differentStudentFromSameCourse);
        when(mockLogic.getDeadlineForUser(typicalFeedbackSession, differentStudentFromSameCourse))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(15)));

        loginAsStudent(differentStudentFromSameCourse.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_unregisteredUsers_cannotAccessStudentComment() {
        typicalFeedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);

        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromStudent();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);

        loginAsUnregistered("unreg");

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_differentStudentInDifferentTeam_cannotAccessTeamComment() {
        typicalFeedbackQuestion.setGiverType(FeedbackParticipantType.TEAMS);

        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromTeam();

        typicalStudent.setTeam(new Team(getTypicalSection(), "first team"));

        Student differentStudentInDifferentTeam = new Student(typicalCourse, "differentStudent",
                "differentstudent@teammates.tmt", "comments",
                new Team(getTypicalSection(), "different team"));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), differentStudentInDifferentTeam.getGoogleId()))
                .thenReturn(differentStudentInDifferentTeam);
        when(mockLogic.getDeadlineForUser(typicalFeedbackSession, differentStudentInDifferentTeam))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(15)));

        loginAsStudent(differentStudentInDifferentTeam.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_differentStudentInSameTeam_canAccessTeamComment() {
        typicalFeedbackQuestion.setGiverType(FeedbackParticipantType.TEAMS);

        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromTeam();

        typicalStudent.setTeam(new Team(new Section(typicalCourse, "Section A"), "first team"));

        Student differentStudentInSameTeam = new Student(typicalCourse, "differentStudent",
                "differentstudent@teammates.tmt", "comments",
                new Team(new Section(typicalCourse, "Section A"), "first team"));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), differentStudentInSameTeam.getGoogleId()))
                .thenReturn(differentStudentInSameTeam);
        when(mockLogic.getDeadlineForUser(typicalFeedbackSession, differentStudentInSameTeam))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(15)));

        loginAsStudent(differentStudentInSameTeam.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_instructorWithCorrectPrivilege_canAccessCrossSectionComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromTeam();

        Instructor instructorWithPrivilege = getTypicalInstructor();
        instructorWithPrivilege.setEmail("instructorWithPrivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section A",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        privileges.updatePrivilege("Section B",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithPrivilege.getGoogleId()))
                .thenReturn(instructorWithPrivilege);

        loginAsInstructor(instructorWithPrivilege.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_instructorWithoutGiverSectionPrivilege_cannotAccessCrossSectionComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromTeam();

        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setEmail("instructorWithPrivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section B",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithoutPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivilege.getGoogleId()))
                .thenReturn(instructorWithoutPrivilege);

        loginAsInstructor(instructorWithoutPrivilege.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorWithoutRecipientSectionPrivilege_cannotAccessCrossSectionComment() {
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromTeam();

        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setEmail("instructorWithPrivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section A",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithoutPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivilege.getGoogleId()))
                .thenReturn(instructorWithoutPrivilege);

        loginAsInstructor(instructorWithoutPrivilege.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorSubmissionPastEndTimeBeforeDeadlineWithinGracePeriod_canAccess() {
        FeedbackSession feedbackSessionPastEndTime = getFeedbackSessionPastEndTime();
        FeedbackQuestion feedbackQuestion = getTypicalFeedbackQuestionForSession(feedbackSessionPastEndTime);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(feedbackQuestion);
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromInstructorAsParticipant();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getDeadlineForUser(feedbackSessionPastEndTime, typicalInstructor))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(10)));

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_instructorSubmissionPastDeadline_cannotAccess() {
        FeedbackSession feedbackSessionPastEndTime = getFeedbackSessionPastEndTime();
        FeedbackQuestion feedbackQuestion = getTypicalFeedbackQuestionForSession(feedbackSessionPastEndTime);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(feedbackQuestion);
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromInstructorAsParticipant();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getDeadlineForUser(feedbackSessionPastEndTime, typicalInstructor))
                .thenReturn(Instant.now().minus(Duration.ofHours(1)));

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_studentSubmissionPastEndTimeBeforeDeadlineWithinGracePeriod_canAccess() {
        FeedbackSession feedbackSessionPastEndTime = getFeedbackSessionPastEndTime();
        FeedbackQuestion feedbackQuestion = getTypicalFeedbackQuestionForSession(feedbackSessionPastEndTime);
        feedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(feedbackQuestion);
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromStudent();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
        when(mockLogic.getStudentByGoogleId(typicalCourse.getId(), typicalStudent.getGoogleId()))
                .thenReturn(typicalStudent);
        when(mockLogic.getDeadlineForUser(feedbackSessionPastEndTime, typicalStudent))
                .thenReturn(Instant.now().plus(Duration.ofMinutes(10)));

        loginAsStudent(typicalStudent.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_studentSubmissionPastDeadline_cannotAccess() {
        FeedbackSession feedbackSessionPastEndTime = getFeedbackSessionPastEndTime();
        FeedbackQuestion feedbackQuestion = getTypicalFeedbackQuestionForSession(feedbackSessionPastEndTime);
        feedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(feedbackQuestion);
        FeedbackResponseComment typicalFeedbackResponseComment = getTypicalCommentFromStudent();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalFeedbackResponseComment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        when(mockLogic.getFeedbackResponseComment(typicalFeedbackResponseComment.getId()))
                .thenReturn(typicalFeedbackResponseComment);
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

}
