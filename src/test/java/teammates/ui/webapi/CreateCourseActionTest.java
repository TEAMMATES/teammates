package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.CourseData;
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

        verifyHttpRequestBodyFailure(null);

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

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        CourseData courseData = (CourseData) result.getOutput();
        assertEquals(courseData.getCourseId(), "new-course");
        assertEquals(courseData.getCourseName(), "New Course");
        assertEquals(courseData.getTimeZone(), "UTC");
        assertEquals(courseData.getInstitute(), "TEAMMATES Test Institute 1");

        CourseAttributes createdCourse = logic.getCourse("new-course");
        assertNotNull(createdCourse);
        assertEquals("New Course", createdCourse.getName());
        assertEquals("UTC", createdCourse.getTimeZone());
        assertEquals("TEAMMATES Test Institute 1", createdCourse.getInstitute());

        ______TS("Typical case with existing course id");

        courseCreateRequest.setCourseName("Existing Course");
        courseCreateRequest.setTimeZone("UTC");
        courseCreateRequest.setCourseId(courseId);

        InvalidOperationException ioe = verifyInvalidOperation(courseCreateRequest);
        assertEquals("The course ID idOfTypicalCourse1 has been used by another course, possibly by some other user. "
                + "Please try again with a different course ID.", ioe.getMessage());

        ______TS("Typical case missing course id");

        courseCreateRequest.setCourseName("New Course");
        courseCreateRequest.setTimeZone("UTC");
        courseCreateRequest.setCourseId("");

        verifyHttpRequestBodyFailure(courseCreateRequest);
    }

    @Override
    @Test
    protected void testAccessControl() {
        String[] submissionParams = new String[] {};

        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}
