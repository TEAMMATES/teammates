package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorStudentListAjaxPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorStudentListAjaxPageData;

/**
 * SUT: {@link InstructorStudentListAjaxPageAction}.
 */
public class InstructorStudentListAjaxPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_AJAX_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor3OfCourse1");
        String instructorId = instructor.googleId;

        gaeSimulation.loginAsInstructor(instructorId);
        ______TS("Unsuccessful case: not enough parameters");

        verifyAssumptionFailure();

        String[] submissionParams = new String[] {};

        verifyAssumptionFailure(submissionParams);

        ______TS("typical successful case");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COURSE_INDEX, "1"
        };

        InstructorStudentListAjaxPageAction action = getAction(submissionParams);
        ShowPageResult result = getShowPageResult(action);
        InstructorStudentListAjaxPageData data = (InstructorStudentListAjaxPageData) result.data;
        assertEquals(2, data.getSections().size());
        assertTrue(data.isHasSection());
        assertEquals(1, data.getCourseIndex());
        assertEquals(instructor.courseId, data.getCourseId());
    }

    @Override
    protected InstructorStudentListAjaxPageAction getAction(String... params) {
        return (InstructorStudentListAjaxPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor3OfCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COURSE_INDEX, "1"
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
