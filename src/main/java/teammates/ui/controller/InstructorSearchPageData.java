package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;

public class InstructorSearchPageData extends PageData {

    public CommentSearchResultBundle commentSearchResultBundle;
    public FeedbackResponseCommentSearchResultBundle feedbackResponseCommentSearchResultBundle;
    
    public InstructorSearchPageData(AccountAttributes account) {
        super(account);
    }

}
