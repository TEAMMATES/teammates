package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorCourseInstructorCopyPageAction;
import teammates.ui.controller.ShowPageResult;

/**
 * SUT: {@link teammates.ui.controller.InstructorCourseInstructorCopyPageAction}.
 */
public class InstructorCourseInstructorCopyPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_COPY_PAGE;
    }

    @Override
    protected InstructorCourseInstructorCopyPageAction getAction(String... params) {
        return (InstructorCourseInstructorCopyPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
        verifyUnaccessibleWithoutModifySessionPrivilege(params);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        // choose instructor3 because this instructor is the co-owner of both Course1 and Course
        gaeSimulation.loginAsInstructor("idOfInstructor3");

        ______TS("typical success case");

        String[] submissionParams = new String[] { Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1" };

        InstructorCourseInstructorCopyPageAction action = getAction(submissionParams);
        ShowPageResult result = getShowPageResult(action);

        String expectedString = getPageResultDestination(
                Const.ViewURIs.INSTRUCTOR_COURSE_INSTRUCTOR_COPY_MODAL, false, "idOfInstructor3");

        assertEquals(expectedString, result.getDestinationWithParams());
        assertTrue(result.getStatusMessage().isEmpty());

        ______TS("failure: non-existent course");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "INVALID COURSE"
        };

        action = getAction(submissionParams);
        try {
            result = getShowPageResult(action);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException uae) {
            assertEquals("Trying to access system using a non-existent instructor entity",
                    uae.getMessage());
        }

        ______TS("failure: insufficient permissions");
        gaeSimulation.loginAsInstructor(typicalBundle.accounts.get("helperOfCourse1").googleId);

        submissionParams = new String[] { Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1" };

        action = getAction(submissionParams);
        try {
            result = getShowPageResult(action);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException uae) {
            assertEquals("Course [idOfTypicalCourse1] is not accessible to instructor [helper@course1.tmt] "
                            + "for privilege [canmodifyinstructor]",
                    uae.getMessage());
        }
    }
}
