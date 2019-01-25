package teammates.test.cases.newaction;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.newcontroller.InstructorGetCoursesAction;

/**
 *SUT: {@link InstructorGetCoursesAction}.
 */
public class InstructorGetCoursesActionTest extends BaseActionTest<InstructorGetCoursesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_STUDENTS_COURSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() {

        ______TS("Courses Exist, No Archived");

        ______TS("Courses Exist, Some Archived");

        ______TS("Courses Exist, All Archived");

        ______TS("No course exists");
    }

    @Override
    @Test
    public void testAccessControl() throws Exception {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForStudents();
        verifyInaccessibleForUnregisteredUsers();
        verifyAccessibleForAdminToMasqueradeAsInstructor(new String[] {});
    }
}
