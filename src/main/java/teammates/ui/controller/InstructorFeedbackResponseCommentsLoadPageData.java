package teammates.ui.controller;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;

public class InstructorFeedbackResponseCommentsLoadPageData extends PageData {

    public Map<String, FeedbackSessionResultsBundle> feedbackResultBundles = new HashMap<String, FeedbackSessionResultsBundle>();
    public InstructorAttributes currentInstructor = null;
    public String instructorEmail = "";
    public CourseRoster roster = null;
    public int numberOfPendingComments = 0;
    
    public InstructorFeedbackResponseCommentsLoadPageData(
            AccountAttributes account) {
        super(account);
    }

    public boolean isResponseCommentPublicToRecipient(FeedbackResponseCommentAttributes comment) {
        return comment.showCommentTo.size() > 0;
    }
}
