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
import teammates.ui.newcontroller.JsonResult.MessageOutput;

/**
 * SUT: {@link DeleteCourseAction}.
 */
public class DeleteCourseActionTest extends BaseActionTest<DeleteCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_DELETE;
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
                Const.ParamsNames.NEXT_URL, Const.ResourceURIs.INSTRUCTOR_HOME
        };

        assertTrue(CoursesLogic.inst().isCoursePresent("icdct.tpa.id1"));
        DeleteCourseAction deleteAction = getAction(submissionParams);
        JsonResult r = getJsonResult(deleteAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        MessageOutput msg = (MessageOutput) r.getOutput();
        assertEquals("The course idOfTypicalCourse1 has been deleted. You can restore it from the 'Courses' tab.",
                msg.getMessage());

        List<CourseAttributes> courseList = CoursesLogic.inst().getCoursesForInstructor(instructorId);
        assertEquals(1, courseList.size());
        assertEquals("icdct.tpa.id1", courseList.get(0).getId());

        ______TS("Masquerade mode, delete last course, redirect to Courses page");

        loginAsAdmin();

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "icdct.tpa.id1",
                Const.ParamsNames.NEXT_URL, Const.ResourceURIs.INSTRUCTOR_COURSES
        };

        deleteAction = getAction(addUserIdToParams(instructorId, submissionParams));
        r = getJsonResult(deleteAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("The course icdct.tpa.id1 has been deleted. You can restore it from the deleted courses table below.",
                msg.getMessage());

        courseList = CoursesLogic.inst().getCoursesForInstructor(instructorId);
        assertEquals(0, courseList.size());

        ______TS("Masquerade mode, delete last course, no next URL, redirect to Courses page");

        CoursesLogic.inst().createCourseAndInstructor(instructorId, "icdct.tpa.id2", "New course", "UTC");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "icdct.tpa.id2",
        };

        deleteAction = getAction(addUserIdToParams(instructorId, submissionParams));
        r = getJsonResult(deleteAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        msg = (MessageOutput) r.getOutput();
        assertEquals("The course icdct.tpa.id2 has been deleted. You can restore it from the deleted courses table below.",
                msg.getMessage());

        courseList = CoursesLogic.inst().getCoursesForInstructor(instructorId);
        assertEquals(0, courseList.size());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        CoursesLogic.inst().createCourseAndInstructor(
                typicalBundle.instructors.get("instructor1OfCourse1").googleId,
                "icdat.owncourse", "New course", "UTC");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "icdat.owncourse"
        };

        /*  Test access for users
         *  This should be separated from testing for admin as we need to recreate the course after being removed
         */
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyInaccessibleWithoutModifyCoursePrivilege(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);

        CoursesLogic.inst().deleteCourseCascade("icdat.owncourse");

        /* Test access for admin in masquerade mode */
        CoursesLogic.inst().createCourseAndInstructor(
                typicalBundle.instructors.get("instructor1OfCourse1").googleId,
                "icdat.owncourse", "New course", "UTC");
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);

        CoursesLogic.inst().deleteCourseCascade("icdat.owncourse");
    }

}
