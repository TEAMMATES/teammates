package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.ui.output.FeedbackSessionStatsData;

/**
 * Action: gets the response stats (submitted / total) of a feedback session.
 */
public class GetSessionResponseStatsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (userInfo.isAdmin) {
            return;
        }

        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        Instructor instructor = logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId());
        gateKeeper.verifyAccessible(instructor, feedbackSession);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        int expectedTotal = logic.getExpectedTotalSubmission(feedbackSession);
        int actualTotal = logic.getActualTotalSubmission(feedbackSession);
        FeedbackSessionStatsData output = new FeedbackSessionStatsData(actualTotal, expectedTotal);
        return new JsonResult(output);
    }

}
