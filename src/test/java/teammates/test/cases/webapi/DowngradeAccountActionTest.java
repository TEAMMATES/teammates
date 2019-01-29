package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.action.DowngradeAccountAction;

/**
 * SUT: {@link DowngradeAccountAction}.
 */
public class DowngradeAccountActionTest extends BaseActionTest<DowngradeAccountAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNTS_DOWNGRADE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
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
