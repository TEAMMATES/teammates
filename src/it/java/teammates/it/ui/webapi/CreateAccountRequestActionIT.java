package teammates.it.ui.webapi;

import teammates.common.util.Const;
import teammates.ui.webapi.CreateAccountRequestAction;

/**
 * SUT: {@link CreateAccountRequestAction}.
 */
public class CreateAccountRequestActionIT extends BaseActionIT<CreateAccountRequestAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @Override
    protected void testExecute() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testExecute'");
    }

    @Override
    protected void testAccessControl() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAccessControl'");
    }

}
