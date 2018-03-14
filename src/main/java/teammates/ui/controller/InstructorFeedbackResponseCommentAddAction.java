package teammates.ui.controller;

import java.time.Instant;
import java.util.ArrayList;

import com.google.appengine.api.datastore.Text;

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
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        String feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionId);
        String feedbackResponseId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseId);
        String commentId = getRequestParamValue(Const.ParamsNames.COMMENT_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COMMENT_ID, commentId);

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
                new InstructorFeedbackResponseCommentAjaxPageData(account, sessionToken);

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
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, commentText);
        if (commentText.trim().isEmpty()) {
            data.errorMessage = Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY;
            data.isError = true;
            return createAjaxResult(data);
        }

        FeedbackResponseCommentAttributes feedbackResponseComment = FeedbackResponseCommentAttributes
                .builder(courseId, feedbackSessionName, instructor.email, new Text(commentText))
                .withFeedbackQuestionId(feedbackQuestionId)
                .withFeedbackResponseId(feedbackResponseId)
                .withCreatedAt(Instant.now())
                .withGiverSection(response.giverSection)
                .withReceiverSection(response.recipientSection)
                .build();

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

        FeedbackResponseCommentAttributes createdComment = null;
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

        if (createdComment == null) {
            data.showCommentToString = "";
            data.showGiverNameToString = "";
        } else {
            data.showCommentToString = StringHelper.toString(createdComment.showCommentTo, ",");
            data.showGiverNameToString = StringHelper.toString(createdComment.showGiverNameTo, ",");
        }

        data.comment = createdComment;
        data.commentId = commentId;
        data.instructorEmailNameTable = bundle.instructorEmailNameTable;
        data.question = logic.getFeedbackQuestion(feedbackQuestionId);
        data.sessionTimeZone = session.getTimeZone();

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENTS_ADD, data);
    }

}
