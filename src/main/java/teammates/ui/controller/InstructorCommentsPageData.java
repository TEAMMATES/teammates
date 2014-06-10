package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;

public class InstructorCommentsPageData extends PageData {
    public Boolean isViewingDraft;
    public Boolean isDisplayArchive;
    public String courseId;
    public String courseName;
    public List<String> coursePaginationList;
    public Map<String, List<CommentAttributes>> comments;
    public CourseRoster roster;
    public Map<String, FeedbackSessionResultsBundle> feedbackResultBundles;
    
    public InstructorCommentsPageData(AccountAttributes account) {
        super(account);
    }
}
