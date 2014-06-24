package teammates.ui.controller;

import java.util.Date;

import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
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
        String feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        Assumption.assertNotNull("null feedback question id", feedbackQuestionId);
        String feedbackResponseId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        Assumption.assertNotNull("null feedback response id", feedbackResponseId);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);
        boolean isCreatorOnly = true;
        
        new GateKeeper().verifyAccessible(instructor, session, !isCreatorOnly, 
                response.giverSection, feedbackSessionName,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        new GateKeeper().verifyAccessible(instructor, session, !isCreatorOnly, 
                response.recipientSection, feedbackSessionName,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        
        InstructorFeedbackResponseCommentAjaxPageData data = 
                new InstructorFeedbackResponseCommentAjaxPageData(account);
        
        String commentText = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT);
        Assumption.assertNotNull("null comment text", commentText);
        if (commentText.trim().isEmpty()) {
            data.errorMessage = Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY;
            data.isError = true;
            return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
        }
        
        FeedbackResponseCommentAttributes feedbackResponseComment = new FeedbackResponseCommentAttributes(courseId,
            feedbackSessionName, feedbackQuestionId, instructor.email, feedbackResponseId, new Date(),
            new Text(commentText));
        
        try {
            logic.createFeedbackResponseComment(feedbackResponseComment);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            data.errorMessage = e.getMessage();
            data.isError = true;
        }
        
        if (!data.isError) {
            statusToAdmin += "InstructorFeedbackResponseCommentAddAction:<br>"
                    + "Adding comment to response: " + feedbackResponseComment.feedbackResponseId + "<br>"
                    + "in course/feedback session: " + feedbackResponseComment.courseId + "/" 
                    + feedbackResponseComment.feedbackSessionName + "<br>"
                    + "by: " + feedbackResponseComment.giverEmail + " at " + feedbackResponseComment.createdAt + "<br>"
                    + "comment text: " + feedbackResponseComment.commentText.getValue();
        }

        // Wait for the operation to persist
        int elapsedTime = 0;
        data.comment = logic.getFeedbackResponseComment(
                feedbackResponseComment.feedbackResponseId, 
                feedbackResponseComment.giverEmail, 
                feedbackResponseComment.createdAt);
        while ((data.comment == null) &&
                (elapsedTime < Config.PERSISTENCE_CHECK_DURATION)) {
            ThreadHelper.waitBriefly();
            data.comment = logic.getFeedbackResponseComment(
                    feedbackResponseComment.feedbackResponseId, 
                    feedbackResponseComment.giverEmail, 
                    feedbackResponseComment.createdAt);
            //check before incrementing to avoid boundary case problem
            if (data.comment == null) {
                elapsedTime += ThreadHelper.WAIT_DURATION;
            }
        }
        if (elapsedTime == Config.PERSISTENCE_CHECK_DURATION) {
            log.severe("Operation did not persist in time: getFeedbackResponseComment "
                    + feedbackResponseComment.feedbackResponseId + ", "
                    + feedbackResponseComment.giverEmail + ", "
                    + feedbackResponseComment.createdAt);
        }
        
        return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
    }
}
