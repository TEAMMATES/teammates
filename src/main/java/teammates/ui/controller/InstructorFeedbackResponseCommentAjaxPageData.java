package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;

public class InstructorFeedbackResponseCommentAjaxPageData extends PageData {

    public FeedbackResponseCommentAttributes comment;
    public boolean isError;
    public String errorMessage;
    
    public InstructorFeedbackResponseCommentAjaxPageData(AccountAttributes account) {
        super(account);
    }
}
