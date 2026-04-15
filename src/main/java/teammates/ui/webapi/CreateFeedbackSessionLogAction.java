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
        if (userInfo != null && !userInfo.isStudent) {
            throw new UnauthorizedAccessException("Only students can create feedback session logs.");
        }

        String courseId = getNonBlankRequestParamValue(Const.ParamsNames.COURSE_ID);
        UUID studentId = getUuidRequestParamValue(Const.ParamsNames.STUDENT_SQL_ID);
        Student requestedStudent = sqlLogic.getStudent(studentId);
        Student authenticatedStudent = getPossiblyUnregisteredSqlStudent(courseId);

        // Student has account but isn't logged in
        if (authenticatedStudent != null && userInfo == null && authenticatedStudent.getAccount() != null) {
            throw new UnauthorizedAccessException("Login is required to access this feedback session");
        }

        if (requestedStudent == null || authenticatedStudent == null
                || !requestedStudent.getId().equals(authenticatedStudent.getId())) {
            throw new UnauthorizedAccessException("You are not allowed to create logs for this student.");
        }
    }

    @Override
    public JsonResult execute() {
        String fslType = getNonBlankRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE);
        FeedbackSessionLogType convertedFslType = FeedbackSessionLogType.valueOfLabel(fslType);
        if (convertedFslType == null) {
            throw new InvalidHttpParameterException("Invalid log type");
        }

        getNonBlankRequestParamValue(Const.ParamsNames.COURSE_ID);
        getNonBlankRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        getNonBlankRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

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
