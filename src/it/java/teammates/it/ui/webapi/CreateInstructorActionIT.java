package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.webapi.CreateInstructorAction;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateInstructorAction}.
 */
public class CreateInstructorActionIT extends BaseActionIT<CreateInstructorAction> {
    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() {
        // see test cases below
    }

    @Test
    protected void testExecute_typicalCase_shouldPass() throws Exception {
        loginAsAdmin();

        Course course1 = typicalBundle.courses.get("course1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course1.getId(),
        };

        InstructorCreateRequest instructorCreateRequest = new InstructorCreateRequest(
                "00000000-0000-4000-8000-000000000006", "newInstructorName",
                "newInstructorEmail@mail.com", Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                "instructorDisplayName", false);
        CreateInstructorAction action = getAction(instructorCreateRequest, params);

        JsonResult response = getJsonResult(action);
        InstructorData instructorData = (InstructorData) response.getOutput();

        Instructor createdInstructor = logic.getInstructorForEmail(course1.getId(), instructorCreateRequest.getEmail());

        assertEquals(createdInstructor.getName(), instructorCreateRequest.getName());
        assertEquals(createdInstructor.getEmail(), instructorCreateRequest.getEmail());
        assertEquals(createdInstructor.getName(), instructorData.getName());
        assertEquals(createdInstructor.getEmail(), instructorData.getEmail());
        assertFalse(createdInstructor.isDisplayedToStudents());
        assertTrue(createdInstructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertTrue(createdInstructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertTrue(createdInstructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertTrue(createdInstructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT));
    }

    @Test
    protected void testExecute_uniqueEmailClash_shouldFail() throws Exception {

        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        InstructorCreateRequest instructorCreateRequest = new InstructorCreateRequest(
                instructor1OfCourse1.getCourseId(), "instructor3ofCourse1",
                instructor1OfCourse1.getEmail(), Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR,
                "instructor3ofCourse1", false);

        CreateInstructorAction action = getAction(instructorCreateRequest, params);
        assertThrows(InvalidOperationException.class, action::execute);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        Instructor instructor = typicalBundle.instructors.get("instructor2OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        ______TS("Admins can access");

        verifyAccessibleForAdmin(submissionParams);

        ______TS("only instructors of the same course can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(course,
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, submissionParams);
        ______TS("instructors of other courses cannot access");

        verifyInaccessibleForInstructorsOfOtherCourses(course, submissionParams);
    }

}
