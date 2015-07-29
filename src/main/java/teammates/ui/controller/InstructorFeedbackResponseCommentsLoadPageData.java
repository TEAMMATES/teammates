package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.ui.template.InstructorFeedbackResponseComment;

public class InstructorFeedbackResponseCommentsLoadPageData extends PageData {

    public FeedbackSessionResultsBundle feedbackResultBundle;
    public InstructorAttributes currentInstructor = null;
    public String instructorEmail = "";
    public CourseRoster roster = null;
    public int numberOfPendingComments = 0;
    public int feedbackSessionIndex = 0;
    private InstructorFeedbackResponseComment instructorFeedbackResponseComment;
    
    public InstructorFeedbackResponseCommentsLoadPageData(AccountAttributes account) {
        super(account);
    }

    public FeedbackSessionResultsBundle getFeedbackResultsBundle() {
        return feedbackResultBundle;
    }

    public int getNumberOfPendingComments() {
        return numberOfPendingComments;
    }
    
    public InstructorFeedbackResponseComment getInstructorFeedbackResponseComment() {
        return instructorFeedbackResponseComment;
    }

    public void setInstructorFeedbackResponseComment(
            InstructorFeedbackResponseComment instructorFeedbackResponseComment) {
        this.instructorFeedbackResponseComment = instructorFeedbackResponseComment;
    }
    
    public int getFeedbackSessionIndex() {
        return feedbackSessionIndex;
    }
}
