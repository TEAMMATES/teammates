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
        frc.setInstructorAllowedToDelete(true);
        frc.setInstructorAllowedToEdit(true);

        return frc;
    }

    public String getCommentIdFirstIndex() {
        return commentId.split("-")[0];
    }

    public String getCommentIdSecondIndex() {
        return commentId.split("-")[1];
    }

    public String getCommentIdThirdIndex() {
        return commentId.split("-")[2];
    }

    public String getCommentIdFrcIndex() {
        return commentId.split("-")[3];
    }
}
