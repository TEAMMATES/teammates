package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.entity.FeedbackQuestion;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.request.FeedbackQuestionCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a feedback question.
 */
public class CreateFeedbackQuestionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        Instructor instructorDetailForCourse =
                logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId());
        gateKeeper.verifyAccessible(instructorDetailForCourse,
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }
        FeedbackQuestionCreateRequest request = getAndValidateRequestBody(FeedbackQuestionCreateRequest.class);

        FeedbackQuestion feedbackQuestion = FeedbackQuestion.makeQuestion(
                feedbackSession,
                request.getQuestionNumber(),
                request.getQuestionDescription(),
                request.getGiverType(),
                request.getRecipientType(),
                request.getNumberOfEntitiesToGiveFeedbackTo(),
                request.getShowResponsesTo(),
                request.getShowGiverNameTo(),
                request.getShowRecipientNameTo(),
                request.getQuestionDetails()
        );

        try {
            // validate questions (giver & recipient)
            String err = feedbackQuestion.getQuestionDetailsCopy().validateGiverRecipientVisibility(feedbackQuestion);

            if (!err.isEmpty()) {
                throw new InvalidHttpRequestBodyException(err);
            }
            // validate questions (question details)
            FeedbackQuestionDetails questionDetails = feedbackQuestion.getQuestionDetailsCopy();
            List<String> questionDetailsErrors = questionDetails.validateQuestionDetails();
            if (!questionDetailsErrors.isEmpty()) {
                throw new InvalidHttpRequestBodyException(questionDetailsErrors.toString());
            }
            feedbackQuestion = logic.createFeedbackQuestion(feedbackQuestion);
            return new JsonResult(new FeedbackQuestionData(feedbackQuestion));
        } catch (InvalidParametersException ex) {
            throw new InvalidHttpRequestBodyException(ex);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException(e);
        }
    }
}
