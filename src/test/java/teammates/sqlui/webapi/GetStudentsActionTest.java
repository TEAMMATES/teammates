package teammates.sqlui.webapi;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
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
    private Instructor stubInstructorWithOnlyViewPrivilegesForDifferentSection;
    private Instructor stubInstructorWithCourseLevelPrivilege;
    private Course stubCourse;
    private Team stubTeamOne;
    private Student stubStudentOne;
    private Student stubStudentTwo;
    private List<Student> stubStudentListSectionOneTeamOne;
    private List<Student> stubStudentListSectionOne;
    private List<Student> stubStudentListAll;
    private List<Student> stubStudentListSectionTwo;
    private InstructorPrivileges sectionPrivilegesOnly;

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
        stubCourse = getTypicalCourse();

        // Instructor with co-owner privileges (course and section privileges)
        stubInstructorWithAllPrivileges = getTypicalInstructor();

        // Instructor without any privileges
        InstructorPrivileges customInstructorPrivileges1 =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        stubInstructorWithoutPrivileges = getTypicalInstructor();
        stubInstructorWithoutPrivileges.setPrivileges(customInstructorPrivileges1);

        // Instructor with only privilege to view students in sections they are in
        sectionPrivilegesOnly =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        sectionPrivilegesOnly.updatePrivilege("section-1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        stubInstructorWithOnlyViewSectionPrivileges = getTypicalInstructor();
        stubInstructorWithOnlyViewSectionPrivileges.setPrivileges(sectionPrivilegesOnly);

        // Instructor with privilege to view students in sections other than the one the student is in
        InstructorPrivileges customInstructorPrivileges2 =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        customInstructorPrivileges2.updatePrivilege("random-1",
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        stubInstructorWithOnlyViewPrivilegesForDifferentSection = getTypicalInstructor();
        stubInstructorWithOnlyViewPrivilegesForDifferentSection.setPrivileges(customInstructorPrivileges2);

        stubInstructorWithOnlyViewSectionPrivileges.setAccount(getTypicalAccount());
        stubInstructorWithoutPrivileges.setAccount(getTypicalAccount());
        stubInstructorWithAllPrivileges.setAccount(getTypicalAccount());

        // Instructor with privilege to view students in sections at course level
        InstructorPrivileges customInstructorPrivileges3 =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        customInstructorPrivileges3.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        stubInstructorWithCourseLevelPrivilege = getTypicalInstructor();
        stubInstructorWithCourseLevelPrivilege.setPrivileges(customInstructorPrivileges3);

        // Section one
        Section stubSection = new Section(stubCourse, "section-1");
        stubTeamOne = new Team(stubSection, "team-1");
        Team stubTeamTwo = new Team(stubSection, "team-2");
        stubStudentListSectionOneTeamOne = new ArrayList<>();
        stubStudentListSectionOne = new ArrayList<>();

        //Section two
        Section stubSectionTwo = new Section(stubCourse, "section-2");
        Team stubTeamThree = new Team(stubSectionTwo, "team-3");
        stubStudentListSectionTwo = new ArrayList<>();

        stubStudentListAll = new ArrayList<>();

        // Students in section one
        stubStudentOne = getTypicalStudent();
        stubStudentOne.setTeam(stubTeamOne);
        stubStudentTwo = new Student(stubCourse, "student-2", "student2@teammates.tmt", "comments", stubTeamTwo);
        Student stubStudentThree = new Student(stubCourse, "student-3", "student3@teammates.tmt", "comments", stubTeamTwo);
        stubStudentListSectionOne.add(stubStudentOne);
        stubStudentListSectionOne.add(stubStudentTwo);
        stubStudentListSectionOne.add(stubStudentThree);
        stubStudentListSectionOneTeamOne.add(stubStudentOne);

        // Students in section two
        Student stubStudentFour = new Student(stubCourse, "student-4", "student4@teammates.tmt", "comments", stubTeamThree);
        stubStudentListSectionTwo.add(stubStudentFour);

        // Students in the entire Course
        stubStudentListAll.add(stubStudentOne);
        stubStudentListAll.add(stubStudentTwo);
        stubStudentListAll.add(stubStudentThree);
        stubStudentListAll.add(stubStudentFour);
        reset(mockLogic);
        logoutUser();
    }

    /**
     * Enum to represent the type of user.
     */
    enum Type {
        INSTRUCTOR, STUDENT
    }

    @Test
    void testExecute_invalidParameters_throwsInvalidHttpParameterException() {
        String[] params1 = {};
        verifyHttpParameterFailure(params1);
        String[] params2 = {
                Const.ParamsNames.TEAM_NAME, stubTeamOne.getName(),
        };
        verifyHttpParameterFailure(params2);
    }

    @Test
     void testExecute_instructorWithPermission_success() {
        loginAsInstructor(stubInstructorWithAllPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithAllPrivileges.getGoogleId()))
                .thenReturn(stubInstructorWithAllPrivileges);
        when(mockLogic.getStudentsForCourse(stubCourse.getId())).thenReturn(stubStudentListAll);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();

        verifyStudentsData(stubStudentListAll, actualStudentsData, Type.INSTRUCTOR);
        verify(mockLogic, never()).getStudentsByTeamName(null, stubCourse.getId());
    }

    @Test
    void testExecute_instructorWithSameSectionPrivilegesAsStudents_success() {
        loginAsInstructor(stubInstructorWithOnlyViewSectionPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithOnlyViewSectionPrivileges
                .getGoogleId())).thenReturn(stubInstructorWithOnlyViewSectionPrivileges);
        when(mockLogic.getStudentsForCourse(stubCourse.getId())).thenReturn(stubStudentListAll);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        GetStudentsAction action1 = getAction(params);
        JsonResult jsonResult1 = getJsonResult(action1);
        StudentsData actualStudentsData1 = (StudentsData) jsonResult1.getOutput();

        // Section two students are not in the StudentsData
        verifyStudentsData(stubStudentListSectionOne, actualStudentsData1, Type.INSTRUCTOR);

        sectionPrivilegesOnly.updatePrivilege("section-1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        sectionPrivilegesOnly.updatePrivilege("section-2", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        GetStudentsAction action2 = getAction(params);
        JsonResult jsonResult2 = getJsonResult(action2);
        StudentsData actualStudentsData2 = (StudentsData) jsonResult2.getOutput();

        // Section One students are not in the StudentsData
        verifyStudentsData(stubStudentListSectionTwo, actualStudentsData2, Type.INSTRUCTOR);

        verify(mockLogic, never()).getStudentsByTeamName(null, stubCourse.getId());
        verify(mockLogic, times(2)).getStudentsForCourse(stubCourse.getId());
    }

    @Test
    void testExecute_instructorWithOnlyViewSectionCourseLevelPrivilege_success() {
        loginAsInstructor(stubInstructorWithCourseLevelPrivilege.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithCourseLevelPrivilege.getGoogleId()))
                .thenReturn(stubInstructorWithCourseLevelPrivilege);
        when(mockLogic.getStudentsForCourse(stubCourse.getId())).thenReturn(stubStudentListAll);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();

        verifyStudentsData(stubStudentListAll, actualStudentsData, Type.INSTRUCTOR);
        verify(mockLogic, never()).getStudentsByTeamName(null, stubCourse.getId());
    }

    @Test
    void testExecute_instructorWithDifferentSectionPrivilegesAsStudents_emptyList() {
        loginAsInstructor(stubInstructorWithOnlyViewPrivilegesForDifferentSection.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithOnlyViewPrivilegesForDifferentSection.getGoogleId()))
                .thenReturn(stubInstructorWithOnlyViewPrivilegesForDifferentSection);
        when(mockLogic.getStudentsForCourse(stubCourse.getId())).thenReturn(stubStudentListAll);

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
    void testExecute_student_success() {
        loginAsStudent(stubStudentOne.getGoogleId());
        when(mockLogic.getStudentsByTeamName(stubStudentOne.getTeam().getName(),
                stubStudentOne.getCourse().getId())).thenReturn(stubStudentListSectionOneTeamOne);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();

        verifyStudentsData(stubStudentListSectionOneTeamOne, actualStudentsData, Type.STUDENT);
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
    void testAccessControl_instructor_course_onlySameCourseWithViewSectionPrivilege() {
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                stubCourse,
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS,
                Const.ParamsNames.COURSE_ID, stubCourse.getId()
        );
    }

    @Test
    void testAccessControl_instructorWithTeamParams_cannotAccess() {
        verifyInstructorsCannotAccess(
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.TEAM_NAME, stubTeamOne.getName()
        );
    }

    @Test
    void testAccessControl_student_sameTeam_canAccess() {
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), "student-googleId"))
                .thenReturn(stubStudentOne);

        verifyStudentsCanAccess(
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME,  stubStudentOne.getTeam().getName()
        );
    }

    @Test
    void testAccessControl_student_otherTeam_cannotAccess() {
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), "student-googleId"))
                .thenReturn(stubStudentOne);
        verifyStudentsCannotAccess(
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME,  stubStudentTwo.getTeam().getName()
        );
    }

    @Test
    void testAccessControl_student_otherCourse_cannotAccess() {
        when(mockLogic.getStudentByGoogleId("another-course-id", "student-googleId"))
                .thenReturn(null);
        verifyStudentsCannotAccess(
                Const.ParamsNames.COURSE_ID, "another-course-id",
                Const.ParamsNames.TEAM_NAME,  stubStudentTwo.getTeam().getName()
        );
    }

    @Test
    void testAccessControl_userNotLoggedIn_cannotAccess() {
        verifyWithoutLoginCannotAccess(
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubTeamOne.getName()
        );
        verifyWithoutLoginCannotAccess(
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId()
        );
    }

    @Test
    void testAccessControl_admin_teamParams_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentTwo.getTeam().getName(),
        };
        verifyAdminsCannotAccess(params);
        verifyWithoutLoginCannotAccess(params);
    }

    @Test
    void testAccessControl_unregistered_validStudent_canAccess() {
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), "unregistered-googleId"))
                .thenReturn(stubStudentOne);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME,  stubStudentOne.getTeam().getName(),
        };
        verifyUnregisteredCanAccess(params);
        verifyWithoutLoginCannotAccess(params);
    }

    @Test
    void testAccessControl_unregistered_invalidStudent_cannotAccess() {
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), "unregistered-googleId"))
                .thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME,  stubStudentOne.getTeam().getName(),
        };
        verifyUnregisteredCannotAccess(params);
        verifyWithoutLoginCannotAccess(params);
    }

    @Test
    void testAccessControl_unregistered_validInstructor_canAccess() {
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), "unregistered-googleId"))
                .thenReturn(stubInstructorWithAllPrivileges);
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };

        verifyUnregisteredCanAccess(params);
        verifyWithoutLoginCannotAccess(params);
    }

    @Test
    void testAccessControl_unregistered_invalidInstructor_cannotAccess() {
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), "unregistered-googleId"))
                .thenReturn(null);
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyUnregisteredCannotAccess(params);
        verifyWithoutLoginCannotAccess(params);
    }

    @Test
    void testAccessControl_invalidParams_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
        verifyHttpParameterFailure(
                Const.ParamsNames.TEAM_NAME, stubTeamOne.getName()
        );
    }

    @Test
    void testAccessControl_invalidCourse_cannotAccess() {
        loginAsInstructor(stubInstructorWithAllPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(
                "invalid-course-id",
                stubInstructorWithAllPrivileges.getGoogleId()))
                .thenReturn(stubInstructorWithAllPrivileges);
        when(mockLogic.getCourse("invalid-course-id")).thenReturn(null);
        verifyCannotAccess(
                Const.ParamsNames.COURSE_ID, "invalid-course-id"
        );
    }

    @Test
    void testAccessControl_instructorDifferentCourseAsStudent_cannotAccess() {
        verifyInstructorsOfOtherCoursesCannotAccess(
                Const.ParamsNames.COURSE_ID, stubCourse.getId()
        );
    }
}
