package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

public class StudentFeedbackResponseCommentDeleteAction extends FeedbackResponseCommentDeleteAction {
    @Override
    protected void verifyAccessibleForUserToFeedbackResponseComment(FeedbackSessionAttributes session, FeedbackResponseAttributes response) {
        if (isModeration) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
            gateKeeper.verifyAccessible(instructor, session, false, response.giverSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, session, false, response.recipientSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        } else {
            StudentAttributes student = logic.getStudentForGoogleId(courseId, account.googleId);
            gateKeeper.verifyAccessible(student, logic.getFeedbackSession(feedbackSessionName, courseId));
        }
    }

    @Override
    protected void appendToStatusToAdmin(Long commentId) {
        statusToAdmin += "StudentFeedbackResponseCommentDeleteAction:<br>"
                + "Deleting feedback response comment: " + commentId + "<br>"
                + "in course/feedback session: " + courseId + "/" + feedbackSessionName + "<br>";
    }
}
