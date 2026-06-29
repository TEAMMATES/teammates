package teammates.ui.webapi;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import teammates.common.datatransfer.SessionKeyType;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Action: creates a feedback session log for the purposes of tracking and auditing.
 */
public class CreateFeedbackSessionLogAction extends SessionKeyAction {
    private Clock clock = Clock.systemUTC();

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("The feedback session does not exist.");
        }

        gateKeeper.verifySessionKey(requestContext, feedbackSessionId, SessionKeyType.SUBMISSION, SessionKeyType.RESULTS);

        Student authenticatedStudent = getStudentFromRequest(feedbackSession.getCourseId());
        if (authenticatedStudent == null) {
            throw new UnauthorizedAccessException("No authenticated student found for the course.");
        }

        if (authenticatedStudent.getAccount() != null
                && !authenticatedStudent.getAccountId().equals(getCurrentUserAccountId())) {
            throw new UnauthorizedAccessException(
                    "Login is required to create a feedback session log for a student with an associated account.");
        }

        if (!authenticatedStudent.getCourseId().equals(feedbackSession.getCourseId())) {
            throw new UnauthorizedAccessException(
                    "Authenticated student does not belong to the course of the feedback session.");
        }
    }

    @Override
    public JsonResult execute() {
        FeedbackSessionLogType convertedFslType =
                getEnumRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE,
                        FeedbackSessionLogType.class);

        UUID fsId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = logic.getFeedbackSession(fsId);
        if (feedbackSession == null) {
            throw new InvalidHttpParameterException("The feedback session does not exist.");
        }

        Instant now = Instant.now(clock);
        User user = getStudentFromRequest(feedbackSession.getCourseId());

        try {
            logic.createFeedbackSessionLog(feedbackSession, user, convertedFslType, now);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe);
        }

        return new JsonResult("Successful");
    }
}
