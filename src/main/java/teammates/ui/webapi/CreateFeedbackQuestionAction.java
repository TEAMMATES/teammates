package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.Instructor;
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
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        Instructor instructorDetailForCourse = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
        gateKeeper.verifyAccessible(instructorDetailForCourse,
                getNonNullFeedbackSession(feedbackSessionName, courseId),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackQuestionCreateRequest request = getAndValidateRequestBody(FeedbackQuestionCreateRequest.class);

        FeedbackQuestion feedbackQuestion = FeedbackQuestion.makeQuestion(
                getNonNullFeedbackSession(feedbackSessionName, courseId),
                request.getQuestionNumber(),
                request.getQuestionDescription(),
                request.getGiverType(),
                request.getRecipientType(),
                request.getNumberOfEntitiesToGiveFeedbackTo(),
                request.getShowResponsesTo(),
                request.getShowGiverNameTo(),
                request.getShowRecipientNameTo(),
                request.getQuestionDetails());

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
            feedbackQuestion = sqlLogic.createFeedbackQuestion(feedbackQuestion);
            return new JsonResult(new FeedbackQuestionData(feedbackQuestion));
        } catch (InvalidParametersException ex) {
            throw new InvalidHttpRequestBodyException(ex);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException(e);
        }
    }
}
