package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.webapi.action.CreateCourseAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.CourseCreateRequest;

/**
 * SUT: {@link CreateCourseAction}.
 */
public class CreateCourseActionTest extends BaseActionTest<CreateCourseAction> {

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

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "new-course",
        };

        CourseCreateRequest courseCreateRequest = new CourseCreateRequest();
        courseCreateRequest.setCourseName("New Course");
        courseCreateRequest.setTimeZone("UTC");

        ______TS("Typical case with new course id");

        if (CoursesLogic.inst().isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }

        loginAsInstructor(instructorId);
        CreateCourseAction action = getAction(courseCreateRequest, submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        ______TS("Typical case with existing course id");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        courseCreateRequest.setCourseName("Existing Course");
        courseCreateRequest.setTimeZone("UTC");

        action = getAction(courseCreateRequest, submissionParams);
        result = getJsonResult(action);
        MessageOutput message = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_CONFLICT, result.getStatusCode());
        assertEquals("Trying to create a Course that exists: " + courseId, message.getMessage());

        ______TS("Typical case missing course id");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "",
        };
        courseCreateRequest.setCourseName("New Course");
        courseCreateRequest.setTimeZone("UTC");

        action = getAction(courseCreateRequest, submissionParams);
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
