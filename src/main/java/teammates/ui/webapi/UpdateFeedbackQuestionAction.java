package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;
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
        FeedbackQuestion feedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Unknown question id");
        }

        gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(feedbackQuestion.getCourseId(), userInfo.getId()),
                getNonNullFeedbackSession(feedbackQuestion.getFeedbackSession().getName(), feedbackQuestion.getCourseId()),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestion feedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Unknown question id");
        }

        FeedbackQuestionUpdateRequest updateRequest = getAndValidateRequestBody(FeedbackQuestionUpdateRequest.class);

        FeedbackQuestion updatedQuestion;
        try {
            updatedQuestion = sqlLogic.updateFeedbackQuestionCascade(feedbackQuestionId, updateRequest);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult(new FeedbackQuestionData(updatedQuestion));
    }

}
