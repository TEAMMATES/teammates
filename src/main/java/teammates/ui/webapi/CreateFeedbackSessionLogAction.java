package teammates.ui.webapi;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
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
        if (userInfo == null) {
            return;
        }

        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("Only students can create feedback session logs.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        UUID studentId = getUuidRequestParamValue(Const.ParamsNames.STUDENT_SQL_ID);
        Student requestedStudent = sqlLogic.getStudent(studentId);
        Student currentStudent = sqlLogic.getStudentByGoogleId(courseId, userInfo.getId());
        if (requestedStudent == null || currentStudent == null || !requestedStudent.getId().equals(currentStudent.getId())) {
            throw new UnauthorizedAccessException("You are not allowed to create logs for this student.");
        }
    }

    @Override
    public JsonResult execute() {
        String fslType = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE);
        FeedbackSessionLogType convertedFslType = FeedbackSessionLogType.valueOfLabel(fslType);
        if (convertedFslType == null) {
            throw new InvalidHttpParameterException("Invalid log type");
        }

        getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        UUID studentId = getUuidRequestParamValue(Const.ParamsNames.STUDENT_SQL_ID);
        UUID fsId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        Instant now = Instant.now(clock);
        Student student = sqlLogic.getStudent(studentId);
        FeedbackSession feedbackSession = sqlLogic.getFeedbackSession(fsId);
        FeedbackSessionLog feedbackSessionLog =
                new FeedbackSessionLog(student, feedbackSession, convertedFslType, now);
        try {
            sqlLogic.createFeedbackSessionLog(feedbackSessionLog);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpParameterException(ipe);
        }

        return new JsonResult("Successful");
    }
}
