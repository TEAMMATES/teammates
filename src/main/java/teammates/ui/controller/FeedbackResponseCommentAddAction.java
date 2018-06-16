package teammates.ui.controller;

import java.time.Instant;
import java.util.ArrayList;

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
    protected String giverRole;

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
        giverRole = getRequestParamValue(Const.ParamsNames.COMMENT_GIVER_TYPE);

        if (!isSpecificUserJoinedCourse()) {
            return createPleaseJoinCourseResponse(courseId);
        }

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
        String giverEmail = response.giver;
        String recipientEmail = response.recipient;
        FeedbackSessionResultsBundle bundle = getDataBundle(userEmailForCourse);

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

        FeedbackParticipantType commentGiverType = getCommentGiverType(giverRole);
        FeedbackResponseCommentAttributes feedbackResponseComment = FeedbackResponseCommentAttributes
                .builder(courseId, feedbackSessionName, userEmailForCourse, new Text(commentText))
                .withFeedbackQuestionId(feedbackQuestionId)
                .withFeedbackResponseId(feedbackResponseId)
                .withCreatedAt(Instant.now())
                .withGiverSection(response.giverSection)
                .withReceiverSection(response.recipientSection)
                .withCommentGiverType(commentGiverType)
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
            appendStatusToAdmin(feedbackResponseComment);
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
        data.commentGiverNameEmailTable = bundle.commentGiverEmailNameTable;
        data.question = logic.getFeedbackQuestion(feedbackQuestionId);
        data.commentGiverType = commentGiverType;
        data.moderation = isModeration;
        data.moderatedPersonEmail = moderatedPersonEmail;
        data.sessionTimeZone = session.getTimeZone();

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENTS_ADD, data);
    }

    protected abstract boolean isSpecificUserJoinedCourse();

    protected abstract void appendStatusToAdmin(FeedbackResponseCommentAttributes feedbackResponseComment);

    protected abstract void verifyAccessibleForSpecificUser(FeedbackSessionAttributes fsa,
                                                            FeedbackResponseAttributes response);

    protected abstract FeedbackSessionResultsBundle getDataBundle(String userEmailForCourse)
            throws EntityDoesNotExistException;

    protected abstract String getUserEmailForCourse();

    private FeedbackParticipantType getCommentGiverType(String commentGiverRole) {
        if (commentGiverRole.equals(Const.STUDENT)) {
            return FeedbackParticipantType.STUDENTS;
        }
        if (commentGiverRole.equals(Const.INSTRUCTOR)) {
            return FeedbackParticipantType.INSTRUCTORS;
        }
        return null;
    }
}
