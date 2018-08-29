package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorCourseEnrollAjaxPageAction;
import teammates.ui.pagedata.InstructorCourseEnrollAjaxPageData;

/**
 * SUT: {@link InstructorCourseEnrollAjaxPageAction}.
 */
public class InstructorCourseEnrollAjaxPageActionTest extends BaseActionTest {
    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_AJAX_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor.googleId;

        gaeSimulation.loginAsInstructor(instructorId);
        ______TS("Unsuccessful case: not enough parameters");
        verifyAssumptionFailure();
        String[] submissionParams = new String[] {};
        verifyAssumptionFailure(submissionParams);

        ______TS("typical successful case");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
        };
        InstructorCourseEnrollAjaxPageAction action = getAction(submissionParams);
        AjaxResult ajaxResult = getAjaxResult(action);
        assertNotNull(ajaxResult);
        InstructorCourseEnrollAjaxPageData pageData = (InstructorCourseEnrollAjaxPageData) ajaxResult.data;

        String expectedString = "Section 1|Team 1.1</td></div>'\"|student1 In Course1</td></div>'\""
                + "|student1InCourse1@gmail.tmt|comment for student1InCourse1</td></div>'\""
                + "Section 1|Team 1.1</td></div>'\"|student2 In Course1|student2InCourse1@gmail.tmt|"
                + "Section 1|Team 1.1</td></div>'\"|student3 In Course1|student3InCourse1@gmail.tmt|"
                + "Section 1|Team 1.1</td></div>'\"|student4 In Course1|student4InCourse1@gmail.tmt|"
                + "Section 2|Team 1.2|student5 In Course1|student5InCourse1@gmail.tmt|";

        StringBuilder resultString = new StringBuilder();
        for (StudentAttributes student : pageData.students) {
            resultString.append(student.toEnrollmentString());
        }
        assertEquals(expectedString, resultString.toString());
    }

    @Override
    protected InstructorCourseEnrollAjaxPageAction getAction(String... params) {
        return (InstructorCourseEnrollAjaxPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
