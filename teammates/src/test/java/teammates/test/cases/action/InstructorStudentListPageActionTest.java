package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.Priority;
import teammates.ui.controller.InstructorStudentListPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorStudentListPageData;

/**
 * SUT: {@link InstructorStudentListPageAction}.
 */
// Priority added due to conflict between InstructorStudentListPageActionTest,
// and StudentHomePageActionTest.
@Priority(-3)
public class InstructorStudentListPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor3OfCourse1");
        String instructorId = instructor.googleId;

        String[] submissionParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "A search key",
                Const.ParamsNames.DISPLAY_ARCHIVE, "false",
        };

        ______TS("Typical case, student list view");

        gaeSimulation.loginAsInstructor(instructorId);
        InstructorStudentListPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST, false, instructorId),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorStudentListPage|||instructorStudentListPage"
                                  + "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"
                                  + "|||instr3@course1n2.tmt|||instructorStudentList Page Load<br>Total Courses: 2"
                                  + "|||/page/instructorStudentListPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        InstructorStudentListPageData islpd = (InstructorStudentListPageData) r.data;
        assertEquals(2, islpd.getNumOfCourses());

        ______TS("No courses");

        instructorId = typicalBundle.accounts.get("instructorWithoutCourses").googleId;

        gaeSimulation.loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST, false, "instructorWithoutCourses"),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.INSTRUCTOR_NO_COURSE_AND_STUDENTS, r.getStatusMessage());

        islpd = (InstructorStudentListPageData) r.data;
        assertEquals(0, islpd.getNumOfCourses());

        expectedLogMessage = "TEAMMATESLOG|||instructorStudentListPage|||instructorStudentListPage"
                           + "|||true|||Instructor|||Instructor Without Courses|||instructorWithoutCourses"
                           + "|||iwc@yahoo.tmt|||instructorStudentList Page Load<br>Total Courses: 0"
                           + "|||/page/instructorStudentListPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        instructor = typicalBundle.instructors.get("instructorOfArchivedCourse");
        instructorId = instructor.googleId;

        ______TS("Archived course, not displayed");

        gaeSimulation.loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_STUDENT_LIST, false, "idOfInstructorOfArchivedCourse"),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        islpd = (InstructorStudentListPageData) r.data;
        assertEquals(0, islpd.getNumOfCourses());

        expectedLogMessage = "TEAMMATESLOG|||instructorStudentListPage|||instructorStudentListPage"
                           + "|||true|||Instructor|||InstructorOfArchiveCourse name|||idOfInstructorOfArchivedCourse"
                           + "|||instructorOfArchiveCourse@archiveCourse.tmt"
                           + "|||instructorStudentList Page Load<br>Total Courses: 1"
                           + "|||/page/instructorStudentListPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        submissionParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "A search key",
                Const.ParamsNames.DISPLAY_ARCHIVE, "true",
        };

        ______TS("Archived course, displayed");

        gaeSimulation.loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_STUDENT_LIST, false, "idOfInstructorOfArchivedCourse"),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        islpd = (InstructorStudentListPageData) r.data;
        assertEquals(1, islpd.getNumOfCourses());

        expectedLogMessage = "TEAMMATESLOG|||instructorStudentListPage|||instructorStudentListPage"
                           + "|||true|||Instructor|||InstructorOfArchiveCourse name|||idOfInstructorOfArchivedCourse"
                           + "|||instructorOfArchiveCourse@archiveCourse.tmt"
                           + "|||instructorStudentList Page Load<br>Total Courses: 1"
                           + "|||/page/instructorStudentListPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

    }

    @Override
    protected InstructorStudentListPageAction getAction(String... params) {
        return (InstructorStudentListPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyInstructorsCanAccess(submissionParams);
    }

}
