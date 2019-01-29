package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.action.SearchAccountsAction;

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
        // TODO
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
