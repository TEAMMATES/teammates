package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.StudentSearchResultBundle;

public class InstructorSearchPageData extends PageData {

    public CommentSearchResultBundle commentSearchResultBundle = new CommentSearchResultBundle();
    public FeedbackResponseCommentSearchResultBundle feedbackResponseCommentSearchResultBundle = new FeedbackResponseCommentSearchResultBundle();
    public StudentSearchResultBundle studentSearchResultBundle = new StudentSearchResultBundle();
    public String searchKey = "";
    public int totalResultsSize;
    public boolean isSearchCommentForStudents;
    public boolean isSearchCommentForResponses;
    public boolean isSearchForStudents;
    
    public InstructorSearchPageData(AccountAttributes account) {
        super(account);
    }
}
