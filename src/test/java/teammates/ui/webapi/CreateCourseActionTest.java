package teammates.ui.webapi;

import java.time.ZoneId;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.AssertHelper;
import teammates.ui.output.CourseData;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.CourseCreateRequest;

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
    public void testExecute() {

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case with new course id");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();
        String courseId = instructor1OfCourse1.getCourseId();

        CourseCreateRequest courseCreateRequest = new CourseCreateRequest();
        courseCreateRequest.setCourseName("New Course");
        courseCreateRequest.setTimeZone("UTC");
        courseCreateRequest.setCourseId("new-course");

        if (logic.getCourse("new-course") != null) {
            logic.deleteCourseCascade("new-course");
        }

        loginAsInstructor(instructorId);
        CreateCourseAction action = getAction(courseCreateRequest);

        JsonResult result = getJsonResult(action);
        CourseData courseData = (CourseData) result.getOutput();
        assertEquals(courseData.getCourseId(), "new-course");
        assertEquals(courseData.getCourseName(), "New Course");
        assertEquals(courseData.getTimeZone(), "UTC");

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CourseAttributes createdCourse = logic.getCourse("new-course");
        assertNotNull(createdCourse);
        assertEquals("New Course", createdCourse.getName());
        assertEquals(ZoneId.of("UTC"), createdCourse.getTimeZone());
        assertEquals("TEAMMATES Test Institute 1", createdCourse.getInstitute());

        ______TS("Typical case with existing course id");

        courseCreateRequest.setCourseName("Existing Course");
        courseCreateRequest.setTimeZone("UTC");
        courseCreateRequest.setCourseId(courseId);

        action = getAction(courseCreateRequest);
        result = getJsonResult(action);
        MessageOutput message = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_CONFLICT, result.getStatusCode());
        AssertHelper.assertContains("has been used by another course, possibly by some other user.", message.getMessage());

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
    protected void testAccessControl() {
        String[] submissionParams = new String[] {};

        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}
