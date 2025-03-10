package teammates.sqlui.webapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.sqllogic.api.Logic;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.ui.output.StudentsData;
import teammates.ui.webapi.GetStudentsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetStudentsAction}.
 */
public class GetStudentsActionTest extends BaseActionTest<GetStudentsAction> {
    private Instructor stubInstructorWithoutPrivileges;
    private Instructor stubInstructorWithAllPrivileges;
    private Instructor stubInstructorWithOnlyViewSectionPrivileges;
    private Course stubCourse;
    private Team stubTeamOne;
    private InstructorPermissionRole customRole;
    private Student stubStudentOne;
    private Student stubStudentTwo;
    private List<Student> stubStudentListOne;
    private List<Student> stubStudentListTwo;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    protected void setUp() {
        mockLogic = mock(Logic.class);
        stubCourse = getTypicalCourse();
        stubInstructorWithAllPrivileges = getTypicalInstructor();
        customRole = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        InstructorPrivileges customInstructorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        stubInstructorWithoutPrivileges = new Instructor(stubCourse, "instructor-1-name", "valid1@teammates.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, customRole, customInstructorPrivileges);
        InstructorPrivileges sectionPrivilegesOnly =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        sectionPrivilegesOnly.updatePrivilege("section-1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        stubInstructorWithOnlyViewSectionPrivileges = new Instructor(stubCourse, "instructor-2-name", "valid2@teammates.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, customRole, sectionPrivilegesOnly);

        Section stubSection = new Section(stubCourse, "section-1");
        stubTeamOne = new Team(stubSection, "team-1");
        Team stubTeamTwo = new Team(stubSection, "team-2");
        stubStudentListOne = new ArrayList<>();
        stubStudentListTwo = new ArrayList<>();
        stubStudentOne = getTypicalStudent();
        stubStudentOne.setTeam(stubTeamOne);
        stubStudentTwo = new Student(stubCourse, "student-2", "student2@teammates.tmt", "comments", stubTeamTwo);
        Student stubStudentThree = new Student(stubCourse, "student-3", "student3@teammates.tmt", "comments", stubTeamTwo);
        stubStudentListOne.add(stubStudentOne);
        stubStudentListTwo.add(stubStudentTwo);
        stubStudentListTwo.add(stubStudentThree);
    }

    @Test
    void testExecute_invalidParameters_throwsInvalidHttpParameterException() {
        String[] params = {};
        verifyHttpParameterFailure(params);
        String[] params2 = {
                Const.ParamsNames.TEAM_NAME, stubTeamOne.getName(),
        };
        verifyHttpParameterFailure(params2);

        String[] params3 = {
                Const.ParamsNames.COURSE_ID, null,
                Const.ParamsNames.TEAM_NAME, stubTeamOne.getName(),
        };
        verifyHttpParameterFailure(params3);

        String[] params5 = {
                Const.ParamsNames.COURSE_ID, null,
                Const.ParamsNames.TEAM_NAME, null,
        };
        verifyHttpParameterFailure(params5);
    }

    @Test
     void testExecute_instructorWithPermission_success() {
        loginAsInstructor(stubInstructorWithAllPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithAllPrivileges.getGoogleId()))
                .thenReturn(stubInstructorWithAllPrivileges);
        when(mockLogic.getStudentsForCourse(stubCourse.getId())).thenReturn(stubStudentListTwo);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();
        assertEquals(stubStudentListTwo.size(), actualStudentsData.getStudents().size());
        assertEquals(stubStudentListTwo.get(0).getGoogleId(), actualStudentsData.getStudents().get(0).getGoogleId());
        assertEquals(stubStudentListTwo.get(1).getGoogleId(), actualStudentsData.getStudents().get(1).getGoogleId());
        verify(mockLogic, never()).getStudentsByTeamName(null, stubCourse.getId());
    }

