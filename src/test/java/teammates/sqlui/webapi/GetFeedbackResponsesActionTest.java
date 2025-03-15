package teammates.sqlui.webapi;

import teammates.common.util.Const;
import teammates.ui.webapi.GetFeedbackResponsesAction;

/**
 * SUT: {@link GetFeedbackResponsesAction}.
 */
public class GetFeedbackResponsesActionTest extends BaseActionTest<GetFeedbackResponsesAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }
}
