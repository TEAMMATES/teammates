package teammates.test.cases.newaction;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.newcontroller.DeleteInstructorCourseAction;
import teammates.ui.newcontroller.JsonResult;

/**
 * SUT: {@link DeleteInstructorCourseAction}.
 */
public class DeleteInstructorCourseActionTest extends BaseActionTest<DeleteInstructorCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_COURSES_DELETE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        ______TS("Typical case, delete a course from home page");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.NEXT_URL, Const.ResourceURIs.INSTRUCTOR_HOME
        };

        loginAsInstructor(instructorId);
        DeleteInstructorCourseAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        JsonResult.MessageOutput message = (JsonResult.MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals("The course " + courseId + " has been deleted. You can restore it from the 'Courses' tab.",
                message.getMessage());

        ______TS("Typical case, delete a course from courses page");

        CoursesLogic.inst().createCourseAndInstructor(instructorId, "new-course", "New course", "UTC");
        submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, "new-course",
                Const.ParamsNames.NEXT_URL, Const.ResourceURIs.INSTRUCTOR_COURSES
        };

        action = getAction(submissionParams);
        result = getJsonResult(action);
        message = (JsonResult.MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals("The course new-course has been deleted. You can restore it from the soft-deleted "
                + "courses table below.", message.getMessage());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };

        verifyOnlyInstructorsCanAccess(submissionParams);
        verifyInaccessibleWithoutModifyCoursePrivilege(submissionParams);
    }
}
