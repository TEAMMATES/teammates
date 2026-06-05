package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.request.FeedbackQuestionUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates a feedback question.
 */
public class UpdateFeedbackQuestionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Unknown question id");
        }

        Instructor instructor = getInstructorFromRequest(feedbackQuestion.getCourseId());
        FeedbackSession feedbackSession = getNonNullFeedbackSession(
                feedbackQuestion.getFeedbackSession().getName(), feedbackQuestion.getCourseId());
        gateKeeper.verifyInstructorCanAccessSession(instructor, feedbackSession);
        gateKeeper.verifyAccessible(instructor, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Unknown question id");
        }

        FeedbackQuestionUpdateRequest updateRequest = getAndValidateRequestBody(FeedbackQuestionUpdateRequest.class);

        FeedbackQuestion updatedQuestion;
        try {
            updatedQuestion = logic.updateFeedbackQuestionCascade(feedbackQuestionId, updateRequest);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult(new FeedbackQuestionData(updatedQuestion));
    }

}
