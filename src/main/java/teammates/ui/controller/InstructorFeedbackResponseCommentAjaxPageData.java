package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.ui.template.FeedbackResponseComment;

/*
 * PageData: to be used for {@link FeedbackResponseCommentAttributes} in Ajax operations
 */
public class InstructorFeedbackResponseCommentAjaxPageData extends PageData {

    public FeedbackResponseCommentAttributes comment;
    public String commentId;
    public String commentTime;
    public String giverName;
    public String recipientName;
    public String showCommentToString;
    public String showGiverNameToString;
    public boolean isError;
    public String errorMessage;
    
    public InstructorFeedbackResponseCommentAjaxPageData(AccountAttributes account) {
        super(account);
    }

    public FeedbackResponseComment getComment() {
        FeedbackResponseComment frc = new FeedbackResponseComment(comment, comment.giverEmail);
        frc.setEditDeleteEnabled(true);
        frc.setResponseGiverName(giverName);
        frc.setResponseRecipientName(recipientName);

        return frc;
    }

    public String getCommentId() {
        return commentId;
    }
}
