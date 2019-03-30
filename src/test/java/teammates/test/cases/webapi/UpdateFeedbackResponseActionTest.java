package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.action.UpdateFeedbackResponseAction;

/**
 * SUT: {@link UpdateFeedbackResponseAction}.
 */
public class UpdateFeedbackResponseActionTest extends BaseActionTest<UpdateFeedbackResponseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // TODO
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        // TODO
    }

}
