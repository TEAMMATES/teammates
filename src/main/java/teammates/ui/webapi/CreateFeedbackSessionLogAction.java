package teammates.ui.webapi;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import teammates.common.datatransfer.logs.FeedbackSessionAuditLogDetails;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;

/**
 * Action: creates a feedback session log for the purposes of tracking and auditing.
 */
public class CreateFeedbackSessionLogAction extends Action {

    private static final Logger log = Logger.getLogger();
    private Clock clock = Clock.systemUTC();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
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
    public JsonResult execute() throws InvalidOperationException {
        String fslType = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE);
        FeedbackSessionLogType convertedFslType = FeedbackSessionLogType.valueOfLabel(fslType);
        if (convertedFslType == null) {
            throw new InvalidHttpParameterException("Invalid log type");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String fsName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        FeedbackSessionAuditLogDetails details = new FeedbackSessionAuditLogDetails();
        details.setCourseId(courseId);
        details.setFeedbackSessionName(fsName);
        details.setStudentEmail(studentEmail);
        details.setAccessType(fslType);

        UUID studentId = getUuidRequestParamValue(Const.ParamsNames.STUDENT_SQL_ID);
        UUID fsId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        details.setStudentId(studentId.toString());
        details.setFeedbackSessionId(fsId.toString());

        Student student = sqlLogic.getStudent(studentId);
        FeedbackSession feedbackSession = sqlLogic.getFeedbackSession(fsId);
        try {
            sqlLogic.validateFeedbackSessionLogContext(student, feedbackSession);
        } catch (InvalidParametersException ipe) {
            throw new InvalidOperationException(ipe);
        }

        Instant now = Instant.now(clock);
        FeedbackSessionLog feedbackSessionLog =
                new FeedbackSessionLog(student, feedbackSession, convertedFslType, now);
        sqlLogic.createFeedbackSessionLog(feedbackSessionLog);

        log.event("Feedback session audit event: " + fslType, details);

        return new JsonResult("Successful");
    }
}
