package teammates.sqlui.webapi;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;
import teammates.ui.webapi.GetInstructorsAction;

/**
 * SUT: {@link GetInstructorsAction}.
 */
public class GetInstructorsActionTest extends BaseActionTest<GetInstructorsAction> {
    private Course stubCourse;
    private Instructor stubInstructorWithPermission;
    private Instructor stubInstructorWithoutPermission;
    private Instructor stubInstructorWithOnlyModifyInstructorPrivilege;
    private List<Instructor> stubInstructors;
    private InstructorsData expectedInstructorsData;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTORS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        stubCourse = getTypicalCourse();
        stubInstructorWithPermission = getTypicalInstructor();
        Account stubAccount1 = getTypicalAccount();
        Account stubAccount2 = getTypicalAccount();
        Account stubAccount3 = getTypicalAccount();
        stubAccount2.setId(new UUID(100, 20));
        stubAccount3.setId(new UUID(100, 30));

        InstructorPermissionRole customRole = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        InstructorPrivileges customInstructorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        stubInstructorWithoutPermission = new Instructor(stubCourse, "instructor-1-name", "valid1@teammates.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, customRole, customInstructorPrivileges);
        InstructorPrivileges modifyInstructorPrivilegeOnly =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        modifyInstructorPrivilegeOnly.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);
        stubInstructorWithOnlyModifyInstructorPrivilege = new Instructor(stubCourse, "instructor-2-name",
                "valid2@teammates.tmt", false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR,
                customRole, modifyInstructorPrivilegeOnly);

        stubInstructorWithPermission.setAccount(stubAccount1);
        stubInstructorWithoutPermission.setAccount(stubAccount2);
        stubInstructorWithOnlyModifyInstructorPrivilege.setAccount(stubAccount3);

        stubInstructors = new ArrayList<>();
        stubInstructors.add(stubInstructorWithPermission);
        stubInstructors.add(stubInstructorWithoutPermission);
        stubInstructors.add(stubInstructorWithOnlyModifyInstructorPrivilege);
        stubInstructors.forEach(stubInstructor -> { stubInstructor.setDisplayedToStudents(true); });

        expectedInstructorsData = new InstructorsData(stubInstructors);
        expectedInstructorsData.getInstructors().get(0).setKey(stubInstructorWithPermission.getRegKey());
        expectedInstructorsData.getInstructors().get(1).setKey(stubInstructorWithoutPermission.getRegKey());
        expectedInstructorsData.getInstructors().get(2).setKey(stubInstructorWithOnlyModifyInstructorPrivilege.getRegKey());
        expectedInstructorsData.getInstructors().get(0).setGoogleId(stubInstructorWithPermission.getGoogleId());
        expectedInstructorsData.getInstructors().get(1).setGoogleId(stubInstructorWithoutPermission.getGoogleId());
        expectedInstructorsData.getInstructors().get(2).setGoogleId(stubInstructorWithOnlyModifyInstructorPrivilege
                .getGoogleId());

