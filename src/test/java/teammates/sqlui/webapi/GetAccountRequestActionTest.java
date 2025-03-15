package teammates.sqlui.webapi;

import teammates.common.util.Const;
import teammates.ui.webapi.GetAccountRequestAction;

/*
 * SUT: {@link GetAccountRequestAction}.
 */
public class GetAccountRequestActionTest extends BaseActionTest<GetAccountRequestAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }
}
