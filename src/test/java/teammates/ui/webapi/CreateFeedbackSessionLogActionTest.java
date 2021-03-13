package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link CreateFeedbackSessionLogAction}.
 */
public class CreateFeedbackSessionLogActionTest extends BaseActionTest<CreateFeedbackSessionLogAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.TRACK_SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // TODO: test execute
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        // TODO: test access control
    }
}
