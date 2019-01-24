package teammates.test.cases.newaction;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.newcontroller.RemindFeedbackSessionResultAction;

/**
 * SUT: {@link RemindFeedbackSessionResultAction}.
 */
public class RemindFeedbackSessionResultTest extends BaseActionTest<RemindFeedbackSessionResultAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_REMIND_RESULT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
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
