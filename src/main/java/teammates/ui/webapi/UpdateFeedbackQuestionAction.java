package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.request.FeedbackQuestionUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates a feedback question.
 */
class UpdateFeedbackQuestionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestionAttributes questionAttributes = logic.getFeedbackQuestion(feedbackQuestionId);

        if (questionAttributes == null) {
            throw new EntityNotFoundException("Unknown question id");
        }

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(questionAttributes.getCourseId(), userInfo.getId()),
                getNonNullFeedbackSession(questionAttributes.getFeedbackSessionName(), questionAttributes.getCourseId()),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestionAttributes oldQuestion = logic.getFeedbackQuestion(feedbackQuestionId);

        FeedbackQuestionUpdateRequest updateRequest = getAndValidateRequestBody(FeedbackQuestionUpdateRequest.class);

        // update old value based on current request
        oldQuestion.setQuestionNumber(updateRequest.getQuestionNumber());
        oldQuestion.setQuestionDescription(updateRequest.getQuestionDescription());

        oldQuestion.setQuestionDetails(updateRequest.getQuestionDetails());

        oldQuestion.setGiverType(updateRequest.getGiverType());
        oldQuestion.setRecipientType(updateRequest.getRecipientType());

        oldQuestion.setNumberOfEntitiesToGiveFeedbackTo(updateRequest.getNumberOfEntitiesToGiveFeedbackTo());

        oldQuestion.setShowResponsesTo(updateRequest.getShowResponsesTo());
        oldQuestion.setShowGiverNameTo(updateRequest.getShowGiverNameTo());
        oldQuestion.setShowRecipientNameTo(updateRequest.getShowRecipientNameTo());

        // validate questions (giver & recipient)
        String err = oldQuestion.getQuestionDetailsCopy().validateGiverRecipientVisibility(oldQuestion);
        if (!err.isEmpty()) {
            throw new InvalidHttpRequestBodyException(err);
        }
        // validate questions (question details)
        FeedbackQuestionDetails questionDetails = oldQuestion.getQuestionDetailsCopy();
        List<String> questionDetailsErrors = questionDetails.validateQuestionDetails();

        if (!questionDetailsErrors.isEmpty()) {
            throw new InvalidHttpRequestBodyException(String.join("\n", questionDetailsErrors));
        }

        try {
            logic.updateFeedbackQuestionCascade(
                    FeedbackQuestionAttributes.updateOptionsBuilder(oldQuestion.getId())
                            .withQuestionNumber(oldQuestion.getQuestionNumber())
                            .withQuestionDescription(oldQuestion.getQuestionDescription())
                            .withQuestionDetails(oldQuestion.getQuestionDetailsCopy())
                            .withGiverType(oldQuestion.getGiverType())
                            .withRecipientType(oldQuestion.getRecipientType())
                            .withNumberOfEntitiesToGiveFeedbackTo(oldQuestion.getNumberOfEntitiesToGiveFeedbackTo())
                            .withShowResponsesTo(oldQuestion.getShowResponsesTo())
                            .withShowGiverNameTo(oldQuestion.getShowGiverNameTo())
                            .withShowRecipientNameTo(oldQuestion.getShowRecipientNameTo())
                            .build());
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult(new FeedbackQuestionData(oldQuestion));
    }

}
