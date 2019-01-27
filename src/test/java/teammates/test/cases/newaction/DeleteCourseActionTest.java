package teammates.test.cases.newaction;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.newcontroller.DeleteCourseAction;
import teammates.ui.newcontroller.JsonResult;

/**
 * SUT: {@link DeleteCourseAction}.
 */
public class DeleteCourseActionTest extends BaseActionTest<DeleteCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;

        loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyHttpParameterFailure();

        ______TS("Typical case, 2 courses, redirect to homepage");
        CoursesLogic.inst().createCourseAndInstructor(instructorId, "icdct.tpa.id1", "New course", "UTC");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_HOME_PAGE
        };

        DeleteCourseAction deleteAction = getAction(submissionParams);
        JsonResult result = getJsonResult(deleteAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        List<CourseAttributes> courseList = CoursesLogic.inst().getCoursesForInstructor(instructorId);
        assertEquals(1, courseList.size());
        assertEquals("icdct.tpa.id1", courseList.get(0).getId());

        ______TS("Masquerade mode, delete last course");

        loginAsAdmin();

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "icdct.tpa.id1",
        };

        deleteAction = getAction(addUserIdToParams(instructorId, submissionParams));
        result = getJsonResult(deleteAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        courseList = CoursesLogic.inst().getCoursesForInstructor(instructorId);
        assertEquals(0, courseList.size());
    }

    @Override
    @Test
    protected void testAccessControl() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        verifyInaccessibleWithoutModifyCoursePrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