        reset(mockLogic);
    }

    @Test
    void testExecute_invalidParams_throwsInvalidHttpParameterException() {
        loginAsInstructor(stubInstructorWithPermission.getGoogleId());
        String[] params1 = {};
        verifyHttpParameterFailure(params1);

        String[] params2 = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.INTENT, "Unknown",
        };
        verifyHttpParameterFailure(params2);

        String[] params3 = {
                Const.ParamsNames.INTENT, "FULL_DETAIL",
        };
        verifyHttpParameterFailure(params3);
    }

    @Test
    void testExecute_withoutIntentInstructorsDisplayedToStudents_shouldReturnPartialData() {
        loginAsStudent("student-id");
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };

        when(mockLogic.getInstructorsByCourse(stubCourse.getId())).thenReturn(stubInstructors);
        GetInstructorsAction action = getAction(params);
        InstructorsData actualInstructorsData = (InstructorsData) getJsonResult(action).getOutput();
        verifyInstructorsData(expectedInstructorsData, actualInstructorsData, true, false, false);
        verify(mockLogic, times(1)).getInstructorsByCourse(stubCourse.getId());
    }

    @Test
    void testExecute_withoutIntentInstructorsNotDisplayedToStudents_shouldReturnNoData() {
        loginAsStudent("student-id");
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };

        when(mockLogic.getInstructorsByCourse(stubCourse.getId())).thenReturn(stubInstructors);
        // set instructors not displayed to students
        stubInstructors.forEach(stubInstructor -> { stubInstructor.setDisplayedToStudents(false); });
        GetInstructorsAction action = getAction(params);
        InstructorsData actualInstructorsData = (InstructorsData) getJsonResult(action).getOutput();
        assertEquals(0, actualInstructorsData.getInstructors().size());
        verify(mockLogic, times(1)).getInstructorsByCourse(stubCourse.getId());
    }

    @Test
    void testExecute_fullDetailIntentAdminLogin_shouldReturnFullDataWithKey() {
        loginAsAdmin();
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.INTENT, "FULL_DETAIL",
        };

        when(mockLogic.getInstructorsByCourse(stubCourse.getId())).thenReturn(stubInstructors);
        GetInstructorsAction action = getAction(params);
        InstructorsData actualInstructorsData = (InstructorsData) getJsonResult(action).getOutput();
        verifyInstructorsData(expectedInstructorsData, actualInstructorsData, false, true, true);
        verify(mockLogic, times(1)).getInstructorsByCourse(stubCourse.getId());
    }

    @Test
    void testExecute_fullDetailIntentInstructorWithPrivileges_shouldReturnFullDataWithoutKey() {
        loginAsInstructor(stubInstructorWithPermission.getGoogleId());
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.INTENT, "FULL_DETAIL",
        };

        when(mockLogic.getInstructorsByCourse(stubCourse.getId())).thenReturn(stubInstructors);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithPermission.getGoogleId()))
                .thenReturn(stubInstructorWithPermission);
        GetInstructorsAction action = getAction(params);
        InstructorsData actualInstructorsData = (InstructorsData) getJsonResult(action).getOutput();
        verifyInstructorsData(expectedInstructorsData, actualInstructorsData, false, false, true);
        verify(mockLogic, times(1)).getInstructorsByCourse(stubCourse.getId());
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithPermission.getGoogleId());
    }

    @Test
    void testExecute_fullDetailIntentInstructorWithNoPrivileges_shouldReturnPartialData() {
        loginAsInstructor(stubInstructorWithoutPermission.getGoogleId());
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.INTENT, "FULL_DETAIL",
        };

        when(mockLogic.getInstructorsByCourse(stubCourse.getId())).thenReturn(stubInstructors);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithoutPermission.getGoogleId()))
                .thenReturn(stubInstructorWithoutPermission);
        GetInstructorsAction action = getAction(params);
        InstructorsData actualInstructorsData = (InstructorsData) getJsonResult(action).getOutput();
        verifyInstructorsData(expectedInstructorsData, actualInstructorsData, false, false, false);
        verify(mockLogic, times(1)).getInstructorsByCourse(stubCourse.getId());
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithoutPermission.getGoogleId());
    }

    @Test
    void testExecute_fullDetailIntentInstructorWithModifyPrivilegesOnly_shouldReturnFullDataWithoutKey() {
        loginAsInstructor(stubInstructorWithOnlyModifyInstructorPrivilege.getGoogleId());
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.INTENT, "FULL_DETAIL",
        };

        when(mockLogic.getInstructorsByCourse(stubCourse.getId())).thenReturn(stubInstructors);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithOnlyModifyInstructorPrivilege
                .getGoogleId())).thenReturn(stubInstructorWithOnlyModifyInstructorPrivilege);
        GetInstructorsAction action = getAction(params);
        InstructorsData actualInstructorsData = (InstructorsData) getJsonResult(action).getOutput();
        verifyInstructorsData(expectedInstructorsData, actualInstructorsData, false, false, true);
        verify(mockLogic, times(1)).getInstructorsByCourse(stubCourse.getId());
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithOnlyModifyInstructorPrivilege.getGoogleId());
    }

    private void verifyInstructorsData(InstructorsData expectedInstructorsData, InstructorsData actualInstructorsData,
                                       boolean isNullIntent, boolean isAdmin, boolean isGoogleIdSetForFullDetail) {
        List<InstructorData> expectedInstructors = expectedInstructorsData.getInstructors();
        List<InstructorData> actualInstructors = actualInstructorsData.getInstructors();
        assertEquals(expectedInstructors.size(), actualInstructors.size());
        for (int i = 0; i < expectedInstructors.size(); i++) {
            if (isNullIntent) {
                assertNull(actualInstructors.get(i).getGoogleId());
                assertNull(actualInstructors.get(i).getJoinState());
                assertNull(actualInstructors.get(i).getIsDisplayedToStudents());
                assertNull(actualInstructors.get(i).getRole());
                assertNull(actualInstructors.get(i).getKey());
                assertEquals(expectedInstructors.get(i).getName(), actualInstructors.get(i).getName());
                assertEquals(expectedInstructors.get(i).getEmail(), actualInstructors.get(i).getEmail());
                assertEquals(expectedInstructors.get(i).getCourseId(), actualInstructors.get(i).getCourseId());
                assertEquals(expectedInstructors.get(i).getInstitute(), actualInstructors.get(i).getInstitute());
            } else {
                assertNotNull(actualInstructors.get(i).getJoinState());
                assertNotNull(actualInstructors.get(i).getIsDisplayedToStudents());
                assertNotNull(actualInstructors.get(i).getRole());

                if (isAdmin) {
                    assertNotNull(actualInstructors.get(i).getKey());
                    assertEquals(expectedInstructors.get(i).getKey(), actualInstructors.get(i).getKey());
                } else {
                    assertNull(actualInstructors.get(i).getKey());
                }

                if (isGoogleIdSetForFullDetail) {
                    assertNotNull(actualInstructors.get(i).getGoogleId());
                    assertEquals(expectedInstructors.get(i).getGoogleId(), actualInstructors.get(i).getGoogleId());
                } else {
                    assertNull(actualInstructors.get(i).getGoogleId());
                }
                assertEquals(expectedInstructors.get(i).getName(), actualInstructors.get(i).getName());
                assertEquals(expectedInstructors.get(i).getEmail(), actualInstructors.get(i).getEmail());
                assertEquals(expectedInstructors.get(i).getCourseId(), actualInstructors.get(i).getCourseId());
                assertEquals(expectedInstructors.get(i).getDisplayedToStudentsAs(),
                        actualInstructors.get(i).getDisplayedToStudentsAs());
                assertEquals(expectedInstructors.get(i).getRole(), actualInstructors.get(i).getRole());
                assertEquals(expectedInstructors.get(i).getJoinState(), actualInstructors.get(i).getJoinState());
                assertEquals(expectedInstructors.get(i).getInstitute(), actualInstructors.get(i).getInstitute());
            }
        }
    }

    @Test
    void testSpecificAccessControl_missingParams_throwsInvalidHttpParameterException() {
        loginAsInstructor(stubInstructorWithPermission.getGoogleId());
        String[] params1 = {};
        verifyHttpParameterFailureAcl(params1);

        String[] params2 = {
                Const.ParamsNames.INTENT, "FULL_DETAIL",
        };
        verifyHttpParameterFailureAcl(params2);

        String[] params3 = {
                Const.ParamsNames.INTENT, "random-intent",
        };
        verifyHttpParameterFailureAcl(params3);
    }

    @Test
    void testSpecificAccessControl_invalidCourseParams_throwsEntityNotFoundException() {
        loginAsInstructor(stubInstructorWithPermission.getGoogleId());
        when(mockLogic.getCourse("invalid-course-id")).thenReturn(null);
        String[] params1 = {
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
        };
        verifyEntityNotFoundAcl(params1);

        String[] params2 = {
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
                Const.ParamsNames.INTENT, "FULL_DETAIL",
        };
        verifyEntityNotFoundAcl(params2);

        String[] params3 = {
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
                Const.ParamsNames.INTENT, "invalid-intent",
        };
        verifyEntityNotFoundAcl(params3);
    }

    @Test
    void testSpecificAccessControl_unknownIntent_throwsInvalidHttpParameterException() {
        loginAsInstructor(stubInstructorWithPermission.getGoogleId());
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.INTENT, "unknown-intent",
        };
        verifyHttpParameterFailureAcl(params);
    }

    @Test
    void testSpecificAccessControl_unregisteredUserLogin_cannotAccess() {
        loginAsUnregistered("unregistered");
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        String[] params1 = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), "unregistered")).thenReturn(null);
        verifyCannotAccess(params1);

        String[] params2 = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.INTENT, "FULL_DETAIL",
        };
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), "unregistered")).thenReturn(null);
        verifyCannotAccess(params2);
    }

    @Test
    void testSpecificAccessControl_fullDetailIntentInstructorLoginSameCourse_canAccess() {
        loginAsInstructor(stubInstructorWithPermission.getGoogleId());
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.INTENT, "FULL_DETAIL",
        };
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithPermission.getGoogleId()))
                .thenReturn(stubInstructorWithPermission);
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_adminLogin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    void testSpecificAccessControl_noIntentStudentLoginSameCourse_canAccess() {
        Student stubStudent = getTypicalStudent();
        stubStudent.setAccount(getTypicalAccount());

        loginAsStudent(stubStudent.getGoogleId());
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_noIntentStudentLoginDifferentCourse_cannotAccess() {
        Student stubStudent = getTypicalStudent();
        stubStudent.setAccount(getTypicalAccount());

        Student anotherStudent = getTypicalStudent();
        anotherStudent.setAccount(getTypicalAccount());
        anotherStudent.getCourse().setId("course");

        loginAsStudent(stubStudent.getGoogleId());
        when(mockLogic.getCourse("course")).thenReturn(anotherStudent.getCourse());
        String[] params = {
                Const.ParamsNames.COURSE_ID, "course",
        };
        when(mockLogic.getStudentByGoogleId("course", stubStudent.getGoogleId())).thenReturn(null);
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_fullDetailIntentAccessInstructorLoginAnotherCourse_cannotAccess() {
        Instructor anotherInstructor = getTypicalInstructor();
        anotherInstructor.getCourse().setId("course");

        loginAsInstructor(stubInstructorWithPermission.getGoogleId());
        when(mockLogic.getCourse("course")).thenReturn(anotherInstructor.getCourse());
        String[] params = {
                Const.ParamsNames.COURSE_ID, "course",
                Const.ParamsNames.INTENT, "FULL_DETAIL",
        };
        when(mockLogic.getInstructorByGoogleId("course", stubInstructorWithPermission.getGoogleId())).thenReturn(null);
        verifyCannotAccess(params);
    }
}
