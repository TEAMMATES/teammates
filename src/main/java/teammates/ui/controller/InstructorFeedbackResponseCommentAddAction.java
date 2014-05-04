package teammates.ui.controller;

import java.util.Date;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.datastore.Text;

public class InstructorFeedbackResponseCommentAddAction extends Action {

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
        
        String commentText = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT);
        Assumption.assertNotNull("null comment text", commentText);
        if (commentText.trim().isEmpty()) {
            data.errorMessage = Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY;
            data.isError = true;
            return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT, data);
        }
        String feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        Assumption.assertNotNull("null feedback question id", feedbackQuestionId);
        String feedbackResponseId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        Assumption.assertNotNull("null feedback response id", feedbackResponseId);
        
        FeedbackResponseCommentAttributes frc = new FeedbackResponseCommentAttributes(courseId,
            feedbackSessionName, feedbackQuestionId, instructor.email, feedbackResponseId, new Date(),
            new Text(commentText));
        
        try {
            logic.createFeedbackResponseComment(frc);
        } catch (EntityAlreadyExistsException e) {
            setStatusForException(e);
            data.errorMessage = e.getMessage();
            data.isError = true;
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            data.errorMessage = e.getMessage();
            data.isError = true;
        }
        
        if (!data.isError) {
            statusToAdmin += "InstructorFeedbackResponseCommentAddAction:<br>"
                    + "Adding comment to response: " + frc.feedbackResponseId + "<br>"
                    + "in course/feedback session: " + frc.courseId + "/" + frc.feedbackSessionName + "<br>"
                    + "by: " + frc.giverEmail + " at " + frc.createdAt + "<br>"
                    + "comment text: " + frc.commentText.getValue();
        }

        data.comment = logic.getFeedbackResponseComment(frc.feedbackResponseId, frc.giverEmail, frc.createdAt);
        while (data.comment == null) {
            ThreadHelper.waitBriefly();
            data.comment = logic.getFeedbackResponseComment(frc.feedbackResponseId, frc.giverEmail, frc.createdAt);
        }
        
        return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT, data);
    }
}
