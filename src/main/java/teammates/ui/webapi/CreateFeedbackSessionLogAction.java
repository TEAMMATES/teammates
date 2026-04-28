package teammates.ui.webapi;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Student;

/**
 * Action: creates a feedback session log for the purposes of tracking and auditing.
 */
public class CreateFeedbackSessionLogAction extends Action {
    private Clock clock = Clock.systemUTC();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = sqlLogic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new UnauthorizedAccessException("The feedback session does not exist.");
        }

        Student authenticatedStudent = getPossiblyUnregisteredSqlStudent(feedbackSession.getCourseId());
        if (authenticatedStudent == null) {
            throw new UnauthorizedAccessException("No authenticated student found for the course.");
        }

        if (!authenticatedStudent.getCourseId().equals(feedbackSession.getCourseId())) {
            throw new UnauthorizedAccessException(
                    "Authenticated student does not belong to the course of the feedback session.");
        }
    }

    @Override
    public JsonResult execute() {
        FeedbackSessionLogType convertedFslType;
        String fslType = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE);
        try {
            convertedFslType = FeedbackSessionLogType.valueOf(fslType);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException("Invalid log type: " + fslType, e);
        }

        UUID fsId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = sqlLogic.getFeedbackSession(fsId);
        if (feedbackSession == null) {
            throw new InvalidHttpParameterException("The feedback session does not exist.");
        }

        Instant now = Instant.now(clock);
        Student student = getPossiblyUnregisteredSqlStudent(feedbackSession.getCourseId());

        try {
            sqlLogic.createFeedbackSessionLog(feedbackSession, student, convertedFslType, now);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe);
        }

        return new JsonResult("Successful");
    }
}
