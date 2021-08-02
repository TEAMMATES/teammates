package teammates.ui.webapi;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.datatransfer.logs.LogEvent;
import teammates.common.exception.LogServiceException;
import teammates.common.util.Const;
import teammates.common.util.Logger;

/**
 * Action: creates a feedback session log for the purposes of tracking and auditing.
 */
class CreateFeedbackSessionLogAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        // No specific access control restrictions on creating feedback session logs
    }

    @Override
    public JsonResult execute() {
        String fslType = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE);
        FeedbackSessionLogType convertedFslType = FeedbackSessionLogType.valueOfLabel(fslType);
        if (convertedFslType == null) {
            return new JsonResult("Invalid log type", HttpStatus.SC_BAD_REQUEST);
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String fsName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        // Skip rigorous validations to avoid incurring extra db reads and to keep the endpoint light

        // The usage of separate logsProcessor to write logs is no longer needed
        // after structured logging is incorporated.
        // TODO remove this block 30 days after V8.0.0 is released.
        try {
            logsProcessor.createFeedbackSessionLog(courseId, studentEmail, fsName, fslType);
        } catch (LogServiceException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        Map<String, Object> details = new HashMap<>();
        details.put("courseId", courseId);
        details.put("feedbackSessionName", fsName);
        details.put("studentEmail", studentEmail);
        details.put("accessType", fslType);

        log.event(LogEvent.FEEDBACK_SESSION_AUDIT, "Feedback session audit event: " + fslType, details);

        return new JsonResult("Successful");
    }
}
