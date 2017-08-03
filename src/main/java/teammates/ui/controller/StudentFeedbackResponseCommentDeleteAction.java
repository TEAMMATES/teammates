package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

public class StudentFeedbackResponseCommentDeleteAction extends FeedbackResponseCommentDeleteAction {

    @Override
    protected void verifyAccessibleForSpecificUser(FeedbackSessionAttributes fsa, FeedbackResponseAttributes response) {
        StudentAttributes student = logic.getStudentForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(student, logic.getFeedbackSession(feedbackSessionName, courseId));
    }

    @Override
    protected void setStatusToAdmin(FeedbackResponseCommentAttributes feedbackResponseComment) {
        statusToAdmin += "StudentFeedbackResponseCommentDeleteAction:<br>"
                + "Deleting feedback response comment: " + feedbackResponseComment.getId() + "<br>"
                + "in course/feedback session: " + courseId + "/" + feedbackSessionName + "<br>";
    }
}
