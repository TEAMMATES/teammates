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
        FeedbackResponseComment frc = new FeedbackResponseComment(comment, comment.giverEmail);

        frc.setResponseVisibleToRecipient(true);
        frc.setResponseVisibleToGiverTeam(true);
        frc.setResponseVisibleToRecipientTeam(true);
        frc.setResponseVisibleToStudents(true);
        frc.setResponseVisibleToInstructors(true);
        frc.setEditDeleteEnabled(true);
        frc.setInstructorAllowedToDelete(true);
        frc.setInstructorAllowedToEdit(true);
        frc.setResponseGiverName(giverName);
        frc.setResponseRecipientName(recipientName);
        frc.setShowCommentToString(showCommentToString);
        frc.setShowGiverNameToString(showGiverNameToString);

        commentIds = commentId.split("-");

        return frc;
    }

    public String[] getCommendIds() {
        return commentIds;
    }
}
