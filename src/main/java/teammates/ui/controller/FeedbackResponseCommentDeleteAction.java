package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.FeedbackResponseCommentAjaxPageData;

public abstract class FeedbackResponseCommentDeleteAction extends Action {

    protected String courseId;
    protected String feedbackSessionName;
    protected boolean isModeration;
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

        isModeration = false;
        if (getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON) != null) {
            isModeration = true;
        }
        verifyAccessibleForUserToFeedbackResponseComment(session, response);

        Long commentId = Long.parseLong(feedbackResponseCommentId);

        logic.deleteDocumentByCommentId(commentId);
        logic.deleteFeedbackResponseCommentById(commentId);

        appendToStatusToAdmin(commentId);

        FeedbackResponseCommentAjaxPageData data =
                new FeedbackResponseCommentAjaxPageData(account, sessionToken);

        return createAjaxResult(data);
    }

    protected abstract void verifyAccessibleForUserToFeedbackResponseComment(FeedbackSessionAttributes session, FeedbackResponseAttributes response);

    protected abstract void appendToStatusToAdmin(Long commentId);
}