    @Test
    void testExecute_instructorWithoutPermission_emptyList() {
        loginAsInstructor(stubInstructorWithoutPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithoutPrivileges.getGoogleId()))
                .thenReturn(stubInstructorWithoutPrivileges);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.TEAM_NAME, null,
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();
        verify(mockLogic, times(1)).getStudentsByTeamName(null, stubCourse.getId());
        verify(mockLogic, never()).getStudentsForCourse(stubCourse.getId());
        assertEquals(0, actualStudentsData.getStudents().size());
    }

    @Test
    void testExecute_instructorWithoutPermissionWithTeamParams_success() {
        loginAsInstructor(stubInstructorWithoutPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithoutPrivileges.getGoogleId()))
                .thenReturn(stubInstructorWithoutPrivileges);
        when(mockLogic.getStudentsByTeamName(stubTeamOne.getName(), stubCourse.getId())).thenReturn(stubStudentListOne);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.TEAM_NAME, stubTeamOne.getName(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();
        verify(mockLogic, times(1)).getStudentsByTeamName(stubTeamOne.getName(), stubCourse.getId());
        verify(mockLogic, never()).getStudentsForCourse(stubCourse.getId());
        assertEquals(stubStudentListOne.size(), actualStudentsData.getStudents().size());
        assertEquals(stubStudentListOne.get(0).getGoogleId(), actualStudentsData.getStudents().get(0).getGoogleId());
    }

    @Test
    void testExecute_instructorWithSameSectionPrivilegesAsStudents_success() {
        loginAsInstructor(stubInstructorWithOnlyViewSectionPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithOnlyViewSectionPrivileges
                .getGoogleId())).thenReturn(stubInstructorWithOnlyViewSectionPrivileges);
        when(mockLogic.getStudentsForCourse(stubCourse.getId())).thenReturn(stubStudentListTwo);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();
        assertEquals(stubStudentListTwo.size(), actualStudentsData.getStudents().size());
        assertEquals(stubStudentListTwo.get(0).getGoogleId(), actualStudentsData.getStudents().get(0).getGoogleId());
        assertEquals(stubStudentListTwo.get(1).getGoogleId(), actualStudentsData.getStudents().get(1).getGoogleId());
        verify(mockLogic, never()).getStudentsByTeamName(null, stubCourse.getId());
        verify(mockLogic, times(1)).getStudentsForCourse(stubCourse.getId());
    }

    @Test
    void testExecute_instructorWithDifferentSectionPrivilegesAsStudents_emptyList() {
        InstructorPrivileges wrongPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        wrongPrivileges.updatePrivilege("random-1",
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        Instructor stubInstructorWithOnlyViewPrivilegesForDifferentSection =
                new Instructor(stubCourse, "instructor-3-name", "valid3@teammates.tmt",
                        false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, customRole, wrongPrivileges);
        loginAsInstructor(stubInstructorWithOnlyViewPrivilegesForDifferentSection.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithOnlyViewSectionPrivileges.getGoogleId()))
                .thenReturn(stubInstructorWithOnlyViewPrivilegesForDifferentSection);
        when(mockLogic.getStudentsForCourse(stubCourse.getId())).thenReturn(stubStudentListTwo);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();
        assertEquals(0, actualStudentsData.getStudents().size());
        verify(mockLogic, never()).getStudentsByTeamName(null, stubCourse.getId());
        verify(mockLogic, times(1)).getStudentsForCourse(stubCourse.getId());
    }

    @Test
    void testExecute_studentSameTeam_success() {
        loginAsStudent(stubStudentOne.getGoogleId());
        when(mockLogic.getStudentsByTeamName(stubStudentOne.getTeam().getName(),
                stubStudentOne.getCourse().getId())).thenReturn(stubStudentListOne);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();
        assertEquals(stubStudentListOne.size(), actualStudentsData.getStudents().size());
        assertEquals(stubStudentListOne.get(0).getGoogleId(), actualStudentsData.getStudents().get(0).getGoogleId());
        assertNull(actualStudentsData.getStudents().get(0).getJoinState());
        assertNull(actualStudentsData.getStudents().get(0).getComments());
        verify(mockLogic, times(1))
                .getStudentsByTeamName(stubStudentOne.getTeamName(), stubCourse.getId());
        verify(mockLogic, never()).getStudentsForCourse(stubCourse.getId());
    }

    @Test
    void testExecute_adminWithTeamNameParams_emptyList() {
        loginAsAdmin();
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        when(mockLogic.getStudentsByTeamName(stubStudentOne.getTeam().getName(),
                stubStudentOne.getCourse().getId())).thenReturn(stubStudentListOne);
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();
        assertEquals(stubStudentListOne.size(), actualStudentsData.getStudents().size());
        assertEquals(stubStudentListOne.get(0).getGoogleId(), actualStudentsData.getStudents().get(0).getGoogleId());
        verify(mockLogic, times(1))
                .getStudentsByTeamName(stubStudentOne.getTeamName(), stubCourse.getId());
        verify(mockLogic, never()).getStudentsForCourse(stubCourse.getId());
    }

    @Test
    void testExecute_unregisteredLoggedInUser_success() {
        loginAsUnregistered("id");
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        when(mockLogic.getStudentsByTeamName(stubStudentOne.getTeam().getName(),
                stubStudentOne.getCourse().getId())).thenReturn(stubStudentListOne);
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();
        assertEquals(stubStudentListOne.size(), actualStudentsData.getStudents().size());
        assertEquals(stubStudentListOne.get(0).getGoogleId(), actualStudentsData.getStudents().get(0).getGoogleId());
        verify(mockLogic, times(1))
                .getStudentsByTeamName(stubStudentOne.getTeamName(), stubCourse.getId());
        verify(mockLogic, never()).getStudentsForCourse(stubCourse.getId());
    }

    @Test
    void testSpecificAccessControl_loggedInInstructorWithPrivileges_canAccess() {
        loginAsInstructor(stubInstructorWithAllPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithAllPrivileges.getGoogleId())).thenReturn(stubInstructorWithAllPrivileges);
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCanAccess(params);
        logoutUser();
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_loggedInInstructorWithoutPrivileges_cannotAccess() {
        loginAsInstructor(stubInstructorWithoutPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithoutPrivileges.getGoogleId())).thenReturn(stubInstructorWithoutPrivileges);
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_loggedInInstructorWithOnlyViewSectionPrivileges_canAccess() {
        loginAsInstructor(stubInstructorWithOnlyViewSectionPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithOnlyViewSectionPrivileges.getGoogleId()))
                .thenReturn(stubInstructorWithOnlyViewSectionPrivileges);
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCanAccess(params);
        logoutUser();
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_studentInSameTeam_canAccess() {
        loginAsStudent(stubStudentOne.getGoogleId());
        when(mockLogic.getStudentByGoogleId(stubStudentOne.getCourse().getId(),
                stubStudentOne.getGoogleId())).thenReturn(stubStudentOne);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        verifyCanAccess(params);
        logoutUser();
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_studentInDifferentTeam_cannotAccess() {
        loginAsStudent(stubStudentOne.getGoogleId());
        when(mockLogic.getStudentByGoogleId(stubStudentOne.getCourse().getId(),
                stubStudentOne.getGoogleId())).thenReturn(stubStudentOne);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentTwo.getTeam().getName(),
        };
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_userNotLoggedIn_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubTeamOne.getName(),
        };
        verifyCannotAccess(params);

        String[] params2 = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
        };
        verifyCannotAccess(params2);
    }

    @Test
    void test_notStudentNotInstructor_cannotAccess() {
        loginAsAdmin();
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentTwo.getTeam().getName(),
        };
        verifyCannotAccess(params);

    }
}
