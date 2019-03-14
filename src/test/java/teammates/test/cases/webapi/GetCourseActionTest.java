package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetCourseAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.CourseData;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link GetCourseAction}.
 */
public class GetCourseActionTest extends BaseActionTest<GetCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        //See test cases below
    }

    @Test
    protected void testExecute_typicalUsage_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes expectedCourse = logic.getCourse(instructor1OfCourse1.getCourseId());

        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("typical success case");

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };
        GetCourseAction getCourseAction = getAction(params);
        JsonResult response = getJsonResult(getCourseAction);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        CourseData courseData = (CourseData) response.getOutput();

        assertEquals(expectedCourse.getId(), courseData.getCourseId());
        assertEquals(expectedCourse.getName(), courseData.getCourseName());
        assertEquals(expectedCourse.getTimeZone().getId(), courseData.getTimeZone());
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_nonExistentCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "fake-course",
        };

        assertNull(logic.getCourse("fake-course"));

        GetCourseAction getCourseAction = getAction(params);
        JsonResult response = getJsonResult(getCourseAction);
        MessageOutput messageOutput = (MessageOutput) response.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusCode());
        assertEquals("No course with id: fake-course", messageOutput.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        ______TS("non-existent course");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes courseAttributes = logic.getCourse(instructor1OfCourse1.getCourseId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "not-exist",
        };

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyCannotAccess(submissionParams);

        ______TS("only instructors of the same course can access");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseAttributes.getId(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
