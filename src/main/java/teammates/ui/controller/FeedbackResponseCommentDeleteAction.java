package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.FeedbackResponseCommentAjaxPageData;

/**
 * Action: Delete {@link FeedbackResponseCommentAttributes}.
 */
public abstract class FeedbackResponseCommentDeleteAction extends Action {

    protected String courseId;
    protected String feedbackSessionName;

    @Override
    protected ActionResult execute() {
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        String feedbackResponseId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseId);
        String feedbackResponseCommentId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseCommentId);

        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        verifyAccessibleForSpecificUser(session, response);

        FeedbackResponseCommentAttributes feedbackResponseComment = new FeedbackResponseCommentAttributes();
        feedbackResponseComment.setId(Long.parseLong(feedbackResponseCommentId));

        logic.deleteDocument(feedbackResponseComment);
        logic.deleteFeedbackResponseComment(feedbackResponseComment);

        setStatusToAdmin(feedbackResponseComment);

        FeedbackResponseCommentAjaxPageData data =
                new FeedbackResponseCommentAjaxPageData(account, sessionToken);

        return createAjaxResult(data);
    }

    protected abstract void verifyAccessibleForSpecificUser(FeedbackSessionAttributes fsa,
            FeedbackResponseAttributes response);

    protected abstract void setStatusToAdmin(FeedbackResponseCommentAttributes feedbackResponseComment);
}
