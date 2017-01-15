package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.test.util.Priority;
import teammates.ui.controller.InstructorStudentListPageAction;
import teammates.ui.controller.InstructorStudentListPageData;
import teammates.ui.controller.ShowPageResult;

// Priority added due to conflict between InstructorStudentListPageActionTest,
// StudentHomePageActionTest, and StudentCommentsPageActionTest.
@Priority(-3)
public class InstructorStudentListPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        String instructorId = instructor.googleId;

        String[] submissionParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "A search key",
                Const.ParamsNames.DISPLAY_ARCHIVE, "false",
        };

        ______TS("Typical case, student list view");

        gaeSimulation.loginAsInstructor(instructorId);
        InstructorStudentListPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertEquals(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST + "?error=false&user=" + instructorId,
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

        instructorId = dataBundle.accounts.get("instructorWithoutCourses").googleId;

        gaeSimulation.loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);

        assertEquals(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST + "?error=false&user=instructorWithoutCourses",
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

        instructor = dataBundle.instructors.get("instructorOfArchivedCourse");
        instructorId = instructor.googleId;

        ______TS("Archived course, not displayed");

        gaeSimulation.loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);

        assertEquals(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST + "?error=false&user=idOfInstructorOfArchivedCourse",
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

        assertEquals(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST + "?error=false&user=idOfInstructorOfArchivedCourse",
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

    private InstructorStudentListPageAction getAction(String... params) {
        return (InstructorStudentListPageAction) gaeSimulation.getActionObject(uri, params);
    }

}
