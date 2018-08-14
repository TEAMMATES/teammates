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
import teammates.ui.controller.InstructorCourseDeleteSoftDeletedCourseAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorCourseDeleteSoftDeletedCourseAction}.
 */
public class InstructorCourseDeleteSoftDeletedCourseActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_SOFT_DELETED_COURSE_DELETE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {

        ______TS("Not enough parameters");

        verifyAssumptionFailure();

        ______TS("Typical case, delete 1 course from Recycle Bin, without privilege");

        InstructorAttributes instructor2OfCourse3 = typicalBundle.instructors.get("instructor2OfCourse3");
        String instructor2Id = instructor2OfCourse3.googleId;
        gaeSimulation.loginAsInstructor(instructor2Id);
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor2OfCourse3.courseId
        };
        assertTrue(CoursesLogic.inst().isCoursePresent(instructor2OfCourse3.courseId));
        InstructorCourseDeleteSoftDeletedCourseAction deleteAction;

        try {
            deleteAction = getAction(submissionParams);
            getRedirectResult(deleteAction);
        } catch (UnauthorizedAccessException e) {
            assertEquals("Course [idOfTypicalCourse3] is not accessible to instructor [instructor2@course3.tmt] "
                    + "for privilege [canmodifycourse]", e.getMessage());
        }

        List<InstructorAttributes> instructorList = new ArrayList<>();
        instructorList.add(instructor2OfCourse3);
        List<CourseAttributes> courseList = CoursesLogic.inst().getSoftDeletedCoursesForInstructors(instructorList);
        assertEquals(1, courseList.size());
        assertEquals(instructor2OfCourse3.courseId, courseList.get(0).getId());
        assertTrue(CoursesLogic.inst().isCoursePresent(instructor2OfCourse3.courseId));

        ______TS("Typical case, delete 1 course from Recycle Bin, with privilege");

        InstructorAttributes instructor1OfCourse3 = typicalBundle.instructors.get("instructor1OfCourse3");
        String instructor1Id = instructor1OfCourse3.googleId;
        gaeSimulation.loginAsInstructor(instructor1Id);
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse3.courseId
        };
        assertTrue(CoursesLogic.inst().isCoursePresent(instructor1OfCourse3.courseId));

        deleteAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(deleteAction);

        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse3"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals("The course idOfTypicalCourse3 has been permanently deleted.", redirectResult.getStatusMessage());
        assertFalse(CoursesLogic.inst().isCoursePresent(instructor1OfCourse3.courseId));
        String expectedLogMessage = "TEAMMATESLOG|||instructorCourseDeleteCourse|||instructorCourseDeleteCourse|||"
                + "true|||Instructor|||Instructor 1 of Course 3|||idOfInstructor1OfCourse3|||"
                + "instr1@course3.tmt|||Course deleted: idOfTypicalCourse3|||"
                + "/page/instructorCourseDeleteCourse";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, deleteAction.getLogMessage());

    }

    @Override
    protected InstructorCourseDeleteSoftDeletedCourseAction getAction(String... params) {
        return (InstructorCourseDeleteSoftDeletedCourseAction) gaeSimulation.getActionObject(getActionUri(), params);
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
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyUnaccessibleForStudentsOfOtherCourses(submissionParams);
        verifyUnaccessibleWithoutModifyCoursePrivilege(submissionParams);
    }
}
