package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.exception.LogServiceException;
import teammates.common.util.Const;

/**
 * Action: creates a feedback session log for the purposes of tracking and auditing.
 */
class CreateFeedbackSessionLogAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        // No specific access control restrictions on creating feedback session logs
    }

    @Override
    JsonResult execute() {
        String fslType = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE);
        if (!fslType.equals(Const.FeedbackSessionLogTypes.ACCESS)
                && !fslType.equals(Const.FeedbackSessionLogTypes.SUBMISSION)
                && !fslType.equals(Const.FeedbackSessionLogTypes.VIEW_RESULT)) {
            return new JsonResult("Invalid log type", HttpStatus.SC_BAD_REQUEST);
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String fsName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        // Skip rigorous validations to avoid incurring extra db reads and to keep the endpoint light

        try {
            logsProcessor.createFeedbackSessionLog(courseId, studentEmail, fsName, fslType);
        } catch (LogServiceException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        return new JsonResult("Successful");
    }
}
