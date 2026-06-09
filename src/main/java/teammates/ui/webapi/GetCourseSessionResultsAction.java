package teammates.ui.webapi;

import java.util.Optional;
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
public class GetCourseSessionResultsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        gateKeeper.verifyInstructorInFeedbackSession(requestContext, feedbackSessionId);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        UUID questionId = getNullableUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        UUID selectedSection = getNullableUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION);
        Optional<Boolean> isDefaultSection = getNullableBooleanRequestParamValue(Const.ParamsNames.IS_DEFAULT_SECTION);

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        Instructor instructor = getInstructorFromRequest(feedbackSession.getCourseId());
        SessionResultsBundle bundle = logic.getSessionResults(feedbackSession, instructor,
                questionId, selectedSection, isDefaultSection.orElse(false));

        return new JsonResult(SessionResultsData.init(bundle));
    }
}
