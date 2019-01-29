package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.action.DeleteFeedbackResponseAction;

/**
 * SUT: {@link DeleteFeedbackResponseAction}.
 */
public class DeleteFeedbackResponseActionTest extends BaseActionTest<DeleteFeedbackResponseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
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
