package teammates.ui.controller;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.ui.template.InstructorFeedbackResponseComment;

public class InstructorFeedbackResponseCommentsLoadPageData extends PageData {

    public Map<String, FeedbackSessionResultsBundle> feedbackResultBundles =
            new HashMap<String, FeedbackSessionResultsBundle>();
    public InstructorAttributes currentInstructor = null;
    public String instructorEmail = "";
    public CourseRoster roster = null;
    public int numberOfPendingComments = 0;
    public int feedbackSessionIndex = 0;
    private InstructorFeedbackResponseComment instructorFeedbackResponseComment;
    
    public InstructorFeedbackResponseCommentsLoadPageData(AccountAttributes account) {
        super(account);

        this.instructorFeedbackResponseComment = 
                new InstructorFeedbackResponseComment(
                        feedbackResultBundles, currentInstructor, instructorEmail, this);
    }

    public Map<String, FeedbackSessionResultsBundle> getFeedbackResultBundles() {
        return feedbackResultBundles;
    }

    public int getNumberOfPendingComments() {
        return numberOfPendingComments;
    }
    
    public InstructorFeedbackResponseComment getInstructorFeedbackResponseComment() {
        return instructorFeedbackResponseComment;
    }
}
