package teammates.ui.webapi.action;

import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.FeedbackQuestionData;
import teammates.ui.webapi.request.FeedbackQuestionUpdateRequest;

/**
 * Updates a feedback question.
 */
public class UpdateFeedbackQuestionAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String feedbackQuestionId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestionAttributes questionAttributes = logic.getFeedbackQuestion(feedbackQuestionId);

        if (questionAttributes == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("Unknown question id"));
        }

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(questionAttributes.getCourseId(), userInfo.getId()),
                logic.getFeedbackSession(questionAttributes.getFeedbackSessionName(), questionAttributes.getCourseId()),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
    }

    @Override
    public ActionResult execute() {
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
        String err = oldQuestion.getQuestionDetails().validateGiverRecipientVisibility(oldQuestion);
        if (!err.isEmpty()) {
            throw new InvalidHttpRequestBodyException(err);
        }
        // validate questions (question details)
        FeedbackQuestionDetails questionDetails = oldQuestion.getQuestionDetails();
        if (questionDetails instanceof FeedbackMsqQuestionDetails) {
            FeedbackMsqQuestionDetails msqQuestionDetails = (FeedbackMsqQuestionDetails) questionDetails;
            int numOfGeneratedMsqChoices = logic.getNumOfGeneratedChoicesForParticipantType(
                    oldQuestion.getCourseId(), msqQuestionDetails.getGenerateOptionsFor());
            msqQuestionDetails.setNumOfGeneratedMsqChoices(numOfGeneratedMsqChoices);
            questionDetails = msqQuestionDetails;
        }
        List<String> questionDetailsErrors = questionDetails.validateQuestionDetails();

        if (!questionDetailsErrors.isEmpty()) {
            throw new InvalidHttpRequestBodyException(questionDetailsErrors.toString());
        }

        try {
            logic.updateFeedbackQuestionCascade(
                    FeedbackQuestionAttributes.updateOptionsBuilder(oldQuestion.getId())
                            .withQuestionNumber(oldQuestion.getQuestionNumber())
                            .withQuestionDescription(oldQuestion.getQuestionDescription())
                            .withQuestionDetails(oldQuestion.getQuestionDetails())
                            .withGiverType(oldQuestion.getGiverType())
                            .withRecipientType(oldQuestion.getRecipientType())
                            .withNumberOfEntitiesToGiveFeedbackTo(oldQuestion.getNumberOfEntitiesToGiveFeedbackTo())
                            .withShowResponsesTo(oldQuestion.getShowResponsesTo())
                            .withShowGiverNameTo(oldQuestion.getShowGiverNameTo())
                            .withShowRecipientNameTo(oldQuestion.getShowRecipientNameTo())
                            .build());
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e.getMessage(), e);
        } catch (EntityDoesNotExistException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        return new JsonResult(new FeedbackQuestionData(oldQuestion));
    }

}
