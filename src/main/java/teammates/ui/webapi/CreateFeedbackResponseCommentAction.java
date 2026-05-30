package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
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

        FeedbackResponseCommentCreateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentCreateRequest.class);

        FeedbackResponse feedbackResponse = logic.getFeedbackResponse(feedbackResponseId);
        if (feedbackResponse == null) {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        String courseId = feedbackResponse.getFeedbackQuestion().getCourseId();
        Instructor instructor = getInstructorFromRequest(courseId);
        ResponseGiver giverRg = new ResponseGiver(instructor);

        try {
            FeedbackResponseComment createdComment = logic.createFeedbackResponseComment(feedbackResponseId, giverRg,
                    comment.getCommentText(), comment.getShowCommentTo(), comment.getShowGiverNameTo());
            HibernateUtil.flushSession();
            return new JsonResult(new FeedbackResponseCommentData(createdComment));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }
}
