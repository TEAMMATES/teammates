package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCoursesPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorCoursesPageData;

/**
 * SUT: {@link InstructorCoursesPageAction}.
 */
public class InstructorCoursesPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSES_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        //TODO: find a way to test status message from session
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;

        String[] submissionParams = new String[] {Const.ParamsNames.IS_USING_AJAX, "true"};

        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        /* Explanation: If the action is supposed to verify parameters,
         * we should check here the correctness of parameter verification.
         * e.g.

             ______TS("Invalid parameters");
            //both parameters missing.
            verifyAssumptionFailure(new String[] {});

            //null student email, only course ID is set
            String[] invalidParams = new String[] {
                    Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
            };
            verifyAssumptionFailure(invalidParams);

         * In this action, there is no parameter verification.
         */

        ______TS("Typical case, 2 courses");
        if (CoursesLogic.inst().isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }

        CoursesLogic.inst().createCourseAndInstructor(instructorId, "new-course", "New course", "UTC");
        gaeSimulation.loginAsInstructor(instructorId);
        InstructorCoursesPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSES, false, "idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorCoursesPageData pageData = (InstructorCoursesPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(2, pageData.getActiveCourses().getRows().size() + pageData.getArchivedCourses().getRows().size());
        assertEquals(0, pageData.getArchivedCourses().getRows().size());
        assertEquals("", pageData.getCourseIdToShow());
        assertEquals("", pageData.getCourseNameToShow());

        String expectedLogMessage = "TEAMMATESLOG|||instructorCoursesPage|||instructorCoursesPage"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1"
                + "|||instr1@course1.tmt|||instructorCourse Page Load<br>Total courses: 2"
                + "|||/page/instructorCoursesPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Masquerade mode, 0 courses");

        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        CoursesLogic.inst().deleteCourseCascade(instructor1ofCourse1.courseId);
        CoursesLogic.inst().deleteCourseCascade("new-course");

        a = getAction(addUserIdToParams(instructorId, submissionParams));
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSES, false, "idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertEquals("You do not seem to have any courses. Use the form above to create a course.", r.getStatusMessage());
        assertFalse(r.isError);

        pageData = (InstructorCoursesPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(0, pageData.getArchivedCourses().getRows().size());
        assertEquals(0, pageData.getActiveCourses().getRows().size());
        assertEquals("", pageData.getCourseIdToShow());
        assertEquals("", pageData.getCourseNameToShow());

        expectedLogMessage = "TEAMMATESLOG|||instructorCoursesPage|||instructorCoursesPage"
                + "|||true|||Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1"
                + "|||instr1@course1.tmt|||instructorCourse Page Load<br>Total courses: 0"
                + "|||/page/instructorCoursesPage";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedLogMessage, a.getLogMessage(), adminUserId);
    }

    @Override
    protected InstructorCoursesPageAction getAction(String... params) {
        return (InstructorCoursesPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}
