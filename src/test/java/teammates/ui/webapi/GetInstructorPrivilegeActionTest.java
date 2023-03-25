package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.InstructorPrivilegeData;

/**
 * SUT: {@link GetInstructorPrivilegeAction}.
 */
public class GetInstructorPrivilegeActionTest extends BaseActionTest<GetInstructorPrivilegeAction> {

    private DataBundle dataBundle;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_PRIVILEGE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void prepareTestData() {
        dataBundle = getTypicalDataBundle();
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String section1 = dataBundle.students.get("student1InCourse1").getSection();
        String session1 = dataBundle.feedbackSessions.get("session1InCourse1").getFeedbackSessionName();
        InstructorPrivileges privileges = instructor1ofCourse1.getPrivileges();
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
        privileges.updatePrivilege(section1,
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        privileges.updatePrivilege(section1,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);

        // session level privilege
        privileges.updatePrivilege(section1, session1,
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        privileges.updatePrivilege(section1, session1,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);
        privileges.updatePrivilege(section1, session1,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);

        instructor1ofCourse1.setPrivileges(privileges);

        instructor1ofCourse1.setRole(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        dataBundle.instructors.put("instructor1OfCourse1", instructor1ofCourse1);
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    protected void testExecute() {
        // see individual tests.
    }

    @Test
    protected void testExecute_fetchPrivilegeOfNonExistInstructor_shouldFail() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Request to fetch privilege of another instructor");
        // course id is used for instructor identify verification here.
        String[] invalidInstructorParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_ID, "invalidid",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(invalidInstructorParams);
        assertEquals("Instructor does not exist.", enfe.getMessage());
    }

    @Test
    protected void testExecute_fetchPrivilegeOfAnotherInstructorByEmail_shouldSucceed() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2ofCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Request to fetch privilege of another instructor");
        // course id is used for instructor identify verification here.
        String[] anotherInstructorParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor2ofCourse1.getEmail(),
        };

        GetInstructorPrivilegeAction a = getAction(anotherInstructorParams);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();
        InstructorPrivileges privileges = response.getPrivileges();
        InstructorPermissionSet courseLevelPrivilege = privileges.getCourseLevelPrivileges();

        assertFalse(courseLevelPrivilege.isCanModifyCourse());
        assertTrue(courseLevelPrivilege.isCanModifyStudent());
        assertTrue(courseLevelPrivilege.isCanModifyInstructor());
        assertTrue(courseLevelPrivilege.isCanModifySession());
        assertTrue(courseLevelPrivilege.isCanViewStudentInSections());
        assertTrue(courseLevelPrivilege.isCanModifySessionCommentsInSections());
        assertTrue(courseLevelPrivilege.isCanViewSessionInSections());
        assertTrue(courseLevelPrivilege.isCanSubmitSessionInSections());

        assertTrue(privileges.getSectionLevelPrivileges().isEmpty());
        assertTrue(privileges.getSessionLevelPrivileges().isEmpty());
    }

    @Test
    protected void testExecute_fetchPrivilegeOfAnotherInstructor_shouldSucceed() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2ofCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Request to fetch privilege of another instructor");
        // course id is used for instructor identify verification here.
        String[] anotherInstructorParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor2ofCourse1.getGoogleId(),
        };

        GetInstructorPrivilegeAction a = getAction(anotherInstructorParams);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();
        InstructorPrivileges privileges = response.getPrivileges();
        InstructorPermissionSet courseLevelPrivilege = privileges.getCourseLevelPrivileges();

        assertFalse(courseLevelPrivilege.isCanModifyCourse());
        assertTrue(courseLevelPrivilege.isCanModifyStudent());
        assertTrue(courseLevelPrivilege.isCanModifyInstructor());
        assertTrue(courseLevelPrivilege.isCanModifySession());
        assertTrue(courseLevelPrivilege.isCanViewStudentInSections());
        assertTrue(courseLevelPrivilege.isCanModifySessionCommentsInSections());
        assertTrue(courseLevelPrivilege.isCanViewSessionInSections());
        assertTrue(courseLevelPrivilege.isCanSubmitSessionInSections());

        assertTrue(privileges.getSectionLevelPrivileges().isEmpty());
        assertTrue(privileges.getSessionLevelPrivileges().isEmpty());
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Not enough parameters");
        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_fetchPrivilegeOfSelf_shouldSucceed() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Request with course id to fetch general privileges");

        String[] courseIdParam = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
        };

        GetInstructorPrivilegeAction a = getAction(courseIdParam);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();
        InstructorPrivileges privileges = response.getPrivileges();
        InstructorPermissionSet courseLevelPrivilege = privileges.getCourseLevelPrivileges();

        assertTrue(courseLevelPrivilege.isCanModifyCourse());
        assertTrue(courseLevelPrivilege.isCanModifyStudent());
        assertFalse(courseLevelPrivilege.isCanModifyInstructor());
        assertFalse(courseLevelPrivilege.isCanModifySession());
        assertFalse(courseLevelPrivilege.isCanViewStudentInSections());
        assertFalse(courseLevelPrivilege.isCanModifySessionCommentsInSections());
        assertFalse(courseLevelPrivilege.isCanViewSessionInSections());
        assertFalse(courseLevelPrivilege.isCanSubmitSessionInSections());

        String section1 = dataBundle.students.get("student1InCourse1").getSection();

        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        InstructorPermissionSet sectionLevelPrivilege = privileges.getSectionLevelPrivileges().get(section1);

        assertFalse(sectionLevelPrivilege.isCanModifyCourse());
        assertFalse(sectionLevelPrivilege.isCanModifyStudent());
        assertFalse(sectionLevelPrivilege.isCanModifyInstructor());
        assertFalse(sectionLevelPrivilege.isCanModifySession());

        assertTrue(sectionLevelPrivilege.isCanViewStudentInSections());
        assertTrue(sectionLevelPrivilege.isCanModifySessionCommentsInSections());
        assertFalse(sectionLevelPrivilege.isCanViewSessionInSections());
        assertFalse(sectionLevelPrivilege.isCanSubmitSessionInSections());

        String session1 = dataBundle.feedbackSessions.get("session1InCourse1").getFeedbackSessionName();

        assertEquals(1, privileges.getSessionLevelPrivileges().size());
        assertEquals(1, privileges.getSessionLevelPrivileges().get(section1).size());
        InstructorPermissionSet sessionLevelPrivilege = privileges.getSessionLevelPrivileges().get(section1).get(session1);

        assertFalse(sessionLevelPrivilege.isCanModifyCourse());
        assertFalse(sessionLevelPrivilege.isCanModifyStudent());
        assertFalse(sessionLevelPrivilege.isCanModifyInstructor());
        assertFalse(sessionLevelPrivilege.isCanModifySession());
        assertFalse(sessionLevelPrivilege.isCanViewStudentInSections());

        assertTrue(sessionLevelPrivilege.isCanModifySessionCommentsInSections());
        assertTrue(sessionLevelPrivilege.isCanViewSessionInSections());
        assertTrue(sessionLevelPrivilege.isCanSubmitSessionInSections());
    }

    @Test
    @Override
    protected void testAccessControl() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
        };

        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdmin(submissionParams);
    }

}
