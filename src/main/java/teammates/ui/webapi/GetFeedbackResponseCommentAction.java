package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackResponseCommentData;

/**
 * Get all the comments given by the user for a response.
 */
public class GetFeedbackResponseCommentAction extends AdminOnlyAction {
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
        if (comment == null) {
            throw new EntityNotFoundException("No comment found for feedback response id: " + feedbackResponseId);
        }
        return new JsonResult(new FeedbackResponseCommentData(comment));
    }
}
