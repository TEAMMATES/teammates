package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;

/**
 * Action: Create a new {@link FeedbackResponseCommentAttributes}.
 */
public class StudentFeedbackResponseCommentAddAction extends FeedbackResponseCommentAddAction {

    @Override
    protected boolean isSpecificUserJoinedCourse() {
        return true;
    }

    @Override
    protected void verifyAccessibleForSpecificUser(FeedbackSessionAttributes fsa, FeedbackResponseAttributes response) {
        if (isModeration) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
            gateKeeper.verifyAccessible(instructor, fsa, false, response.giverSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, fsa, false, response.recipientSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        } else {
            StudentAttributes student = logic.getStudentForGoogleId(courseId, account.googleId);
            gateKeeper.verifyAccessible(student, logic.getFeedbackSession(feedbackSessionName, courseId));
        }
        
    }

    @Override
    protected String getUserEmailForCourse() {
        return logic.getStudentForGoogleId(courseId, account.googleId).email;
    }

    @Override
    protected FeedbackSessionResultsBundle getDataBundle(String userEmail) throws EntityDoesNotExistException {
        return logic.getFeedbackSessionResultsForStudent(feedbackSessionName, courseId, userEmail);
    }

    @Override
    protected void setStatusToAdmin(FeedbackResponseCommentAttributes feedbackResponseComment) {
        if (isModeration) {
            String instructorEmail = logic.getInstructorForGoogleId(courseId, account.googleId).email;
            statusToAdmin += "StudentFeedbackResponseCommentAddAction:<br>"
                    + "Adding comment to response: " + feedbackResponseComment.feedbackResponseId + "<br>"
                    + "in course/feedback session: " + feedbackResponseComment.courseId + "/"
                    + feedbackResponseComment.feedbackSessionName + "<br>"
                    + "by: " + instructorEmail + " moderated as " + feedbackResponseComment.giverEmail + " at "
                    + feedbackResponseComment.createdAt + "<br>"
                    + "comment text: " + feedbackResponseComment.commentText.getValue();
        } else {
            statusToAdmin += "StudentFeedbackResponseCommentAddAction:<br>"
                    + "Adding comment to response: " + feedbackResponseComment.feedbackResponseId + "<br>"
                    + "in course/feedback session: " + feedbackResponseComment.courseId + "/"
                    + feedbackResponseComment.feedbackSessionName + "<br>"
                    + "by: " + feedbackResponseComment.giverEmail + " at "
                    + feedbackResponseComment.createdAt + "<br>"
                    + "comment text: " + feedbackResponseComment.commentText.getValue();
        }
        
    }

    @Override
    protected boolean isInstructor() {
        return false;
    }
}