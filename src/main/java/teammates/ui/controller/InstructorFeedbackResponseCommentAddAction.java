package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Date;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.pagedata.InstructorFeedbackResponseCommentAjaxPageData;

/**
 * Action: Create a new {@link FeedbackResponseCommentAttributes}.
 */
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
        String commentId = getRequestParamValue(Const.ParamsNames.COMMENT_ID);
        Assumption.assertNotNull("null comment id", commentId);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);
        boolean isCreatorOnly = true;

        gateKeeper.verifyAccessible(instructor, session, !isCreatorOnly, response.giverSection,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        gateKeeper.verifyAccessible(instructor, session, !isCreatorOnly, response.recipientSection,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);

        InstructorFeedbackResponseCommentAjaxPageData data =
                new InstructorFeedbackResponseCommentAjaxPageData(account);

        String giverEmail = response.giver;
        String recipientEmail = response.recipient;
        FeedbackSessionResultsBundle bundle =
                logic.getFeedbackSessionResultsForInstructor(feedbackSessionName, courseId, instructor.email);

        String giverName = bundle.getGiverNameForResponse(response);
        String giverTeamName = bundle.getTeamNameForEmail(giverEmail);
        data.giverName = bundle.appendTeamNameToName(giverName, giverTeamName);

        String recipientName = bundle.getRecipientNameForResponse(response);
        String recipientTeamName = bundle.getTeamNameForEmail(recipientEmail);
        data.recipientName = bundle.appendTeamNameToName(recipientName, recipientTeamName);

        //Set up comment text
        String commentText = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT);
        Assumption.assertNotNull("null comment text", commentText);
        if (commentText.trim().isEmpty()) {
            data.errorMessage = Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY;
            data.isError = true;
            return createAjaxResult(data);
        }

        FeedbackResponseCommentAttributes feedbackResponseComment = new FeedbackResponseCommentAttributes(courseId,
                feedbackSessionName, feedbackQuestionId, instructor.email, feedbackResponseId, new Date(),
                new Text(commentText), response.giverSection, response.recipientSection);

        //Set up visibility settings
        String showCommentTo = getRequestParamValue(Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO);
        String showGiverNameTo = getRequestParamValue(Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO);
        feedbackResponseComment.showCommentTo = new ArrayList<FeedbackParticipantType>();
        if (showCommentTo != null && !showCommentTo.isEmpty()) {
            String[] showCommentToArray = showCommentTo.split(",");
            for (String viewer : showCommentToArray) {
                feedbackResponseComment.showCommentTo.add(FeedbackParticipantType.valueOf(viewer.trim()));
            }
        }
        feedbackResponseComment.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        if (showGiverNameTo != null && !showGiverNameTo.isEmpty()) {
            String[] showGiverNameToArray = showGiverNameTo.split(",");
            for (String viewer : showGiverNameToArray) {
                feedbackResponseComment.showGiverNameTo.add(FeedbackParticipantType.valueOf(viewer.trim()));
            }
        }

        //Set up sending state
        if (isResponseCommentPublicToRecipient(feedbackResponseComment) && session.isPublished()) {
            feedbackResponseComment.sendingState = CommentSendingState.PENDING;
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
            statusToAdmin += "InstructorFeedbackResponseCommentAddAction:<br>"
                           + "Adding comment to response: " + feedbackResponseComment.feedbackResponseId + "<br>"
                           + "in course/feedback session: " + feedbackResponseComment.courseId + "/"
                           + feedbackResponseComment.feedbackSessionName + "<br>"
                           + "by: " + feedbackResponseComment.giverEmail + " at "
                           + feedbackResponseComment.createdAt + "<br>"
                           + "comment text: " + feedbackResponseComment.commentText.getValue();
        }

        data.comment = createdComment;
        data.commentId = commentId;
        data.showCommentToString = StringHelper.toString(createdComment.showCommentTo, ",");
        data.showGiverNameToString = StringHelper.toString(createdComment.showGiverNameTo, ",");

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENTS_ADD, data);
    }

    private boolean isResponseCommentPublicToRecipient(FeedbackResponseCommentAttributes comment) {
        return comment.isVisibleTo(FeedbackParticipantType.GIVER)
             || comment.isVisibleTo(FeedbackParticipantType.RECEIVER)
             || comment.isVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)
             || comment.isVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
             || comment.isVisibleTo(FeedbackParticipantType.STUDENTS);
    }
}
