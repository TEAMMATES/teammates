package teammates.test.cases.newaction;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.newcontroller.PublishFeedbackSessionAction;

/**
 * SUT: {@link PublishFeedbackSessionAction}.
 */
public class PublishFeedbackSessionActionTest extends BaseActionTest<PublishFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_PUBLISH;
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
