package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.SessionResultsData;

/**
 * Gets course-wide feedback session results including statistics where necessary.
 */
public class GetCourseSessionResultsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        gateKeeper.verifyInstructorInFeedbackSession(requestContext, feedbackSessionId);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        UUID questionId = getNullableUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        Instructor instructor = getInstructorFromRequest(feedbackSession.getCourseId());
        SessionResultsBundle bundle = logic.getSessionResults(feedbackSession, instructor, questionId);

        return new JsonResult(SessionResultsData.init(bundle));
    }
}
