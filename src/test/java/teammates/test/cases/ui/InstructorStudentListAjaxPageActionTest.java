package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorStudentListAjaxPageAction;
import teammates.ui.controller.InstructorStudentListAjaxPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorStudentListAjaxPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_AJAX_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        String instructorId = instructor.googleId;

        gaeSimulation.loginAsInstructor(instructorId);
        ______TS("Unsuccessful case: not enough parameters");

        verifyAssumptionFailure();

        String[] submissionParams = new String[] {

        };

        verifyAssumptionFailure(submissionParams);

        ______TS("typical successful case");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COURSE_INDEX, "1"
        };

        InstructorStudentListAjaxPageAction action = getAction(submissionParams);
        ShowPageResult result = (ShowPageResult) action.executeAndPostProcess();
        InstructorStudentListAjaxPageData data = (InstructorStudentListAjaxPageData) result.data;
        assertEquals(2, data.getSections().size());
        assertTrue(data.isHasSection());
        assertEquals(1, data.getCourseIndex());
        assertEquals(instructor.courseId, data.getCourseId());
    }

    private InstructorStudentListAjaxPageAction getAction(String... params) {
        return (InstructorStudentListAjaxPageAction) gaeSimulation.getActionObject(uri, params);
    }

}
