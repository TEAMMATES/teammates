package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.FeedbackResponseCommentCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new feedback response comment.
 */
public class CreateFeedbackResponseCommentAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackResponseId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);

        FeedbackResponse feedbackResponse = logic.getFeedbackResponse(feedbackResponseId);
        if (feedbackResponse == null) {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        String courseId = feedbackResponse.getFeedbackQuestion().getCourseId();
        FeedbackQuestion feedbackQuestion = feedbackResponse.getFeedbackQuestion();
        FeedbackSession session = feedbackQuestion.getFeedbackSession();

        Instructor instructor = getInstructorFromRequest(courseId);
        ResponseGiver giver = feedbackResponse.getGiver();
        String giverSectionName = giver.getSectionName();
        ResponseRecipient recipient = feedbackResponse.getRecipient();
        String recipientSectionName = recipient.getSectionName();
        gateKeeper.verifyAccessible(instructor, session, giverSectionName,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
        gateKeeper.verifyAccessible(instructor, session, recipientSectionName,
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
        if (!feedbackQuestion.getQuestionDetailsCopy().isInstructorCommentsOnResponsesAllowed()) {
            throw new InvalidHttpParameterException("Invalid question type for instructor comment");
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID feedbackResponseId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);

        FeedbackResponse feedbackResponse = logic.getFeedbackResponse(feedbackResponseId);
        if (feedbackResponse == null) {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        String courseId = feedbackResponse.getFeedbackQuestion().getCourseId();

        FeedbackResponseCommentCreateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentCreateRequest.class);

        Instructor instructor = getInstructorFromRequest(courseId);
        ResponseGiver giverRg = new ResponseGiver(instructor);
        boolean isFromParticipant = false;
        boolean isFollowingQuestionVisibility = false;

        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(giverRg,
                comment.getCommentText(), isFollowingQuestionVisibility, isFromParticipant,
                comment.getShowCommentTo(), comment.getShowGiverNameTo(), giverRg);
        feedbackResponse.addFeedbackResponseComment(feedbackResponseComment);
        try {
            FeedbackResponseComment createdComment = logic.createFeedbackResponseComment(feedbackResponseComment);
            HibernateUtil.flushSession();
            return new JsonResult(new FeedbackResponseCommentData(createdComment));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException(e);
        }
    }
}
