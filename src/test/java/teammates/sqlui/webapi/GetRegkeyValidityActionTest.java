package teammates.sqlui.webapi;

import teammates.common.util.Const;
import teammates.ui.webapi.GetRegkeyValidityAction;

public class GetRegkeyValidityActionTest extends BaseActionTest<GetRegkeyValidityAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.AUTH_REGKEY;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }
}
