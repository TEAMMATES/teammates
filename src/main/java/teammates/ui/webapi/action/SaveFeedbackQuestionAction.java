package teammates.ui.webapi.action;

import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.FeedbackQuestionData;
import teammates.ui.webapi.request.FeedbackQuestionSaveRequest;

/**
 * Save a feedback question.
 */
public class SaveFeedbackQuestionAction extends Action {

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

        FeedbackQuestionSaveRequest saveRequest = getAndValidateRequestBody(FeedbackQuestionSaveRequest.class);

        // update old value based on current request
        oldQuestion.setQuestionNumber(saveRequest.getQuestionNumber());
        oldQuestion.setQuestionDescription(saveRequest.getQuestionDescription());

        oldQuestion.setQuestionDetails(saveRequest.getQuestionDetails());

        oldQuestion.setGiverType(saveRequest.getGiverType());
        oldQuestion.setRecipientType(saveRequest.getRecipientType());

        oldQuestion.setNumberOfEntitiesToGiveFeedbackTo(saveRequest.getNumberOfEntitiesToGiveFeedbackTo());

        oldQuestion.setShowResponsesTo(saveRequest.getShowResponsesTo());
        oldQuestion.setShowGiverNameTo(saveRequest.getShowGiverNameTo());
        oldQuestion.setShowRecipientNameTo(saveRequest.getShowRecipientNameTo());

        // validate questions (giver & recipient)
        String err = oldQuestion.getQuestionDetails().validateGiverRecipientVisibility(oldQuestion);
        if (!err.isEmpty()) {
            throw new InvalidHttpRequestBodyException(err);
        }
        // validate questions (question details)
        List<String> questionDetailsErrors =
                oldQuestion.getQuestionDetails().validateQuestionDetails(oldQuestion.getCourseId());

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
                            .withShowGiveNameTo(oldQuestion.getShowGiverNameTo())
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
