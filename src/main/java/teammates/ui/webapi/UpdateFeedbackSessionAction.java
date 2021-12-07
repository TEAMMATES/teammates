package teammates.ui.webapi;

import java.time.Instant;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.FeedbackSessionUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates a feedback session.
 */
class UpdateFeedbackSessionAction extends Action {

    private static final Logger log = Logger.getLogger();

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

        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        FeedbackSessionUpdateRequest updateRequest =
                getAndValidateRequestBody(FeedbackSessionUpdateRequest.class);

        String timeZone = feedbackSession.getTimeZone();
        Instant startTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                updateRequest.getSubmissionStartTime(), timeZone, true);
        Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                updateRequest.getSubmissionEndTime(), timeZone, true);
        Instant sessionVisibleTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                updateRequest.getSessionVisibleFromTime(), timeZone, true);
        Instant resultsVisibleTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                updateRequest.getResultsVisibleFromTime(), timeZone, true);
        try {
            FeedbackSessionAttributes updateFeedbackSession = logic.updateFeedbackSession(
                    FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                            .withInstructions(updateRequest.getInstructions())
                            .withStartTime(startTime)
                            .withEndTime(endTime)
                            .withGracePeriod(updateRequest.getGracePeriod())
                            .withSessionVisibleFromTime(sessionVisibleTime)
                            .withResultsVisibleFromTime(resultsVisibleTime)
                            .withIsClosingEmailEnabled(updateRequest.isClosingEmailEnabled())
                            .withIsPublishedEmailEnabled(updateRequest.isPublishedEmailEnabled())
                            .build());

            return new JsonResult(new FeedbackSessionData(updateFeedbackSession));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        } catch (EntityDoesNotExistException ednee) {
            // Entity existence has been verified before, and this exception should not happen
            log.severe("Unexpected error", ednee);
            return new JsonResult(ednee.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
