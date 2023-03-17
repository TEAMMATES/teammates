package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.questions.FeedbackConstantSumQuestion;
import teammates.storage.sqlentity.questions.FeedbackContributionQuestion;
import teammates.storage.sqlentity.questions.FeedbackMcqQuestion;
import teammates.storage.sqlentity.questions.FeedbackMsqQuestion;
import teammates.storage.sqlentity.questions.FeedbackNumericalScaleQuestion;
import teammates.storage.sqlentity.questions.FeedbackRankOptionsQuestion;
import teammates.storage.sqlentity.questions.FeedbackRankRecipientsQuestion;
import teammates.storage.sqlentity.questions.FeedbackRubricQuestion;
import teammates.storage.sqlentity.questions.FeedbackTextQuestion;
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
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        if (instructorDetailForCourse != null) {
            gateKeeper.verifyAccessible(instructorDetailForCourse,
                    getNonNullFeedbackSession(feedbackSessionName, courseId),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
            return;
        }

        // TODO: Remove sql from variable name after migration
        Instructor sqlInstructorDetailForCourse = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
        gateKeeper.verifyAccessible(sqlInstructorDetailForCourse,
                getNonNullSqlFeedbackSession(feedbackSessionName, courseId),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackQuestionCreateRequest request = getAndValidateRequestBody(FeedbackQuestionCreateRequest.class);

        FeedbackQuestion feedbackQuestion = null;
        switch (request.getQuestionDetails().getQuestionType()) {
        case TEXT:
            feedbackQuestion = new FeedbackTextQuestion(
                    getNonNullSqlFeedbackSession(feedbackSessionName, courseId),
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
            break;
        case MCQ:
            feedbackQuestion = new FeedbackMcqQuestion(
                    getNonNullSqlFeedbackSession(feedbackSessionName, courseId),
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
            break;
        case MSQ:
            feedbackQuestion = new FeedbackMsqQuestion(
                    getNonNullSqlFeedbackSession(feedbackSessionName, courseId),
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
            break;
        case NUMSCALE:
            feedbackQuestion = new FeedbackNumericalScaleQuestion(
                    getNonNullSqlFeedbackSession(feedbackSessionName, courseId),
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
            break;
        case CONSTSUM:
        case CONSTSUM_OPTIONS:
        case CONSTSUM_RECIPIENTS:
            feedbackQuestion = new FeedbackConstantSumQuestion(
                    getNonNullSqlFeedbackSession(feedbackSessionName, courseId),
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
            break;
        case CONTRIB:
            feedbackQuestion = new FeedbackContributionQuestion(
                    getNonNullSqlFeedbackSession(feedbackSessionName, courseId),
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
            break;
        case RUBRIC:
            feedbackQuestion = new FeedbackRubricQuestion(
                    getNonNullSqlFeedbackSession(feedbackSessionName, courseId),
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
            break;
        case RANK_OPTIONS:
            feedbackQuestion = new FeedbackRankOptionsQuestion(
                    getNonNullSqlFeedbackSession(feedbackSessionName, courseId),
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
            break;
        case RANK_RECIPIENTS:
            feedbackQuestion = new FeedbackRankRecipientsQuestion(
                    getNonNullSqlFeedbackSession(feedbackSessionName, courseId),
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
            break;
        }

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
        }
    }

}
