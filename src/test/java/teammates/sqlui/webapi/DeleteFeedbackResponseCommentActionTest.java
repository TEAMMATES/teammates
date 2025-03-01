package teammates.sqlui.webapi;

import teammates.common.util.Const;
import teammates.ui.webapi.DeleteFeedbackResponseCommentAction;

/**
 * SUT: {@link DeleteFeedbackResponseCommentAction}.
 */
public class DeleteFeedbackResponseCommentActionTest extends BaseActionTest<DeleteFeedbackResponseCommentAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    String getRequestMethod() {
        return DELETE;
    }

}
