package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.DataBundleHelper;
import teammates.ui.webapi.action.InstructorGetCoursesAction;
import teammates.ui.webapi.action.InstructorGetCoursesAction.CourseDetails;
import teammates.ui.webapi.action.InstructorGetCoursesAction.InstructorGetCoursesResult;
import teammates.ui.webapi.action.JsonResult;

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
        DataBundleHelper dataBundleHelper = new DataBundleHelper(typicalBundle);

        InstructorAttributes instructor3 = typicalBundle.instructors.get("instructor3OfCourse2");
        loginAsInstructor(instructor3.googleId);

        boolean isInstructorAllowedToModify = instructor3.isAllowedForPrivilege(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

        ______TS("Courses Exist");

        InstructorGetCoursesAction action = getAction();
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        InstructorGetCoursesResult output = (InstructorGetCoursesResult) result.getOutput();

        List<CourseAttributes> expectedCourses = dataBundleHelper.getCoursesByInstructorGoogleId(instructor3.googleId);

        List<CourseDetails> expectedCourseDetailsList = new ArrayList<>();
        expectedCourses.forEach(course -> {
            if (course != null) {
                expectedCourseDetailsList.add(new CourseDetails(
                        course.getId(), course.getName(), instructor3.isArchived, isInstructorAllowedToModify));
            }
        });

        AssertHelper.assertSameContentIgnoreOrder(expectedCourseDetailsList, output.getCourses());
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
