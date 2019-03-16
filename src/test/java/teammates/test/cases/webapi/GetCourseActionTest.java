package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetCourseAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.CourseData;

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
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes courseAttributes = logic.getCourse(instructor1OfCourse1.getCourseId());

        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("typical success case");

        String[] params = {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };
        GetCourseAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        CourseData response = (CourseData) r.getOutput();

        assertEquals(courseAttributes.getId(), response.getCourseId());
        assertEquals(courseAttributes.getName(), response.getCourseName());
        assertEquals(courseAttributes.getTimeZone().getId(), response.getTimeZone());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes courseAttributes = logic.getCourse(instructor1OfCourse1.getCourseId());

        ______TS("non-existent feedback session");

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
