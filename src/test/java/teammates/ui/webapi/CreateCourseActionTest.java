package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
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

        String[] submissionParams = {
                Const.ParamsNames.INSTRUCTOR_INSTITUTE, "TEAMMATES Test Institute 1",
        };

        CourseCreateRequest courseCreateRequest = new CourseCreateRequest();
        courseCreateRequest.setCourseName("New Course");
        courseCreateRequest.setTimeZone("UTC");
        courseCreateRequest.setCourseId("new-course");

        verifyHttpRequestBodyFailure(null, submissionParams);
        verifyHttpParameterFailure(courseCreateRequest);

        ______TS("Typical case with new course id");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();
        String courseId = instructor1OfCourse1.getCourseId();

        if (logic.getCourse("new-course") != null) {
            logic.deleteCourseCascade("new-course");
        }

        loginAsInstructor(instructorId);
        CreateCourseAction action = getAction(courseCreateRequest, submissionParams);

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

        InvalidOperationException ioe = verifyInvalidOperation(courseCreateRequest, submissionParams);
        assertEquals("The course ID idOfTypicalCourse1 has been used by another course, possibly by some other user. "
                + "Please try again with a different course ID.", ioe.getMessage());

        ______TS("Typical case missing course id");

        courseCreateRequest.setCourseName("New Course");
        courseCreateRequest.setTimeZone("UTC");
        courseCreateRequest.setCourseId("");

        verifyHttpRequestBodyFailure(courseCreateRequest, submissionParams);
    }

    @Override
    @Test
    protected void testAccessControl() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Cannot access with wrong/missing institute param");

        verifyHttpParameterFailureAcl();

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_INSTITUTE, "Unknown Institute",
        };
        verifyCannotAccess(submissionParams);

        ______TS("Can access with correct institute param");

        submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_INSTITUTE, "TEAMMATES Test Institute 1",
        };
        verifyCanAccess(submissionParams);

        ______TS("Cannot access without instructor privilege");

        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.getGoogleId());

        verifyCannotAccess(submissionParams);

        loginAsUnregistered("Unknown");

        verifyCannotAccess(submissionParams);
    }

}
