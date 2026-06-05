package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.SubmittedGiverSetBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
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

        Instructor instructor = getInstructorFromRequest(feedbackSession.getCourseId());

        gateKeeper.verifyInstructorCanAccessSession(instructor, feedbackSession);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        SubmittedGiverSetBundle submittedGiverSetBundle;
        try {
            submittedGiverSetBundle = logic.getSubmittedGiverSet(feedbackSessionId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        FeedbackSessionSubmittedGiverSet output = new FeedbackSessionSubmittedGiverSet(submittedGiverSetBundle);
        return new JsonResult(output);
    }
}
