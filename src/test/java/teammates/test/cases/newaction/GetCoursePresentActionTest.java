package teammates.test.cases.newaction;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.newcontroller.GetCoursePresentAction;
import teammates.ui.newcontroller.GetCoursePresentAction.CourseInfo;
import teammates.ui.newcontroller.JsonResult;

/**
 * SUT: {@link GetCoursePresentAction}.
 */
public class GetCoursePresentActionTest extends BaseActionTest<GetCoursePresentAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_PRESENT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() {
        ______TS("Invalid parameters");

        //no parameters
        verifyHttpParameterFailure();

        //null course id
        String[] invalidParams = new String[] {};
        verifyHttpParameterFailure(invalidParams);

        ______TS("Course is present");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };

        GetCoursePresentAction a = getAction(submissionParams);
        JsonResult result = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        CourseInfo output = (CourseInfo) result.getOutput();

        assertTrue(output.isCoursePresent());

        ______TS("Course is not present");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "invalidcourse"
        };

        a = getAction(submissionParams);
        result = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        output = (CourseInfo) result.getOutput();

        assertFalse(output.isCoursePresent());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyAnyLoggedInUserCanAccess();
    }
}
