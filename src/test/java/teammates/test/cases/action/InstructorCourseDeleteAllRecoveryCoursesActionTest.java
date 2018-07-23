package teammates.test.cases.action;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseDeleteAllRecoveryCoursesAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorCourseDeleteAllRecoveryCoursesAction}.
 */
public class InstructorCourseDeleteAllRecoveryCoursesActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_RECOVERY_COURSE_DELETE_ALL;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {

        ______TS("Typical case, delete all courses from Recycle Bin, without privilege");

        InstructorAttributes instructor2OfCourse3 = typicalBundle.instructors.get("instructor2OfCourse3");
        String instructor2Id = instructor2OfCourse3.googleId;
        gaeSimulation.loginAsInstructor(instructor2Id);
        assertTrue(CoursesLogic.inst().isCoursePresent(instructor2OfCourse3.courseId));
        InstructorCourseDeleteAllRecoveryCoursesAction deleteAllAction;

        try {
            deleteAllAction = getAction();
            getRedirectResult(deleteAllAction);
        } catch (UnauthorizedAccessException e) {
            assertEquals("Course [idOfTypicalCourse3] is not accessible to instructor [instructor2@course3.tmt] "
                    + "for privilege [canmodifycourse]", e.getMessage());
        }

        List<InstructorAttributes> instructorList = new ArrayList<>();
        instructorList.add(instructor2OfCourse3);
        List<CourseAttributes> courseList = CoursesLogic.inst().getRecoveryCoursesForInstructors(instructorList);
        assertEquals(1, courseList.size());
        assertEquals(instructor2OfCourse3.courseId, courseList.get(0).getId());
        assertTrue(CoursesLogic.inst().isCoursePresent(instructor2OfCourse3.courseId));

        ______TS("Typical case, delete all courses from Recycle Bin, with privilege");

        InstructorAttributes instructor1OfCourse3 = typicalBundle.instructors.get("instructor1OfCourse3");
        String instructor1Id = instructor1OfCourse3.googleId;
        gaeSimulation.loginAsInstructor(instructor1Id);
        assertTrue(CoursesLogic.inst().isCoursePresent(instructor1OfCourse3.courseId));

        deleteAllAction = getAction();
        RedirectResult redirectResult = getRedirectResult(deleteAllAction);

        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse3"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals("All courses have been permanently deleted.", redirectResult.getStatusMessage());
        assertFalse(CoursesLogic.inst().isCoursePresent(instructor1OfCourse3.courseId));
        String expectedLogMessage = "TEAMMATESLOG|||instructorRecoveryDeleteAllCourses|||"
                + "instructorRecoveryDeleteAllCourses|||true|||Instructor|||Instructor 1 of Course 3|||"
                + "idOfInstructor1OfCourse3|||instr1@course3.tmt|||All courses deleted|||"
                + "/page/instructorRecoveryDeleteAllCourses";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, deleteAllAction.getLogMessage());

    }

    @Override
    protected InstructorCourseDeleteAllRecoveryCoursesAction getAction(String... params) {
        return (InstructorCourseDeleteAllRecoveryCoursesAction) gaeSimulation.getActionObject(getActionUri(), params);
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
