package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.Provider;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;

/**
 * SUT: {@link CreateInstructorAction}.
 */
public class CreateInstructorActionIT extends BaseActionIT<CreateInstructorAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());

        // Ensure the admin account exists for email sending
        String adminEmail = Config.APP_ADMINS.get(0);
        inTransaction(() -> logic.createAccount(Provider.TEAMMATES_DEV, "validAdminSubject",
                "validAdminTenant", adminEmail, adminEmail));
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
    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute() {
        // see test cases below
    }

    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute_typicalCase_shouldPass() {
        loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").getGoogleId());

        Course course1 = typicalBundle.courses.get("course1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course1.getId(),
        };

        InstructorCreateRequest instructorCreateRequest = new InstructorCreateRequest(
                "00000000-0000-4000-8000-000000000006", "newInstructorName",
                "newinstructoremail@mail.com", Const.InstructorPermissionRoleNames.COOWNER,
                "instructorDisplayName", false);
        CreateInstructorAction action = getAction(instructorCreateRequest, params);

        JsonResult response = getJsonResult(action);
        InstructorData instructorData = (InstructorData) response.getOutput();

        Instructor createdInstructor = inTransaction(() ->
                logic.getInstructorForEmail(course1.getId(), instructorCreateRequest.getEmail()));

        assertEquals(createdInstructor.getName(), instructorCreateRequest.getName());
        assertEquals(createdInstructor.getEmail(), instructorCreateRequest.getEmail());
        assertEquals(createdInstructor.getName(), instructorData.getName());
        assertEquals(createdInstructor.getEmail(), instructorData.getEmail());
        assertFalse(createdInstructor.isDisplayedToStudents());
        assertTrue(logic.hasInstructorPermissions(createdInstructor,
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertTrue(logic.hasInstructorPermissions(createdInstructor,
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertTrue(logic.hasInstructorPermissions(createdInstructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertTrue(logic.hasInstructorPermissions(createdInstructor,
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
    }

    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute_uniqueEmailClash_shouldFail() {
        loginAsAdmin();

        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        InstructorCreateRequest instructorCreateRequest = new InstructorCreateRequest(
                instructor1OfCourse1.getCourseId(), "instructor3ofCourse1",
                instructor1OfCourse1.getEmail(), Const.InstructorPermissionRoleNames.TUTOR,
                "instructor3ofCourse1", false);

        CreateInstructorAction action = getAction(instructorCreateRequest, params);
        assertThrowsInTransaction(InvalidOperationException.class, action::execute);
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        Instructor instructor = typicalBundle.instructors.get("instructor2OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        ______TS("only instructors of the same course can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(course,
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, submissionParams);
        ______TS("instructors of other courses cannot access");

        verifyInaccessibleForInstructorsOfOtherCourses(course, submissionParams);
    }

}
