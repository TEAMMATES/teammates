package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
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
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        if (!isCourseMigrated(courseId)) {
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
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackQuestionCreateRequest request = getAndValidateRequestBody(FeedbackQuestionCreateRequest.class);

        if (!isCourseMigrated(courseId)) {
            return executeWithDataStore(courseId, feedbackSessionName, request);
        }

        FeedbackQuestion feedbackQuestion = FeedbackQuestion.makeQuestion(
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

    private JsonResult executeWithDataStore(String courseId, String feedbackSessionName,
            FeedbackQuestionCreateRequest request) throws InvalidHttpRequestBodyException {
        FeedbackQuestionAttributes attributes = FeedbackQuestionAttributes.builder()
                .withCourseId(courseId)
                .withFeedbackSessionName(feedbackSessionName)
                .withGiverType(request.getGiverType())
                .withRecipientType(request.getRecipientType())
                .withQuestionNumber(request.getQuestionNumber())
                .withNumberOfEntitiesToGiveFeedbackTo(request.getNumberOfEntitiesToGiveFeedbackTo())
                .withShowResponsesTo(request.getShowResponsesTo())
                .withShowGiverNameTo(request.getShowGiverNameTo())
                .withShowRecipientNameTo(request.getShowRecipientNameTo())
                .withQuestionDetails(request.getQuestionDetails())
                .withQuestionDescription(request.getQuestionDescription())
                .build();

        // validate questions (giver & recipient)
        String err = attributes.getQuestionDetailsCopy().validateGiverRecipientVisibility(attributes);
        if (!err.isEmpty()) {
            throw new InvalidHttpRequestBodyException(err);
        }
        // validate questions (question details)
        FeedbackQuestionDetails questionDetails = attributes.getQuestionDetailsCopy();
        List<String> questionDetailsErrors = questionDetails.validateQuestionDetails();
        if (!questionDetailsErrors.isEmpty()) {
            throw new InvalidHttpRequestBodyException(String.join("\n", questionDetailsErrors));
        }

        try {
            attributes = logic.createFeedbackQuestion(attributes);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        return new JsonResult(new FeedbackQuestionData(attributes));
    }

}
