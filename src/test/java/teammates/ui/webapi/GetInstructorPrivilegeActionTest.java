package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.InstructorPermissionRole;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.output.MessageOutput;

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
        InstructorPrivileges privileges = instructor1ofCourse1.privileges;
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

        instructor1ofCourse1.privileges = privileges;

        instructor1ofCourse1.role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM;
        dataBundle.instructors.put("instructor1OfCourse1", instructor1ofCourse1);
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    protected void testExecute() throws Exception {
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

        GetInstructorPrivilegeAction a = getAction(invalidInstructorParams);
        JsonResult result = getJsonResult(a);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());
        assertEquals("Instructor does not exist.", output.getMessage());
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

        assertFalse(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertTrue(response.isCanModifyInstructor());
        assertTrue(response.isCanModifySession());

        assertTrue(response.isCanViewStudentInSections());

        assertTrue(response.isCanModifySessionCommentsInSections());
        assertTrue(response.isCanViewSessionInSections());
        assertTrue(response.isCanSubmitSessionInSections());
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

        assertFalse(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertTrue(response.isCanModifyInstructor());
        assertTrue(response.isCanModifySession());

        assertTrue(response.isCanViewStudentInSections());

        assertTrue(response.isCanModifySessionCommentsInSections());
        assertTrue(response.isCanViewSessionInSections());
        assertTrue(response.isCanSubmitSessionInSections());
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Not enough parameters");
        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_withCourseId_shouldReturnGeneralPrivilege() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Request with course id to fetch general privileges");

        String[] courseIdParam = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
        };

        GetInstructorPrivilegeAction a = getAction(courseIdParam);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertFalse(response.isCanViewStudentInSections());

        assertFalse(response.isCanModifySessionCommentsInSections());
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanSubmitSessionInSections());
    }

    @Test
    protected void testExecute_withCourseIdAndSectionName_shouldReturnSectionLevelPrivilege() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Request with course id and section name to fetch section level privileges");

        String[] sectionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.SECTION_NAME, student1InCourse1.getSection(),
        };

        GetInstructorPrivilegeAction a = getAction(sectionParams);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertTrue(response.isCanViewStudentInSections());

        assertTrue(response.isCanModifySessionCommentsInSections());
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanSubmitSessionInSections());
    }

    @Test
    protected void testExecute_withCourseIdAndSectionNameAndSessionName_shouldReturnSessionLevelPrivilege() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session1ofCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Request with course id, section name and session name to fetch session level privileges");

        String[] sessionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.SECTION_NAME, student1InCourse1.getSection(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1ofCourse1.getFeedbackSessionName(),
        };

        GetInstructorPrivilegeAction a = getAction(sessionParams);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertTrue(response.isCanViewStudentInSections());

        assertTrue(response.isCanModifySessionCommentsInSections());
        assertTrue(response.isCanViewSessionInSections());
        assertTrue(response.isCanSubmitSessionInSections());
    }

    @Test
    protected void testExecute_withCourseIdAndSessionName_shouldReturnHybridPrivilege() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session1ofCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Request with course id, section name and session name to fetch session level privileges");

        String[] sessionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1ofCourse1.getFeedbackSessionName(),
        };

        GetInstructorPrivilegeAction a = getAction(sessionParams);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertFalse(response.isCanViewStudentInSections());

        assertTrue(response.isCanModifySessionCommentsInSections());
        assertTrue(response.isCanViewSessionInSections());
        assertTrue(response.isCanSubmitSessionInSections());
    }

    @Test
    protected void testExecute_withCourseIdAndSectionNameAndInvalidSessionName_shouldReturnSectionLevelPrivilege() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Request with course id,"
                + " section name and invalid session name, return section level privilege.");

        String[] invalidSessionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.SECTION_NAME, student1InCourse1.getSection(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "invalid session",
        };

        GetInstructorPrivilegeAction a = getAction(invalidSessionParams);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertTrue(response.isCanViewStudentInSections());

        assertTrue(response.isCanModifySessionCommentsInSections());
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanSubmitSessionInSections());
    }

    @Test
    protected void testExecute_withCourseIdAndInvalidSectionName_shouldReturnGeneralPrivilege() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Request with course id and invalid section name, return general privilege.");

        String[] invalidSectionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.SECTION_NAME, "invalid section",
        };

        GetInstructorPrivilegeAction a = getAction(invalidSectionParams);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertFalse(response.isCanViewStudentInSections());

        assertFalse(response.isCanModifySessionCommentsInSections());
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanSubmitSessionInSections());
    }

    @Test
    protected void testExecute_withCourseIdAndInvalidSessionName_shouldReturnGeneralPrivilege() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        ______TS("Request with course id and invalid session name, return general privilege.");

        String[] invalidSectionAndCourseIdParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "invalid session",
        };

        GetInstructorPrivilegeAction a = getAction(invalidSectionAndCourseIdParams);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());
        assertFalse(response.isCanModifySession());

        assertFalse(response.isCanViewStudentInSections());

        assertTrue(response.isCanModifySessionCommentsInSections());
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanSubmitSessionInSections());
    }

    @Test
    protected void testExecute_withCourseIdAndInstructorRole_shouldReturnPrivilegeForRole() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Request with course id and instructor role to fetch privilege for an instructor role");

        // course id is used for instructor identify verification here.
        String[] courseAndSessionParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME,
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.toString(),
        };

        GetInstructorPrivilegeAction a = getAction(courseAndSessionParams);
        InstructorPrivilegeData response = (InstructorPrivilegeData) getJsonResult(a).getOutput();

        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifyStudent());
        assertTrue(response.isCanModifyInstructor());
        assertTrue(response.isCanModifySession());

        assertTrue(response.isCanViewStudentInSections());

        assertTrue(response.isCanModifySessionCommentsInSections());
        // has privilege in any of the sections.
        assertTrue(response.isCanViewSessionInSections());
        assertTrue(response.isCanSubmitSessionInSections());
    }

    @Test
    protected void testExecute_withCourseIdAndInvalidInstructorRole_shouldFail() {
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        ______TS("Failure: Request with invalid instructor role.");
        String[] invalidRoleParams = {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME, "invalid role",
        };

        GetInstructorPrivilegeAction a = getAction(invalidRoleParams);
        JsonResult result = getJsonResult(a);

        MessageOutput output = (MessageOutput) result.getOutput();
        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid instructor role.", output.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
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
