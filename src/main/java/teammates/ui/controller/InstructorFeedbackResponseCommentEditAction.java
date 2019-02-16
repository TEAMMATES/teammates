package teammates.ui.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.FeedbackResponseCommentAjaxPageData;

/**
 * Action: Edit {@link FeedbackResponseCommentAttributes}.
 */
public class InstructorFeedbackResponseCommentEditAction extends InstructorFeedbackResponseCommentAbstractAction {
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        String feedbackResponseId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseId);
        String feedbackResponseCommentId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseCommentId);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        FeedbackResponseCommentAttributes frc =
                logic.getFeedbackResponseComment(Long.parseLong(feedbackResponseCommentId));
        Assumption.assertNotNull("FeedbackResponseComment should not be null", frc);
        verifyAccessibleForInstructorToFeedbackResponseComment(
                feedbackResponseCommentId, instructor, session, response);

        FeedbackResponseCommentAjaxPageData data =
                new FeedbackResponseCommentAjaxPageData(account, sessionToken);

        // Edit comment text
        String commentText = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, commentText);
        if (commentText.trim().isEmpty()) {
            data.errorMessage = Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY;
            data.isError = true;
            return createAjaxResult(data);
        }

        FeedbackResponseCommentAttributes.UpdateOptions.Builder commentUpdateOptions =
                FeedbackResponseCommentAttributes.updateOptionsBuilder(Long.parseLong(feedbackResponseCommentId))
                        .withCommentText(commentText)
                        .withLastEditorEmail(instructor.email)
                        .withLastEditorAt(Instant.now());

        // edit visibility settings
        String showCommentTo = getRequestParamValue(Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO);
        String showGiverNameTo = getRequestParamValue(Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO);
        if (showCommentTo != null && !showCommentTo.isEmpty()) {
            String[] showCommentToArray = showCommentTo.split(",");
            List<FeedbackParticipantType> showCommentToList = new ArrayList<>();
            for (String viewer : showCommentToArray) {
                showCommentToList.add(FeedbackParticipantType.valueOf(viewer.trim()));
            }
            commentUpdateOptions.withShowCommentTo(showCommentToList);
        }
        if (showGiverNameTo != null && !showGiverNameTo.isEmpty()) {
            String[] showGiverNameToArray = showGiverNameTo.split(",");
            List<FeedbackParticipantType> showGiverNameToList = new ArrayList<>();
            for (String viewer : showGiverNameToArray) {
                showGiverNameToList.add(FeedbackParticipantType.valueOf(viewer.trim()));
            }
            commentUpdateOptions.withShowGiverNameTo(showGiverNameToList);
        }

        FeedbackResponseCommentAttributes updatedComment = null;
        try {
            updatedComment = logic.updateFeedbackResponseComment(commentUpdateOptions.build());
            //TODO: move putDocument to task queue
            logic.putDocument(updatedComment);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            data.errorMessage = e.getMessage();
            data.isError = true;
        }

        if (!data.isError) {
            statusToAdmin += "InstructorFeedbackResponseCommentEditAction:<br>"
                           + "Editing feedback response comment: " + updatedComment.getId() + "<br>"
                           + "in course/feedback session: " + updatedComment.courseId + "/"
                           + updatedComment.feedbackSessionName + "<br>"
                           + "by: " + updatedComment.commentGiver + "<br>"
                           + "comment text: " + updatedComment.commentText;

            String commentGiverName = logic.getInstructorForEmail(courseId, frc.commentGiver).name;
            String commentEditorName = instructor.name;

            // createdAt and lastEditedAt fields in updatedComment as well as sessionTimeZone
            // are required to generate timestamps in editedCommentDetails
            data.comment = updatedComment;
            data.sessionTimeZone = session.getTimeZone();

            data.editedCommentDetails = data.createEditedCommentDetails(commentGiverName, commentEditorName);
        }

        return createAjaxResult(data);
    }
}
