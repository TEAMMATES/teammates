package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.datastore.Text;

public class InstructorFeedbackResponseCommentEditAction extends Action {
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("null course id", courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull("null feedback session name", feedbackSessionName);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        
        new GateKeeper().verifyAccessible(
                instructor, 
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false);
        
        InstructorFeedbackResponseCommentAjaxPageData data = 
                new InstructorFeedbackResponseCommentAjaxPageData(account);
        
        String feedbackResponseCommentId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        Assumption.assertNotNull("null response comment id", feedbackResponseCommentId);
        
        String commentText = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT);
        Assumption.assertNotNull("null comment text", commentText);
        if (commentText.trim().isEmpty()) {
            data.errorMessage = Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY;
            data.isError = true;
            return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT, data);
        }
        
        FeedbackResponseCommentAttributes frc = new FeedbackResponseCommentAttributes(
                courseId, feedbackSessionName, null, instructor.email, null, null,
                new Text(commentText));
        frc.setId(Long.parseLong(feedbackResponseCommentId));
        
        try {
            logic.updateFeedbackResponseComment(frc);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            data.errorMessage = e.getMessage();
            data.isError = true;
        }
        
        if (!data.isError) {
            statusToAdmin += "InstructorFeedbackResponseCommentEditAction:<br>"
                    + "Editing feedback response comment: " + frc.getId() + "<br>"
                    + "in course/feedback session: " + frc.courseId + "/" + frc.feedbackSessionName + "<br>"
                    + "by: " + frc.giverEmail + "<br>"
                    + "comment text: " + frc.commentText.getValue();
        }
        
        data.comment = frc;

        return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT, data);
    }
}
