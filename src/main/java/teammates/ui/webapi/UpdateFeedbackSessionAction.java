package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnexpectedServerException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.FeedbackSessionUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates a feedback session.
 */
public class UpdateFeedbackSessionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(
                sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        FeedbackSessionUpdateRequest updateRequest =
                getAndValidateRequestBody(FeedbackSessionUpdateRequest.class);

        List<DeadlineExtension> prevDeadlineExtensions = feedbackSession.getDeadlineExtensions();

        String timeZone = feedbackSession.getCourse().getTimeZone();
        Instant startTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                updateRequest.getSubmissionStartTime(), timeZone, true);
        if (!updateRequest.getSubmissionStartTime().equals(feedbackSession.getStartTime())) {
            String startTimeError = FieldValidator.getInvalidityInfoForNewStartTime(startTime, timeZone);
            if (!startTimeError.isEmpty()) {
                throw new InvalidHttpRequestBodyException("Invalid submission opening time: " + startTimeError);
            }
        }
        Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                updateRequest.getSubmissionEndTime(), timeZone, true);
        if (!updateRequest.getSubmissionEndTime().equals(feedbackSession.getEndTime())) {
            String endTimeError = FieldValidator.getInvalidityInfoForNewEndTime(endTime, timeZone);
            if (!endTimeError.isEmpty()) {
                throw new InvalidHttpRequestBodyException("Invalid submission closing time: " + endTimeError);
            }
        }
        Instant sessionVisibleTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                updateRequest.getSessionVisibleFromTime(), timeZone, true);
        if (!updateRequest.getSessionVisibleFromTime().equals(feedbackSession.getSessionVisibleFromTime())) {
            String visibilityStartAndSessionStartTimeError = FieldValidator
                    .getInvalidityInfoForTimeForNewVisibilityStart(sessionVisibleTime, startTime);
            if (!visibilityStartAndSessionStartTimeError.isEmpty()) {
                throw new InvalidHttpRequestBodyException("Invalid session visible time: "
                        + visibilityStartAndSessionStartTimeError);
            }
        }
        Instant resultsVisibleTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                updateRequest.getResultsVisibleFromTime(), timeZone, true);

        feedbackSession.setInstructions(updateRequest.getInstructions());
        feedbackSession.setStartTime(startTime);
        feedbackSession.setEndTime(endTime);
        feedbackSession.setGracePeriod(updateRequest.getGracePeriod());
        feedbackSession.setSessionVisibleFromTime(sessionVisibleTime);
        feedbackSession.setResultsVisibleFromTime(resultsVisibleTime);
        feedbackSession.setClosingSoonEmailEnabled(updateRequest.isClosingSoonEmailEnabled());
        feedbackSession.setPublishedEmailEnabled(updateRequest.isPublishedEmailEnabled());
        feedbackSession.setDeadlineExtensions(prevDeadlineExtensions);
        try {
            feedbackSession = sqlLogic.updateFeedbackSession(feedbackSession);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        } catch (EntityDoesNotExistException ednee) {
            // Entity existence has been verified before, and this exception should not happen
            throw new UnexpectedServerException(ednee.getMessage(), ednee);
        }

        return new JsonResult(new FeedbackSessionData(feedbackSession));
    }
}
