package teammates.test.cases.newaction;

import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.newcontroller.SearchAccountsAction;

/**
 * SUT: {@link SearchAccountsAction}.
 */
public class SearchAccountsActionTest extends BaseActionTest<SearchAccountsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNTS;
    }

    @Override
    protected String getRequestMethod() {
        return HttpGet.METHOD_NAME;
    }

    @Override
    @Test
    protected void testExecute() {
        // TODO
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
