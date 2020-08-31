package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.output.CourseData;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.CourseCreateRequest;
import teammates.ui.webapi.CreateCourseAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateCourseAction}.
 */
public class CreateCourseActionTest extends BaseActionTest<CreateCourseAction> {
    private CoursesLogic coursesLogic = CoursesLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case with new course id");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        CourseCreateRequest courseCreateRequest = new CourseCreateRequest();
        courseCreateRequest.setCourseName("New Course");
        courseCreateRequest.setTimeZone("UTC");
        courseCreateRequest.setCourseId("new-course");

        if (coursesLogic.isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }

        loginAsInstructor(instructorId);
        CreateCourseAction action = getAction(courseCreateRequest);

        JsonResult result = getJsonResult(action);
        CourseData courseData = (CourseData) result.getOutput();
        assertEquals(courseData.getCourseId(), "new-course");
        assertEquals(courseData.getCourseName(), "New Course");
        assertEquals(courseData.getTimeZone(), "UTC");

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertTrue(coursesLogic.isCoursePresent("new-course"));

        ______TS("Typical case with existing course id");

        courseCreateRequest.setCourseName("Existing Course");
        courseCreateRequest.setTimeZone("UTC");
        courseCreateRequest.setCourseId(courseId);

        action = getAction(courseCreateRequest);
        result = getJsonResult(action);
        MessageOutput message = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_CONFLICT, result.getStatusCode());
        AssertHelper.assertContains("Trying to create an entity that exists", message.getMessage());

        ______TS("Typical case missing course id");

        courseCreateRequest.setCourseName("New Course");
        courseCreateRequest.setTimeZone("UTC");
        courseCreateRequest.setCourseId("");

        action = getAction(courseCreateRequest);
        result = getJsonResult(action);

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};

        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}
