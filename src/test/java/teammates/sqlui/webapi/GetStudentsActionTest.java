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

    /**
     * Enum to represent the type of user.
     */
    enum Type {
        INSTRUCTOR, STUDENT
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

        verifyStudentsData(stubStudentListTwo, actualStudentsData, Type.INSTRUCTOR);
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

        assertEquals(0, actualStudentsData.getStudents().size());
        verify(mockLogic, times(1)).getStudentsByTeamName(null, stubCourse.getId());
        verify(mockLogic, never()).getStudentsForCourse(stubCourse.getId());

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

        verifyStudentsData(stubStudentListOne, actualStudentsData, Type.STUDENT);
        verify(mockLogic, times(1)).getStudentsByTeamName(stubTeamOne.getName(), stubCourse.getId());
        verify(mockLogic, never()).getStudentsForCourse(stubCourse.getId());
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

        verifyStudentsData(stubStudentListTwo, actualStudentsData, Type.INSTRUCTOR);
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

        verifyStudentsData(stubStudentListOne, actualStudentsData, Type.STUDENT);
        verify(mockLogic, times(1))
                .getStudentsByTeamName(stubStudentOne.getTeamName(), stubCourse.getId());
        verify(mockLogic, never()).getStudentsForCourse(stubCourse.getId());
    }

    @Test
    void testExecute_adminWithTeamNameParams_success() {
        loginAsAdmin();
        when(mockLogic.getStudentsByTeamName(stubStudentOne.getTeam().getName(),
                stubStudentOne.getCourse().getId())).thenReturn(stubStudentListOne);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();

        verifyStudentsData(stubStudentListOne, actualStudentsData, Type.STUDENT);
        verify(mockLogic, times(1))
                .getStudentsByTeamName(stubStudentOne.getTeamName(), stubCourse.getId());
        verify(mockLogic, never()).getStudentsForCourse(stubCourse.getId());
    }

    @Test
    void testExecute_unregisteredLoggedInUser_success() {
        loginAsUnregistered("id");
        when(mockLogic.getStudentsByTeamName(stubStudentOne.getTeam().getName(),
                stubStudentOne.getCourse().getId())).thenReturn(stubStudentListOne);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();

        verifyStudentsData(stubStudentListOne, actualStudentsData, Type.STUDENT);
        verify(mockLogic, times(1))
                .getStudentsByTeamName(stubStudentOne.getTeamName(), stubCourse.getId());
        verify(mockLogic, never()).getStudentsForCourse(stubCourse.getId());
    }

    private void verifyStudentsData(List<Student> expectedStudents, StudentsData actualStudentsData, Type type) {
        assertEquals(expectedStudents.size(), actualStudentsData.getStudents().size());

        for (int i = 0; i < expectedStudents.size(); i++) {
            assertEquals(expectedStudents.get(i).getCourseId(), actualStudentsData.getStudents().get(i).getCourseId());
            assertEquals(expectedStudents.get(i).getTeamName(), actualStudentsData.getStudents().get(i).getTeamName());
            assertEquals(expectedStudents.get(i).getSectionName(), actualStudentsData.getStudents().get(i).getSectionName());
            assertEquals(expectedStudents.get(i).getEmail(), actualStudentsData.getStudents().get(i).getEmail());
            assertEquals(expectedStudents.get(i).getName(), actualStudentsData.getStudents().get(i).getName());
            assertEquals(expectedStudents.get(i).getGoogleId(), actualStudentsData.getStudents().get(i).getGoogleId());
            assertNull(actualStudentsData.getStudents().get(i).getKey());
            if (type == Type.INSTRUCTOR) {
                assertEquals(expectedStudents.get(i).getComments(), actualStudentsData.getStudents().get(i).getComments());
            } else {
                assertNull(actualStudentsData.getStudents().get(i).getJoinState());
                assertNull(actualStudentsData.getStudents().get(i).getComments());
            }
        }
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
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(),
                stubInstructorWithAllPrivileges.getGoogleId());
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithAllPrivileges.getGoogleId());
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

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(),
                stubInstructorWithoutPrivileges.getGoogleId());
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithoutPrivileges.getGoogleId());
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
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(),
                stubInstructorWithOnlyViewSectionPrivileges.getGoogleId());
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithOnlyViewSectionPrivileges.getGoogleId());
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
        verify(mockLogic, times(1)).getStudentByGoogleId(stubCourse.getId(),
                stubStudentOne.getGoogleId());
        verify(mockLogic, never()).getInstructorByGoogleId(stubCourse.getId(),
                stubStudentOne.getGoogleId());
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

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, times(1)).getStudentByGoogleId(stubCourse.getId(),
                stubStudentOne.getGoogleId());
        verify(mockLogic, never()).getInstructorByGoogleId(stubCourse.getId(),
                stubStudentOne.getGoogleId());
    }

    @Test
    void testSpecificAccessControl_userNotLoggedIn_cannotAccess() {
        String[] params1 = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubTeamOne.getName(),
        };
        verifyCannotAccess(params1);
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(), null);
        verify(mockLogic, never()).getInstructorByGoogleId(stubCourse.getId(), null);

        String[] params2 = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
        };
        verifyCannotAccess(params2);

        verify(mockLogic, never()).getInstructorByGoogleId(stubStudentOne.getCourseId(), null);
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(), null);
    }

    @Test
    void testSpecificAccessControl_notStudentNotInstructor_cannotAccess() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentTwo.getTeam().getName(),
        };
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, times(1)).getStudentByGoogleId(stubCourse.getId(), "app_admin@gmail.com");
        verify(mockLogic, never()).getInstructorByGoogleId(stubCourse.getId(), null);
    }

    @Test
    void testSpecificAccessControl_loginAsUnregisteredValidStudent_canAccess() {
        loginAsUnregistered("unregistered-student");
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), "unregistered-student")).thenReturn(stubStudentOne);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        verifyCanAccess(params);

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, times(1)).getStudentByGoogleId(stubCourse.getId(), "unregistered-student");
        verify(mockLogic, never()).getInstructorByGoogleId(stubCourse.getId(), "unregistered-student");
    }

    @Test
    void testSpecificAccessControl_loginAsUnregisteredInvalidStudent_cannotAccess() {
        loginAsUnregistered("unregistered-student");
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), "unregistered-student")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, times(1)).getStudentByGoogleId(stubCourse.getId(), "unregistered-student");
        verify(mockLogic, never()).getInstructorByGoogleId(stubCourse.getId(), "unregistered-student");
    }

    @Test
    void testSpecificAccessControl_loginAsUnregisteredValidInstructor_canAccess() {
        loginAsUnregistered("unregistered-instructor");
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), "unregistered-instructor"))
                .thenReturn(stubInstructorWithAllPrivileges);
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCanAccess(params);

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(), "unregistered-instructor");
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(), "unregistered-instructor");
    }

    @Test
    void testSpecificAccessControl_loginAsUnregisteredInvalidInstructor_canAccess() {
        loginAsUnregistered("unregistered-instructor");
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), "unregistered-instructor")).thenReturn(null);
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(), "unregistered-instructor");
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(), "unregistered-instructor");
    }

    @Test
    void testSpecificAccessControl_invalidParams_throwsInvalidHttpParameterException() {
        loginAsInstructor(stubInstructorWithAllPrivileges.getGoogleId());

        String[] params = {};
        verifyHttpParameterFailure(params);

        String[] params2 = {
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        verifyHttpParameterFailure(params2);
    }

    @Test
    void testSpecificAccessControl_anotherCourseFromInstructor_cannotAccess() {
        loginAsInstructor(stubInstructorWithAllPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId("another-course-id", stubInstructorWithAllPrivileges.getGoogleId()))
                .thenReturn(null);
        when(mockLogic.getCourse("another-course-id")).thenReturn(stubCourse);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "another-course-id",
        };
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, never()).getStudentByGoogleId("another-course-id",
                stubInstructorWithAllPrivileges.getGoogleId());
        verify(mockLogic, times(1)).getInstructorByGoogleId("another-course-id",
                stubInstructorWithAllPrivileges.getGoogleId());
    }

    @Test
    void testSpecificAccessControl_invalidCourse_cannotAccess() {
        loginAsInstructor(stubInstructorWithAllPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId("invalid-course-id", stubInstructorWithAllPrivileges.getGoogleId()))
                .thenReturn(null);
        when(mockLogic.getCourse("invalid-course-id")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
        };
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, never()).getStudentByGoogleId("invalid-course-id",
                stubInstructorWithAllPrivileges.getGoogleId());
        verify(mockLogic, times(1)).getInstructorByGoogleId("invalid-course-id",
                stubInstructorWithAllPrivileges.getGoogleId());
    }
}
