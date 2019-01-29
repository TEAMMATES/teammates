package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.action.GetFeedbackQuestionRecipientsAction;

/**
 * SUT: {@link GetFeedbackQuestionRecipientsAction}.
 */
public class GetFeedbackQuestionRecipientsActionTest extends BaseActionTest<GetFeedbackQuestionRecipientsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTION_RECIPIENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
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
