package teammates.it.ui.webapi;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.webapi.GetInstructorPrivilegeAction;

/**
 * SUT: {@link GetInstructorPrivilegeAction}.
 */
public class GetInstructorPrivilegeActionIT extends BaseActionIT<GetInstructorPrivilegeAction> {
    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
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

        Assertions.assertTrue(courseLevelPrivilege.isCanModifyCourse());
        Assertions.assertTrue(courseLevelPrivilege.isCanModifyInstructor());
        Assertions.assertTrue(courseLevelPrivilege.isCanModifySession());
        Assertions.assertTrue(courseLevelPrivilege.isCanModifyStudent());
        Assertions.assertTrue(courseLevelPrivilege.isCanViewStudentInSections());
        Assertions.assertTrue(courseLevelPrivilege.isCanViewSessionInSections());
        Assertions.assertTrue(courseLevelPrivilege.isCanSubmitSessionInSections());
        Assertions.assertTrue(courseLevelPrivilege.isCanModifySessionCommentsInSections());

        Assertions.assertTrue(privileges.getSectionLevelPrivileges().isEmpty());
        Assertions.assertTrue(privileges.getSessionLevelPrivileges().isEmpty());

        ______TS("Typical Success Case fetching privilege of another instructor by email");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, otherInstructor.getEmail(),
        };

        getInstructorPrivilegeAction = getAction(params);
        response = (InstructorPrivilegeData) getJsonResult(getInstructorPrivilegeAction).getOutput();
        privileges = response.getPrivileges();
        courseLevelPrivilege = privileges.getCourseLevelPrivileges();

        Assertions.assertFalse(courseLevelPrivilege.isCanModifyCourse());
        Assertions.assertFalse(courseLevelPrivilege.isCanModifyInstructor());
        Assertions.assertFalse(courseLevelPrivilege.isCanModifySession());
        Assertions.assertFalse(courseLevelPrivilege.isCanModifyStudent());
        Assertions.assertTrue(courseLevelPrivilege.isCanViewStudentInSections());
        Assertions.assertTrue(courseLevelPrivilege.isCanViewSessionInSections());
        Assertions.assertTrue(courseLevelPrivilege.isCanSubmitSessionInSections());
        Assertions.assertFalse(courseLevelPrivilege.isCanModifySessionCommentsInSections());

        Assertions.assertTrue(privileges.getSectionLevelPrivileges().isEmpty());
        Assertions.assertTrue(privileges.getSessionLevelPrivileges().isEmpty());

        ______TS("Typical Success Case fetching privilege of another instructor by id");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_ID, otherInstructor.getGoogleId(),
        };

        getInstructorPrivilegeAction = getAction(params);
        response = (InstructorPrivilegeData) getJsonResult(getInstructorPrivilegeAction).getOutput();
        privileges = response.getPrivileges();
        courseLevelPrivilege = privileges.getCourseLevelPrivileges();

        Assertions.assertFalse(courseLevelPrivilege.isCanModifyCourse());
        Assertions.assertFalse(courseLevelPrivilege.isCanModifyInstructor());
        Assertions.assertFalse(courseLevelPrivilege.isCanModifySession());
        Assertions.assertFalse(courseLevelPrivilege.isCanModifyStudent());
        Assertions.assertTrue(courseLevelPrivilege.isCanViewStudentInSections());
        Assertions.assertTrue(courseLevelPrivilege.isCanViewSessionInSections());
        Assertions.assertTrue(courseLevelPrivilege.isCanSubmitSessionInSections());
        Assertions.assertFalse(courseLevelPrivilege.isCanModifySessionCommentsInSections());

        Assertions.assertTrue(privileges.getSectionLevelPrivileges().isEmpty());
        Assertions.assertTrue(privileges.getSessionLevelPrivileges().isEmpty());

        ______TS("Fetch privilege of non-existent instructor, should fail");
        params = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_ID, "invalidId",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        Assertions.assertEquals("Instructor does not exist.", enfe.getMessage());

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
