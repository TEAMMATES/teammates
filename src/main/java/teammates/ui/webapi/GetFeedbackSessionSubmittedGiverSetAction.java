package teammates.ui.webapi;

import java.util.Set;
import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.output.FeedbackSessionSubmittedGiverSet;

/**
 * Get a set of givers that has given at least one response in the feedback session.
 */
public class GetFeedbackSessionSubmittedGiverSetAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
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

        Set<String> giverSet;
        try {
            giverSet = logic.getGiverSetThatAnsweredFeedbackSession(feedbackSessionId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        FeedbackSessionSubmittedGiverSet output = new FeedbackSessionSubmittedGiverSet(giverSet);
        return new JsonResult(output);
    }
}
