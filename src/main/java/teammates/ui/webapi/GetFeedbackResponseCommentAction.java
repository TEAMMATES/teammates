package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackResponseCommentData;

/**
 * Get all the comments given by the user for a response.
 */
public class GetFeedbackResponseCommentAction extends BasicCommentSubmissionAction {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.ALL_ACCESS;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        throw new UnauthorizedAccessException("Backdoor only API.");
    }

    @Override
    public JsonResult execute() {
        UUID feedbackResponseId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        FeedbackResponseComment comment =
                logic.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);
        return new JsonResult(new FeedbackResponseCommentData(comment));
    }
}
