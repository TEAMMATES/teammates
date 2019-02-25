package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.StatusMessageColor;
import teammates.ui.webapi.action.GetCourseEnrollPageDataAction;
import teammates.ui.webapi.action.GetCourseEnrollPageDataAction.CourseEnrollPageData;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link GetCourseEnrollPageDataAction}.
 */
public class GetCourseEnrollPageDataActionTest extends BaseActionTest<GetCourseEnrollPageDataAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_ENROLL_PAGE_DATA;
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

        ______TS("Course is present and has existing responses");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
        };

        GetCourseEnrollPageDataAction a = getAction(submissionParams);
        JsonResult result = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        CourseEnrollPageData output = (CourseEnrollPageData) result.getOutput();

        assertTrue(output.isCoursePresent());
        assertEquals(Const.StatusMessages.COURSE_ENROLL_POSSIBLE_DATA_LOSS,
                output.getStatusMessage().getText());
        assertEquals(StatusMessageColor.WARNING.name().toLowerCase(),
                output.getStatusMessage().getColor());

        ______TS("Course is not present");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "invalidcourse",
        };

        a = getAction(submissionParams);
        result = getJsonResult(a);

        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());
        MessageOutput msgOutput = (MessageOutput) result.getOutput();

        assertEquals("Invalid course", msgOutput.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyAnyLoggedInUserCanAccess();
    }
}
