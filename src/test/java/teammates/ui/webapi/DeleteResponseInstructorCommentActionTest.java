package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM;

import java.time.Instant;
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
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DeleteResponseInstructorCommentAction}.
 */
public class DeleteResponseInstructorCommentActionTest extends BaseActionTest<DeleteResponseInstructorCommentAction> {

    private Course typicalCourse;
    private Instructor typicalInstructor;
    private Student typicalStudent;
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
        FeedbackSession typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
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
        ResponseInstructorComment typicalResponseInstructorComment = getTypicalCommentFromInstructor();
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalResponseInstructorComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalResponseInstructorComment.getId()))
                .thenReturn(typicalResponseInstructorComment);

        loginAsInstructor(typicalInstructor.getGoogleId());

        DeleteResponseInstructorCommentAction action = getAction(params);
        JsonResult r = getJsonResult(action);
        MessageOutput output = (MessageOutput) r.getOutput();

        assertEquals("Successfully deleted feedback response comment.", output.getMessage());
        verify(mockLogic).deleteResponseInstructorComment(typicalResponseInstructorComment.getId());
    }

    @Test
    void testExecute_nonExistentResponseInstructorComment_success() {
        UUID nonExistentCommentId = UUID.fromString("00000000-0000-4000-8000-000000009999");
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, nonExistentCommentId.toString(),
        };

        when(mockLogic.getResponseInstructorComment(nonExistentCommentId))
                .thenReturn(null);

        loginAsInstructor(typicalInstructor.getGoogleId());

        DeleteResponseInstructorCommentAction action = getAction(params);
        JsonResult r = getJsonResult(action);
        MessageOutput output = (MessageOutput) r.getOutput();

        assertEquals("Successfully deleted feedback response comment.", output.getMessage());
        verify(mockLogic).deleteResponseInstructorComment(nonExistentCommentId);
    }

    @Test
    void testAccessControl_instructorWithoutSubmitSessionInSectionsPrivilege_cannotAccessInstructorComment() {
        ResponseInstructorComment typicalResponseInstructorComment = getTypicalCommentFromInstructor();

        Instructor instructorWithoutAccess = getTypicalInstructor();
        instructorWithoutAccess.setEmail("helper@teammates.tmt");
        instructorWithoutAccess.setPrivileges(new InstructorPrivileges(INSTRUCTOR_PERMISSION_ROLE_CUSTOM));

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalResponseInstructorComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalResponseInstructorComment.getId()))
                .thenReturn(typicalResponseInstructorComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutAccess.getGoogleId()))
                .thenReturn(instructorWithoutAccess);

        loginAsInstructor(instructorWithoutAccess.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_logOut_cannotAccessInstructorComment() {
        ResponseInstructorComment typicalResponseInstructorComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalResponseInstructorComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalResponseInstructorComment.getId()))
                .thenReturn(typicalResponseInstructorComment);

        logoutUser();

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_unregisteredUser_cannotAccessInstructorComment() {
        ResponseInstructorComment typicalResponseInstructorComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalResponseInstructorComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalResponseInstructorComment.getId()))
                .thenReturn(typicalResponseInstructorComment);
        when(mockLogic.getInstructorByGoogleId(any(String.class), any(String.class))).thenReturn(null);

        loginAsUnregistered("unreg.user");

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_studentsForInstructorComment_cannotAccessInstructorComment() {
        ResponseInstructorComment typicalResponseInstructorComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalResponseInstructorComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalResponseInstructorComment.getId()))
                .thenReturn(typicalResponseInstructorComment);

        loginAsStudent(typicalStudent.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorAsCommentGiver_canAccessInstructorComment() {
        ResponseInstructorComment typicalResponseInstructorComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalResponseInstructorComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalResponseInstructorComment.getId()))
                .thenReturn(typicalResponseInstructorComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_instructorInSameCourse_canAccessInstructorComment() {
        ResponseInstructorComment typicalResponseInstructorComment = getTypicalCommentFromInstructor();

        Instructor instructorInSameCourse = getTypicalInstructor();
        instructorInSameCourse.setEmail("instructor2@teammates.tmt");

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalResponseInstructorComment.getId().toString(),
        };

        loginAsInstructor(instructorInSameCourse.getGoogleId());

        when(mockLogic.getResponseInstructorComment(typicalResponseInstructorComment.getId()))
                .thenReturn(typicalResponseInstructorComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorInSameCourse.getGoogleId()))
                .thenReturn(instructorInSameCourse);

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_adminToMasqueradeAsInstructor_canAccessInstructorComment() {
        ResponseInstructorComment typicalResponseInstructorComment = getTypicalCommentFromInstructor();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalResponseInstructorComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalResponseInstructorComment.getId()))
                .thenReturn(typicalResponseInstructorComment);
        when(mockLogic.getInstructorByGoogleId(any(String.class), any(String.class)))
                .thenReturn(typicalInstructor);

        loginAsAdmin();

        verifyCanMasquerade(typicalInstructor.getGoogleId(), params);
    }

    @Test
    void testAccessControl_instructorWithWrongSectionPrivilege_cannotAccessInstructorComment() {
        ResponseInstructorComment typicalResponseInstructorComment = getTypicalCommentFromInstructor();

        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setEmail("instructorWithoutPrivilege@teammates.tmt");

        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("test-section1",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithoutPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalResponseInstructorComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalResponseInstructorComment.getId()))
                .thenReturn(typicalResponseInstructorComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivilege.getGoogleId()))
                .thenReturn(instructorWithoutPrivilege);

        loginAsInstructor(instructorWithoutPrivilege.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorWithCorrectPrivilege_canAccessCrossSectionComment() {
        ResponseInstructorComment typicalResponseInstructorComment = getTypicalCommentFromTeam(typicalStudent.getTeam());

        Instructor instructorWithPrivilege = getTypicalInstructor();
        instructorWithPrivilege.setEmail("instructorWithPrivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section A",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        privileges.updatePrivilege("Section B",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalResponseInstructorComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalResponseInstructorComment.getId()))
                .thenReturn(typicalResponseInstructorComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithPrivilege.getGoogleId()))
                .thenReturn(instructorWithPrivilege);

        loginAsInstructor(instructorWithPrivilege.getGoogleId());

        verifyCanAccess(params);
    }

    @Test
    void testAccessControl_instructorWithoutGiverSectionPrivilege_cannotAccessCrossSectionComment() {
        ResponseInstructorComment typicalResponseInstructorComment = getTypicalCommentFromTeam(typicalStudent.getTeam());

        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setEmail("instructorWithPrivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section B",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithoutPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalResponseInstructorComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalResponseInstructorComment.getId()))
                .thenReturn(typicalResponseInstructorComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivilege.getGoogleId()))
                .thenReturn(instructorWithoutPrivilege);

        loginAsInstructor(instructorWithoutPrivilege.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorWithoutRecipientSectionPrivilege_cannotAccessCrossSectionComment() {
        ResponseInstructorComment typicalResponseInstructorComment = getTypicalCommentFromTeam(typicalStudent.getTeam());

        Instructor instructorWithoutPrivilege = getTypicalInstructor();
        instructorWithoutPrivilege.setEmail("instructorWithPrivilege@teammates.tmt");
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.updatePrivilege("Section A",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructorWithoutPrivilege.setPrivileges(privileges);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, typicalResponseInstructorComment.getId().toString(),
        };

        when(mockLogic.getResponseInstructorComment(typicalResponseInstructorComment.getId()))
                .thenReturn(typicalResponseInstructorComment);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutPrivilege.getGoogleId()))
                .thenReturn(instructorWithoutPrivilege);

        loginAsInstructor(instructorWithoutPrivilege.getGoogleId());

        verifyCannotAccess(params);
    }

    private ResponseInstructorComment getTypicalCommentFromInstructor() {
        ResponseGiver giver = new ResponseGiver(typicalInstructor);
        ResponseInstructorComment responseInstructorComment = new ResponseInstructorComment(
                giver,
                "typical comment",
                Arrays.asList(ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.INSTRUCTORS),
                giver);
        typicalFeedbackResponse.addResponseInstructorComment(responseInstructorComment);
        responseInstructorComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000002"));
        responseInstructorComment.setCreatedAt(Instant.EPOCH);
        responseInstructorComment.setUpdatedAt(Instant.EPOCH);
        return responseInstructorComment;
    }

    private ResponseInstructorComment getTypicalCommentFromTeam(Team team) {
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
        ResponseInstructorComment responseInstructorComment = new ResponseInstructorComment(
                giver,
                "typical comment",
                Arrays.asList(ViewerType.INSTRUCTORS),
                Arrays.asList(ViewerType.INSTRUCTORS),
                giver);
        typicalFeedbackResponse.addResponseInstructorComment(responseInstructorComment);
        responseInstructorComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000004"));
        responseInstructorComment.setCreatedAt(Instant.EPOCH);
        responseInstructorComment.setUpdatedAt(Instant.EPOCH);
        return responseInstructorComment;
    }

}
