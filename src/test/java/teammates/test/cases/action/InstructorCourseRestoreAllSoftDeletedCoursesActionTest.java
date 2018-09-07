package teammates.test.cases.action;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseRestoreAllSoftDeletedCoursesAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorCourseRestoreAllSoftDeletedCoursesAction}.
 */
public class InstructorCourseRestoreAllSoftDeletedCoursesActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_SOFT_DELETED_COURSE_RESTORE_ALL;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {

        ______TS("Typical case, restore all courses from Recycle Bin, with privilege");

        InstructorAttributes instructor1OfCourse3 = typicalBundle.instructors.get("instructor1OfCourse3");
        String instructor1Id = instructor1OfCourse3.googleId;
        gaeSimulation.loginAsInstructor(instructor1Id);
        assertTrue(CoursesLogic.inst().isCoursePresent(instructor1OfCourse3.courseId));

        InstructorCourseRestoreAllSoftDeletedCoursesAction restoreAllAction = getAction();
        RedirectResult redirectResult = getRedirectResult(restoreAllAction);

        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse3"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals("All courses have been restored.", redirectResult.getStatusMessage());
        List<CourseAttributes> courseList = CoursesLogic.inst().getCoursesForInstructor(instructor1Id);
        assertEquals(3, courseList.size());
        assertEquals(instructor1OfCourse3.courseId, courseList.get(1).getId());
        String expectedLogMessage = "TEAMMATESLOG|||instructorCourseRestoreAllCourses|||"
                + "instructorCourseRestoreAllCourses|||true|||Instructor|||Instructor 1 of Course 3|||"
                + "idOfInstructor1OfCourse3|||instr1@course3.tmt|||All courses restored|||"
                + "/page/instructorCourseRestoreAllCourses";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, restoreAllAction.getLogMessage());

        ______TS("Typical case, restore all courses from Recycle Bin, without privilege");

        InstructorAttributes instructor2OfCourse3 = typicalBundle.instructors.get("instructor2OfCourse3");
        String instructor2Id = instructor2OfCourse3.googleId;
        gaeSimulation.loginAsInstructor(instructor2Id);
        assertTrue(CoursesLogic.inst().isCoursePresent(instructor2OfCourse3.courseId));

        try {
            restoreAllAction = getAction();
            getRedirectResult(restoreAllAction);
        } catch (UnauthorizedAccessException e) {
            assertEquals("Course [idOfTypicalCourse3] is not accessible to instructor [instructor2@course3.tmt] "
                    + "for privilege [canmodifycourse]", e.getMessage());
        }

        courseList = CoursesLogic.inst().getCoursesForInstructor(instructor2Id);
        assertEquals(1, courseList.size());
        assertEquals(instructor2OfCourse3.courseId, courseList.get(0).getId());

    }

    @Override
    protected InstructorCourseRestoreAllSoftDeletedCoursesAction getAction(String... params) {
        return (InstructorCourseRestoreAllSoftDeletedCoursesAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        CoursesLogic.inst().createCourseAndInstructor(
                typicalBundle.instructors.get("instructor1OfCourse3").googleId,
                "icdat.owncourse", "New course", "UTC");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "icdat.owncourse"
        };

        //  Test access for users
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
    }
}
