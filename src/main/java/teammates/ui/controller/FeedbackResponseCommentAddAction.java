package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Date;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.pagedata.FeedbackResponseCommentAjaxPageData;

public abstract class FeedbackResponseCommentAddAction extends Action {

    protected String courseId;
    protected String feedbackSessionName;
    protected String feedbackQuestionId;
    protected String feedbackResponseId;
    protected String commentId;
    protected boolean isModeration;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionId);
        feedbackResponseId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseId);
        commentId = getRequestParamValue(Const.ParamsNames.COMMENT_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COMMENT_ID, commentId);

        isModeration = false;
        String moderatedPersonEmail = "";
        if (getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON) != null) {
            isModeration = true;
            moderatedPersonEmail = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        }

        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);
        
        verifyAccessibleForSpecificUser(session, response);

        FeedbackResponseCommentAjaxPageData data =
                new FeedbackResponseCommentAjaxPageData(account, sessionToken);

        String userEmailForCourse = isModeration ? moderatedPersonEmail : getUserEmailForCourse();
        FeedbackSessionResultsBundle bundle =
                getDataBundle(userEmailForCourse);
        String giverEmail = response.giver;
        String recipientEmail = response.recipient;

        String giverName = bundle.getGiverNameForResponse(response);
        String giverTeamName = bundle.getTeamNameForEmail(giverEmail);
        data.giverName = bundle.appendTeamNameToName(giverName, giverTeamName);

        String recipientName = bundle.getRecipientNameForResponse(response);
        String recipientTeamName = bundle.getTeamNameForEmail(recipientEmail);
        data.recipientName = bundle.appendTeamNameToName(recipientName, recipientTeamName);

        //Set up comment text
        String commentText = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, commentText);
        if (commentText.trim().isEmpty()) {
            data.errorMessage = Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY;
            data.isError = true;
            return createAjaxResult(data);
        }

        FeedbackResponseCommentAttributes feedbackResponseComment = new FeedbackResponseCommentAttributes(courseId,
                feedbackSessionName, feedbackQuestionId, userEmailForCourse, feedbackResponseId, new Date(),
                new Text(commentText), response.giverSection, response.recipientSection);

        //Set up visibility settings
        String showCommentTo = getRequestParamValue(Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO);
        String showGiverNameTo = getRequestParamValue(Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO);
        feedbackResponseComment.showCommentTo = new ArrayList<>();
        if (showCommentTo != null && !showCommentTo.isEmpty()) {
            String[] showCommentToArray = showCommentTo.split(",");
            for (String viewer : showCommentToArray) {
                feedbackResponseComment.showCommentTo.add(FeedbackParticipantType.valueOf(viewer.trim()));
            }
        }
        feedbackResponseComment.showGiverNameTo = new ArrayList<>();
        if (showGiverNameTo != null && !showGiverNameTo.isEmpty()) {
            String[] showGiverNameToArray = showGiverNameTo.split(",");
            for (String viewer : showGiverNameToArray) {
                feedbackResponseComment.showGiverNameTo.add(FeedbackParticipantType.valueOf(viewer.trim()));
            }
        }

        FeedbackResponseCommentAttributes createdComment = new FeedbackResponseCommentAttributes();
        try {
            createdComment = logic.createFeedbackResponseComment(feedbackResponseComment);
            logic.putDocument(createdComment);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            data.errorMessage = e.getMessage();
            data.isError = true;
        }

        if (!data.isError) {
            setStatusToAdmin(feedbackResponseComment);
        }

        data.comment = createdComment;
        data.commentId = commentId;
        data.showCommentToString = StringHelper.toString(createdComment.showCommentTo, ",");
        data.showGiverNameToString = StringHelper.toString(createdComment.showGiverNameTo, ",");
        data.commentGiverNameEmailTable = bundle.commentGiverEmailNameTable;
        data.question = logic.getFeedbackQuestion(feedbackQuestionId);
        data.commentGiverInstructor = isInstructor();
        data.moderation = isModeration;
        data.moderatedPersonEmail = moderatedPersonEmail;

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENTS_ADD, data);
    }

    protected abstract boolean isSpecificUserJoinedCourse();

    protected abstract void verifyAccessibleForSpecificUser(FeedbackSessionAttributes fsa,
            FeedbackResponseAttributes response);
    
    protected abstract String getUserEmailForCourse();

    protected abstract FeedbackSessionResultsBundle getDataBundle(String userEmailForCourse)
            throws EntityDoesNotExistException;

    protected abstract void setStatusToAdmin(FeedbackResponseCommentAttributes feedbackResponseComment);

    protected abstract boolean isInstructor();
}
