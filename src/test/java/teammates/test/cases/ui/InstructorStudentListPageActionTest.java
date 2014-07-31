package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorStudentListPageAction;
import teammates.ui.controller.InstructorStudentListPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorStudentListPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        String instructorId = instructor.googleId;

        String[] submissionParams = new String[]{
                Const.ParamsNames.SEARCH_KEY, "A search key",
                Const.ParamsNames.DISPLAY_ARCHIVE, "false",
        };

        ______TS("Typical case, student list view");

        gaeSimulation.loginAsInstructor(instructorId);
        InstructorStudentListPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertEquals(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST
                + "?error=false&user=" + instructorId,
                r.getDestinationWithParams());
        assertEquals(false, r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorStudentListPageData pageData = (InstructorStudentListPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(2, pageData.courses.size());
        assertEquals("A search key", pageData.searchKey);
        assertEquals(new Boolean(false), pageData.displayArchive);

        String expectedLogMessage = "TEAMMATESLOG|||instructorStudentListPage|||instructorStudentListPage"+
                "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"+
                "|||instr3@course1n2.com|||instructorStudentList Page Load<br>Total Courses: 2"+
                "|||/page/instructorStudentListPage";
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("No courses");
        
        instructorId = dataBundle.accounts.get("instructorWithoutCourses").googleId;
        
        gaeSimulation.loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);

        assertEquals(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST
                + "?error=false&user=instructorWithoutCourses",
                r.getDestinationWithParams());
        assertEquals(false, r.isError);
        assertEquals(Const.StatusMessages.INSTRUCTOR_NO_COURSE_AND_STUDENTS, r.getStatusMessage());

        pageData = (InstructorStudentListPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(0, pageData.courses.size());

        expectedLogMessage = "TEAMMATESLOG|||instructorStudentListPage|||instructorStudentListPage"+
                "|||true|||Instructor|||Instructor Without Courses|||instructorWithoutCourses"+
                "|||iwc@yahoo.com|||instructorStudentList Page Load<br>Total Courses: 0"+
                "|||/page/instructorStudentListPage";
        assertEquals(expectedLogMessage, a.getLogMessage());
        
    }
    
    private InstructorStudentListPageAction getAction(String... params) throws Exception {
        return (InstructorStudentListPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}
