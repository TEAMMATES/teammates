package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.InstructorPrivilegeData;

/**
 * SUT: {@link GetInstructorPrivilegeAction}.
 */
public class GetInstructorPrivilegeActionIT extends BaseActionIT<GetInstructorPrivilegeAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_PRIVILEGE;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        Instructor otherInstructor = typicalBundle.instructors.get("instructor2OfCourse1");

        loginAsInstructor(instructor);

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
        assertTrue(courseLevelPrivilege.isCanViewSession());
        assertTrue(courseLevelPrivilege.isCanSubmitSession());
        assertTrue(courseLevelPrivilege.isCanModifySessionComments());

        assertTrue(privileges.getSectionLevelPrivileges().isEmpty());
        assertTrue(privileges.getSessionLevelPrivileges().isEmpty());

        ______TS("Typical Success Case fetching privilege of another instructor by user ID");
        params = new String[] {
                Const.ParamsNames.USER_ID, otherInstructor.getId().toString(),
        };

        getInstructorPrivilegeAction = getAction(params);
        response = (InstructorPrivilegeData) getJsonResult(getInstructorPrivilegeAction).getOutput();
        privileges = response.getPrivileges();
        courseLevelPrivilege = privileges.getCourseLevelPrivileges();

        assertFalse(courseLevelPrivilege.isCanModifyCourse());
        assertFalse(courseLevelPrivilege.isCanModifyInstructor());
        assertFalse(courseLevelPrivilege.isCanModifySession());
        assertFalse(courseLevelPrivilege.isCanModifyStudent());
        assertTrue(courseLevelPrivilege.isCanViewSession());
        assertTrue(courseLevelPrivilege.isCanSubmitSession());
        assertFalse(courseLevelPrivilege.isCanModifySessionComments());

        assertTrue(privileges.getSectionLevelPrivileges().isEmpty());
        assertTrue(privileges.getSessionLevelPrivileges().isEmpty());

        ______TS("Fetch privilege of non-existent instructor, should fail");
        UUID invalidInstructorId = UUID.randomUUID();
        params = new String[] {
                Const.ParamsNames.USER_ID, invalidInstructorId.toString(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Instructor does not exist.", enfe.getMessage());

        ______TS("Insufficient number of parameters, should fail");
        verifyHttpParameterFailure();
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testAccessControl() throws Exception {
        Instructor otherInstructor = typicalBundle.instructors.get("instructor2OfCourse1");
        Course course = typicalBundle.courses.get("course1");
        String[] params = new String[] {
                Const.ParamsNames.USER_ID, otherInstructor.getId().toString(),
        };

        verifyInaccessibleWithoutLogin(params);
        verifyInaccessibleForUnregisteredUsers(params);
        verifyInaccessibleForStudents(course, params);
        verifyAccessibleForInstructorsOfTheSameCourse(course, params);
        verifyAccessibleForAdmin(params);
    }

}
