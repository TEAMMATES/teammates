package teammates.ui.webapi.action;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.FeedbackQuestionData;
import teammates.ui.webapi.request.FeedbackQuestionCreateRequest;

/**
 * Create a feedback question.
 */
public class CreateFeedbackQuestionAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, userInfo.getId());

        gateKeeper.verifyAccessible(instructorDetailForCourse,
                getFeedbackSession(feedbackSessionName, courseId),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
    }

    @Override
    public ActionResult execute() {
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
        String err = attributes.getQuestionDetails().validateGiverRecipientVisibility(attributes);
        if (!err.isEmpty()) {
            throw new InvalidHttpRequestBodyException(err);
        }
        // validate questions (question details)
        FeedbackQuestionDetails questionDetails = attributes.getQuestionDetails();
        if (questionDetails instanceof FeedbackMsqQuestionDetails) {
            FeedbackMsqQuestionDetails msqQuestionDetails = (FeedbackMsqQuestionDetails) questionDetails;
            int numOfGeneratedMsqChoices = logic.getNumOfGeneratedChoicesForParticipantType(
                    attributes.getCourseId(), msqQuestionDetails.getGenerateOptionsFor());
            msqQuestionDetails.setNumOfGeneratedMsqChoices(numOfGeneratedMsqChoices);
            questionDetails = msqQuestionDetails;
        }
        List<String> questionDetailsErrors = questionDetails.validateQuestionDetails();
        if (!questionDetailsErrors.isEmpty()) {
            throw new InvalidHttpRequestBodyException(questionDetailsErrors.toString());
        }

        try {
            attributes = logic.createFeedbackQuestion(attributes);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e.getMessage(), e);
        }

        return new JsonResult(new FeedbackQuestionData(attributes));
    }

}
