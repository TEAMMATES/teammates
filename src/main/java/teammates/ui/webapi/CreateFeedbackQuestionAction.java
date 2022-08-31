package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.request.FeedbackQuestionCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a feedback question.
 */
class CreateFeedbackQuestionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, userInfo.getId());

        gateKeeper.verifyAccessible(instructorDetailForCourse,
                getNonNullFeedbackSession(feedbackSessionName, courseId),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackQuestionCreateRequest request = getAndValidateRequestBody(FeedbackQuestionCreateRequest.class);
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
