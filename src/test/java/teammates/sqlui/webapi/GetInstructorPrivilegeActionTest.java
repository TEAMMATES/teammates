package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetInstructorPrivilegeAction;

/**
 * SUT: {@link GetInstructorPrivilegeAction}.
 */
public class GetInstructorPrivilegeActionTest extends BaseActionTest<GetInstructorPrivilegeAction> {
    private static String feedbackSessionName = "test-feedback-session";
    private static String studentSectionName = "test-section";
    private Instructor testInstructor1OfCourse1;
    private Instructor testInstructor2OfCourse1;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_PRIVILEGE;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        Course c1 = getTypicalCourse();
        testInstructor1OfCourse1 = getTypicalInstructor();
        testInstructor2OfCourse1 = getTypicalInstructor();
        testInstructor1OfCourse1.setCourse(c1);
        testInstructor1OfCourse1.setGoogleId("user-googleId-1");
        testInstructor2OfCourse1.setCourse(c1);
        testInstructor2OfCourse1.setGoogleId("user-googleId-2");

        InstructorPrivileges privileges = testInstructor1OfCourse1.getPrivileges();
        // update section privilege for testing purpose.

        // course level privilege
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE, true);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION, false);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, false);

        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);

        privileges.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, false);

        // section level privilege
        privileges.updatePrivilege(studentSectionName,
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        privileges.updatePrivilege(studentSectionName,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);

        // session level privilege
        privileges.updatePrivilege(studentSectionName, feedbackSessionName,
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        privileges.updatePrivilege(studentSectionName, feedbackSessionName,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);
        privileges.updatePrivilege(studentSectionName, feedbackSessionName,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);

        testInstructor1OfCourse1.setPrivileges(privileges);

        testInstructor1OfCourse1.setRole(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
    }

    @Test
    void testAccessControl_unregisteredUsers_cannotAccess() {
        logoutUser();
        loginAsUnregistered(Const.ParamsNames.USER_ID);

        String[] submissionParams = { Const.ParamsNames.COURSE_ID, "course_id" };
        verifyCannotAccess(submissionParams);
    }

    @Test
    void testAccessControl_students_cannotAccess() {
        loginAsStudent(Const.ParamsNames.STUDENT_ID);

        String[] submissionParams = { Const.ParamsNames.COURSE_ID, "course_id" };
        verifyCannotAccess(submissionParams);
    }

    @Test
    void testAccessControl_instructorsOfDifferentCourses_cannotAccess() {
        loginAsInstructor(Const.ParamsNames.INSTRUCTOR_ID);

        when(mockLogic.getInstructorByGoogleId(any(), any())).thenReturn(null);

        String[] submissionParams = { Const.ParamsNames.COURSE_ID, "course_id" };
        verifyCannotAccess(submissionParams);
    }

    @Test
    void testAccessControl_instructorsOfTheSameCourse_canAccess() {
        loginAsInstructor(Const.ParamsNames.INSTRUCTOR_ID);

        String[] submissionParams = { Const.ParamsNames.COURSE_ID, "course_id" };
        verifyCannotAccess(submissionParams);
    }

    @Test
    void testAccessControl_admin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    void testExecute_fetchPrivilegeOfNonExistInstructor_shouldFail() {
        loginAsInstructor(testInstructor1OfCourse1.getGoogleId());

        // course id is used for instructor identify verification here.
        String[] invalidInstructorParams = {
                Const.ParamsNames.COURSE_ID, testInstructor1OfCourse1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_ID, "invalidId",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(invalidInstructorParams);
        assertEquals("Instructor does not exist.", enfe.getMessage());
    }

    @Test
    void testExecute_fetchPrivilegeOfAnotherInstructorByEmail_shouldSucceed() {
        loginAsInstructor(testInstructor2OfCourse1.getGoogleId());

        // course id is used for instructor identify verification here.
        String[] anotherInstructorParams = {
                Const.ParamsNames.COURSE_ID, testInstructor2OfCourse1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, testInstructor1OfCourse1.getEmail(),
        };

        when(mockLogic.getInstructorForEmail(testInstructor2OfCourse1.getCourseId(), testInstructor1OfCourse1.getEmail()))
                .thenReturn(testInstructor1OfCourse1);

        GetInstructorPrivilegeAction a = getAction(anotherInstructorParams);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();
        InstructorPrivileges privileges = response.getPrivileges();
        assertEqualToTestInstructorPrivileges(privileges);
    }

    @Test
    protected void testExecute_fetchPrivilegeOfAnotherInstructor_shouldSucceed() {
        loginAsInstructor(testInstructor2OfCourse1.getGoogleId());

        // course id is used for instructor identify verification here.
        String[] anotherInstructorParams = {
                Const.ParamsNames.COURSE_ID, testInstructor2OfCourse1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_ID, testInstructor1OfCourse1.getGoogleId(),
        };

        when(mockLogic.getInstructorByGoogleId(testInstructor2OfCourse1.getCourseId(),
                testInstructor1OfCourse1.getGoogleId())).thenReturn(testInstructor1OfCourse1);

        GetInstructorPrivilegeAction a = getAction(anotherInstructorParams);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();
        InstructorPrivileges privileges = response.getPrivileges();

        assertEqualToTestInstructorPrivileges(privileges);
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        loginAsInstructor(testInstructor1OfCourse1.getGoogleId());

        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_fetchPrivilegeOfSelf_shouldSucceed() {
        loginAsInstructor(testInstructor1OfCourse1.getGoogleId());

        String[] courseIdParam = {
                Const.ParamsNames.COURSE_ID, testInstructor1OfCourse1.getCourseId(),
        };

        when(mockLogic.getInstructorByGoogleId(testInstructor1OfCourse1.getCourseId(),
                testInstructor1OfCourse1.getGoogleId())).thenReturn(testInstructor1OfCourse1);

        GetInstructorPrivilegeAction a = getAction(courseIdParam);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();
        InstructorPrivileges privileges = response.getPrivileges();

        assertEqualToTestInstructorPrivileges(privileges);
    }

    private void assertEqualToTestInstructorPrivileges(InstructorPrivileges privileges) {
        InstructorPermissionSet courseLevelPrivilege = privileges.getCourseLevelPrivileges();

        assertTrue(courseLevelPrivilege.isCanModifyCourse());
        assertTrue(courseLevelPrivilege.isCanModifyStudent());
        assertFalse(courseLevelPrivilege.isCanModifyInstructor());
        assertFalse(courseLevelPrivilege.isCanModifySession());
        assertFalse(courseLevelPrivilege.isCanViewStudentInSections());
        assertFalse(courseLevelPrivilege.isCanModifySessionCommentsInSections());
        assertFalse(courseLevelPrivilege.isCanViewSessionInSections());
        assertFalse(courseLevelPrivilege.isCanSubmitSessionInSections());

        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        InstructorPermissionSet sectionLevelPrivilege = privileges.getSectionLevelPrivileges().get(studentSectionName);

        assertFalse(sectionLevelPrivilege.isCanModifyCourse());
        assertFalse(sectionLevelPrivilege.isCanModifyStudent());
        assertFalse(sectionLevelPrivilege.isCanModifyInstructor());
        assertFalse(sectionLevelPrivilege.isCanModifySession());

        assertTrue(sectionLevelPrivilege.isCanViewStudentInSections());
        assertTrue(sectionLevelPrivilege.isCanModifySessionCommentsInSections());
        assertFalse(sectionLevelPrivilege.isCanViewSessionInSections());
        assertFalse(sectionLevelPrivilege.isCanSubmitSessionInSections());

        assertEquals(1, privileges.getSessionLevelPrivileges().size());
        assertEquals(1, privileges.getSessionLevelPrivileges().get(studentSectionName).size());
        InstructorPermissionSet sessionLevelPrivilege =
                privileges.getSessionLevelPrivileges().get(studentSectionName).get(feedbackSessionName);

        assertFalse(sessionLevelPrivilege.isCanModifyCourse());
        assertFalse(sessionLevelPrivilege.isCanModifyStudent());
        assertFalse(sessionLevelPrivilege.isCanModifyInstructor());
        assertFalse(sessionLevelPrivilege.isCanModifySession());
        assertFalse(sessionLevelPrivilege.isCanViewStudentInSections());

        assertTrue(sessionLevelPrivilege.isCanModifySessionCommentsInSections());
        assertTrue(sessionLevelPrivilege.isCanViewSessionInSections());
        assertTrue(sessionLevelPrivilege.isCanSubmitSessionInSections());
    }
}
