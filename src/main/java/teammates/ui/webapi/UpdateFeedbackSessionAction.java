package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.FeedbackSessionUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates a feedback session.
 */
class UpdateFeedbackSessionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionUpdateRequest updateRequest =
                getAndValidateRequestBody(FeedbackSessionUpdateRequest.class);

        try {
            FeedbackSessionAttributes updateFeedbackSession = logic.updateFeedbackSession(
                    FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                            .withInstructions(updateRequest.getInstructions())
                            .withStartTime(updateRequest.getSubmissionStartTime())
                            .withEndTime(updateRequest.getSubmissionEndTime())
                            .withGracePeriod(updateRequest.getGracePeriod())
                            .withSessionVisibleFromTime(updateRequest.getSessionVisibleFromTime())
                            .withResultsVisibleFromTime(updateRequest.getResultsVisibleFromTime())
                            .withIsClosingEmailEnabled(updateRequest.isClosingEmailEnabled())
                            .withIsPublishedEmailEnabled(updateRequest.isPublishedEmailEnabled())
                            .build());

            return new JsonResult(new FeedbackSessionData(updateFeedbackSession));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }
    }

}
