package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.action.GetOngoingSessionsAction;

/**
 * SUT: {@link GetOngoingSessionsAction}.
 */
public class GetOngoingSessionsActionTest extends BaseActionTest<GetOngoingSessionsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSIONS_ADMIN;
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
