package teammates.ui.newcontroller;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;

/**
 * Save a feedback session.
 */
public class SaveFeedbackSessionAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                feedbackSession,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);

        FeedbackSessionInfo.FeedbackSessionSaveRequest saveRequest =
                getAndValidateRequestBody(FeedbackSessionInfo.FeedbackSessionSaveRequest.class);

        feedbackSession.setInstructions(saveRequest.getInstructions());

        feedbackSession.setStartTime(saveRequest.getSubmissionStartTime());
        feedbackSession.setEndTime(saveRequest.getSubmissionEndTimestamp());
        feedbackSession.setGracePeriodMinutes(saveRequest.getGracePeriod());

        feedbackSession.setSessionVisibleFromTime(saveRequest.getSessionVisibleFromTime());
        feedbackSession.setResultsVisibleFromTime(saveRequest.getResultsVisibleFromTime());

        feedbackSession.setClosingEmailEnabled(saveRequest.isClosingEmailEnabled());
        feedbackSession.setPublishedEmailEnabled(saveRequest.isPublishedEmailEnabled());

        try {
            logic.updateFeedbackSession(feedbackSession);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe.getMessage(), ipe);
        } catch (EntityDoesNotExistException ednee) {
            return new JsonResult(ednee.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        return new JsonResult(new FeedbackSessionInfo.FeedbackSessionResponse(feedbackSession));
    }

}
