package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.SearchAccountsAction;
import teammates.ui.webapi.output.AdminSearchResultData;

/**
 * SUT: {@link SearchAccountsAction}.
 */
public class SearchAccountsActionTest extends BaseActionTest<SearchAccountsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNTS_SEARCH;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes acc = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsAdmin();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case: search google id");

        String[] submissionParams = new String[] {
                Const.ParamsNames.ADMIN_SEARCH_KEY, acc.getGoogleId(),
        };

        SearchAccountsAction a = getAction(submissionParams);
        JsonResult result = getJsonResult(a);
        AdminSearchResultData response = (AdminSearchResultData) result.getOutput();

        assertTrue(response.getStudents().isEmpty());
        assertEquals(1, response.getInstructors().size());


    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
