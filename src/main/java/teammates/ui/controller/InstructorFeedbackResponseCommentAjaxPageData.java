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
    public String errorMessage;
    public boolean isError;
    public String[] commentIds;
    
    public InstructorFeedbackResponseCommentAjaxPageData(AccountAttributes account) {
        super(account);
    }

    public FeedbackResponseComment getComment() {
        FeedbackResponseComment frc = 
            new FeedbackResponseComment(comment, comment.giverEmail, giverName, recipientName, 
                                        showCommentToString, showGiverNameToString, true, true, true,
                                        true, true, true, true, true);

        commentIds = commentId.split("-");

        return frc;
    }

    public String[] getCommendIds() {
        return commentIds;
    }
}
