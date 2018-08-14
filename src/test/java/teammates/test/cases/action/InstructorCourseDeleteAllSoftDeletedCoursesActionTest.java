package teammates.test.cases.action;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseDeleteAllSoftDeletedCoursesAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorCourseDeleteAllSoftDeletedCoursesAction}.
 */
public class InstructorCourseDeleteAllSoftDeletedCoursesActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_SOFT_DELETED_COURSE_DELETE_ALL;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {

        ______TS("Typical case, delete all courses from Recycle Bin, without privilege");

        InstructorAttributes instructor2OfCourse3 = typicalBundle.instructors.get("instructor2OfCourse3");
        String instructor2Id = instructor2OfCourse3.googleId;
        gaeSimulation.loginAsInstructor(instructor2Id);
        assertTrue(CoursesLogic.inst().isCoursePresent(instructor2OfCourse3.courseId));
        InstructorCourseDeleteAllSoftDeletedCoursesAction deleteAllAction;

        try {
            deleteAllAction = getAction();
            getRedirectResult(deleteAllAction);
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

        ______TS("Typical case, delete all courses from Recycle Bin, with privilege for only some courses");

        InstructorAttributes instructor1OfCourse3 = typicalBundle.instructors.get("instructor1OfCourse3");
        InstructorAttributes newInstructor = InstructorAttributes
                .builder(instructor1OfCourse3.getGoogleId(), "icdat.owncourse", "Instructor1 Course3",
                        "instructor1@course3.tmt")
                .build();
        gaeSimulation.loginAsInstructor(instructor1OfCourse3.googleId);
        instructorList.remove(instructor2OfCourse3);
        instructorList.add(instructor1OfCourse3);
        instructorList.add(newInstructor);
        courseList = CoursesLogic.inst().getCoursesForInstructor(instructorList);
        assertEquals(1, courseList.size());
        CoursesLogic.inst().moveCourseToRecycleBin("icdat.owncourse");
        courseList = CoursesLogic.inst().getSoftDeletedCoursesForInstructors(instructorList);
        assertEquals(2, courseList.size());
        newInstructor.privileges.updatePrivilege("canmodifycourse", false);
        InstructorsLogic.inst().updateInstructorByGoogleId(instructor1OfCourse3.getGoogleId(), newInstructor);

        try {
            deleteAllAction = getAction();
            getRedirectResult(deleteAllAction);
        } catch (UnauthorizedAccessException e) {
            assertEquals("Course [icdat.owncourse] is not accessible to instructor [instructor1@course3.tmt] "
                    + "for privilege [canmodifycourse]", e.getMessage());
        }

        courseList = CoursesLogic.inst().getSoftDeletedCoursesForInstructors(instructorList);
        assertEquals(2, courseList.size());
        assertTrue(CoursesLogic.inst().isCoursePresent(instructor1OfCourse3.courseId));
        assertTrue(CoursesLogic.inst().isCoursePresent(newInstructor.courseId));

        ______TS("Typical case, delete all courses from Recycle Bin, with privilege");

        String instructor1Id = instructor1OfCourse3.googleId;
        gaeSimulation.loginAsInstructor(instructor1Id);
        newInstructor.privileges.updatePrivilege("canmodifycourse", true);
        InstructorsLogic.inst().updateInstructorByGoogleId(instructor1OfCourse3.getGoogleId(), newInstructor);

        deleteAllAction = getAction();
        RedirectResult redirectResult = getRedirectResult(deleteAllAction);

        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse3"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals("All courses have been permanently deleted.", redirectResult.getStatusMessage());
        assertFalse(CoursesLogic.inst().isCoursePresent(instructor1OfCourse3.courseId));
        assertFalse(CoursesLogic.inst().isCoursePresent("icdat.owncourse"));
        String expectedLogMessage = "TEAMMATESLOG|||instructorCourseDeleteAllCourses|||"
                + "instructorCourseDeleteAllCourses|||true|||Instructor|||Instructor 1 of Course 3|||"
                + "idOfInstructor1OfCourse3|||instr1@course3.tmt|||All courses deleted|||"
                + "/page/instructorCourseDeleteAllCourses";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, deleteAllAction.getLogMessage());

    }

    @Override
    protected InstructorCourseDeleteAllSoftDeletedCoursesAction getAction(String... params) {
        return (InstructorCourseDeleteAllSoftDeletedCoursesAction) gaeSimulation.getActionObject(getActionUri(), params);
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
