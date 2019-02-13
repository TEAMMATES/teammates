package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.DowngradeAccountAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link DowngradeAccountAction}.
 */
public class DowngradeAccountActionTest extends BaseActionTest<DowngradeAccountAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_DOWNGRADE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsAdmin();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case");

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1ofCourse1.getGoogleId(),
        };

        DowngradeAccountAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        MessageOutput response = (MessageOutput) r.getOutput();

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        assertEquals("Instructor account is successfully downgraded to student.", response.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
