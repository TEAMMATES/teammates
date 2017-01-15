package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorStudentRecordsAjaxPageAction;
import teammates.ui.controller.InstructorStudentRecordsAjaxPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorStudentRecordsAjaxPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_AJAX_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        StudentAttributes student = dataBundle.students.get("student2InCourse1");
        String instructorId = instructor.googleId;

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Typical case: specific session name");
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session"
        };

        InstructorStudentRecordsAjaxPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertEquals(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS_AJAX + "?error=false&user=idOfInstructor3",
                     r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());
        
        InstructorStudentRecordsAjaxPageData data = (InstructorStudentRecordsAjaxPageData) r.data;
        assertEquals(1, data.getResultsTables().size());
        
        ______TS("Typical case: instructor cannot view sections");

        instructor = dataBundle.instructors.get("helperOfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session"
        };

        a = getAction(submissionParams);
        r = getShowPageResult(a);

        assertEquals(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS_AJAX + "?error=false&user=idOfHelperOfCourse1",
                     r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        data = (InstructorStudentRecordsAjaxPageData) r.data;
        assertEquals(0, data.getResultsTables().size());
        
    }

    private InstructorStudentRecordsAjaxPageAction getAction(String... params) {
        return (InstructorStudentRecordsAjaxPageAction) gaeSimulation.getActionObject(uri, params);
    }

}
