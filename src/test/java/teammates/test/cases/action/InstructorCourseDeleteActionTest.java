package teammates.test.cases.action;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseDeleteAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorCourseDeleteAction}.
 */
public class InstructorCourseDeleteActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_DELETE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyAssumptionFailure();

        ______TS("Typical case, 2 courses, redirect to homepage");
        CoursesLogic.inst().createCourseAndInstructor(instructorId, "icdct.tpa.id1", "New course", "UTC");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_HOME_PAGE
        };

        assertTrue(CoursesLogic.inst().isCoursePresent("icdct.tpa.id1"));
        InstructorCourseDeleteAction deleteAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(deleteAction);

        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_HOME_PAGE, false, "idOfInstructor1OfCourse1"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals("The course idOfTypicalCourse1 has been deleted. You can restore it from the 'Courses' tab.",
                redirectResult.getStatusMessage());

        List<CourseAttributes> courseList = CoursesLogic.inst().getCoursesForInstructor(instructorId);
        assertEquals(1, courseList.size());
        assertEquals("icdct.tpa.id1", courseList.get(0).getId());

        String expectedLogMessage = "TEAMMATESLOG|||instructorCourseDelete|||instructorCourseDelete|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                                    + "instr1@course1.tmt|||Course moved to recycle bin: idOfTypicalCourse1|||"
                                    + "/page/instructorCourseDelete";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, deleteAction.getLogMessage());

        ______TS("Masquerade mode, delete last course, redirect to Courses page");

        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "icdct.tpa.id1",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
        };
        deleteAction = getAction(addUserIdToParams(instructorId, submissionParams));
        redirectResult = getRedirectResult(deleteAction);

        assertEquals(getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse1"),
                     redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals("The course icdct.tpa.id1 has been deleted. You can restore it from the deleted courses table below.",
                redirectResult.getStatusMessage());

        courseList = CoursesLogic.inst().getCoursesForInstructor(instructorId);
        assertEquals(0, courseList.size());

        expectedLogMessage = "TEAMMATESLOG|||instructorCourseDelete|||instructorCourseDelete|||true|||Instructor(M)|||"
                             + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Course moved to recycle bin: icdct.tpa.id1|||/page/instructorCourseDelete";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedLogMessage, deleteAction.getLogMessage(), adminUserId);

        ______TS("Masquerade mode, delete last course, no next URL, redirect to Courses page");

        CoursesLogic.inst().createCourseAndInstructor(instructorId, "icdct.tpa.id2", "New course", "UTC");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "icdct.tpa.id2",
        };
        deleteAction = getAction(addUserIdToParams(instructorId, submissionParams));
        redirectResult = getRedirectResult(deleteAction);

        assertEquals(getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse1"),
                     redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals("The course icdct.tpa.id2 has been deleted. You can restore it from the deleted courses table below.",
                redirectResult.getStatusMessage());

        courseList = CoursesLogic.inst().getCoursesForInstructor(instructorId);
        assertEquals(0, courseList.size());

        expectedLogMessage = "TEAMMATESLOG|||instructorCourseDelete|||instructorCourseDelete|||true|||Instructor(M)|||"
                             + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Course moved to recycle bin: icdct.tpa.id2|||/page/instructorCourseDelete";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedLogMessage, deleteAction.getLogMessage(), adminUserId);

    }

    @Override
    protected InstructorCourseDeleteAction getAction(String... params) {
        return (InstructorCourseDeleteAction) gaeSimulation.getActionObject(getActionUri(), params);
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
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyUnaccessibleWithoutModifyCoursePrivilege(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);

        CoursesLogic.inst().deleteCourseCascade("icdat.owncourse");

        /* Test access for admin in masquerade mode */
        CoursesLogic.inst().createCourseAndInstructor(
                typicalBundle.instructors.get("instructor1OfCourse1").googleId,
                "icdat.owncourse", "New course", "UTC");
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
}
