package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.action.RemindFeedbackSessionSubmissionAction;

/**
 * SUT: {@link RemindFeedbackSessionSubmissionAction}.
 */
public class RemindFeedbackSessionSubmissionActionTest extends BaseActionTest<RemindFeedbackSessionSubmissionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_REMIND_SUBMISSION;
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
