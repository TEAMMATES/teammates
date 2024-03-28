package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetInstructorPrivilegeAction;

/**
 * SUT: {@link GetInstructorPrivilegeAction}.
 */
public class GetInstructorPrivilegeActionIT extends BaseActionIT<GetInstructorPrivilegeAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_PRIVILEGE;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor otherInstructor = typicalBundle.instructors.get("instructor2OfCourse1");

        loginAsInstructor(instructor.getGoogleId());

        ______TS("Typical Success Case fetching privilege of self");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        GetInstructorPrivilegeAction getInstructorPrivilegeAction = getAction(params);
        InstructorPrivilegeData response =
                (InstructorPrivilegeData) getJsonResult(getInstructorPrivilegeAction).getOutput();
        InstructorPrivileges privileges = response.getPrivileges();
        InstructorPermissionSet courseLevelPrivilege = privileges.getCourseLevelPrivileges();

        assertTrue(courseLevelPrivilege.isCanModifyCourse());
        assertTrue(courseLevelPrivilege.isCanModifyInstructor());
        assertTrue(courseLevelPrivilege.isCanModifySession());
        assertTrue(courseLevelPrivilege.isCanModifyStudent());
        assertTrue(courseLevelPrivilege.isCanViewStudentInSections());
        assertTrue(courseLevelPrivilege.isCanViewSessionInSections());
        assertTrue(courseLevelPrivilege.isCanSubmitSessionInSections());
        assertTrue(courseLevelPrivilege.isCanModifySessionCommentsInSections());

        assertTrue(privileges.getSectionLevelPrivileges().isEmpty());
        assertTrue(privileges.getSessionLevelPrivileges().isEmpty());

        ______TS("Typical Success Case fetching privilege of another instructor by email");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, otherInstructor.getEmail(),
        };

        getInstructorPrivilegeAction = getAction(params);
        response = (InstructorPrivilegeData) getJsonResult(getInstructorPrivilegeAction).getOutput();
        privileges = response.getPrivileges();
        courseLevelPrivilege = privileges.getCourseLevelPrivileges();

        assertFalse(courseLevelPrivilege.isCanModifyCourse());
        assertFalse(courseLevelPrivilege.isCanModifyInstructor());
        assertFalse(courseLevelPrivilege.isCanModifySession());
        assertFalse(courseLevelPrivilege.isCanModifyStudent());
        assertTrue(courseLevelPrivilege.isCanViewStudentInSections());
        assertTrue(courseLevelPrivilege.isCanViewSessionInSections());
        assertTrue(courseLevelPrivilege.isCanSubmitSessionInSections());
        assertFalse(courseLevelPrivilege.isCanModifySessionCommentsInSections());

        assertTrue(privileges.getSectionLevelPrivileges().isEmpty());
        assertTrue(privileges.getSessionLevelPrivileges().isEmpty());

        ______TS("Typical Success Case fetching privilege of another instructor by id");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_ID, otherInstructor.getGoogleId(),
        };

        getInstructorPrivilegeAction = getAction(params);
        response = (InstructorPrivilegeData) getJsonResult(getInstructorPrivilegeAction).getOutput();
        privileges = response.getPrivileges();
        courseLevelPrivilege = privileges.getCourseLevelPrivileges();

        assertFalse(courseLevelPrivilege.isCanModifyCourse());
        assertFalse(courseLevelPrivilege.isCanModifyInstructor());
        assertFalse(courseLevelPrivilege.isCanModifySession());
        assertFalse(courseLevelPrivilege.isCanModifyStudent());
        assertTrue(courseLevelPrivilege.isCanViewStudentInSections());
        assertTrue(courseLevelPrivilege.isCanViewSessionInSections());
        assertTrue(courseLevelPrivilege.isCanSubmitSessionInSections());
        assertFalse(courseLevelPrivilege.isCanModifySessionCommentsInSections());

        assertTrue(privileges.getSectionLevelPrivileges().isEmpty());
        assertTrue(privileges.getSessionLevelPrivileges().isEmpty());

        ______TS("Fetch privilege of non-existent instructor, should fail");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_ID, "invalidId",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Instructor does not exist.", enfe.getMessage());

        ______TS("Insufficient number of parameters, should fail");
        verifyHttpParameterFailure();
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        Course course = typicalBundle.courses.get("course1");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        verifyInaccessibleWithoutLogin(params);
        verifyInaccessibleForUnregisteredUsers(params);
        verifyInaccessibleForStudents(course, params);
        verifyAccessibleForInstructorsOfTheSameCourse(course, params);
        verifyAccessibleForAdmin(params);
    }

}
