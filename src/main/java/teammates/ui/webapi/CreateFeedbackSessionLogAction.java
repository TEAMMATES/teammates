package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.LogsServiceException;
import teammates.common.util.Const;
import teammates.common.util.Const.FeedbackSessionLogTypes;

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
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String fsName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(fsName, courseId);
        if (feedbackSession == null) {
            return new JsonResult("No feedback session with name " + fsName
                    + " for course " + courseId, HttpStatus.SC_NOT_FOUND);
        }

        String fslType = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE);
        if (!fslType.equals(FeedbackSessionLogTypes.ACCESS) && !fslType.equals(FeedbackSessionLogTypes.SUBMISSION)) {
            return new JsonResult("Invalid feedback session log type.", HttpStatus.SC_BAD_REQUEST);
        }

        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            return new JsonResult("No student found", HttpStatus.SC_NOT_FOUND);
        }

        try {
            logic.createFeedbackSessionLog(courseId, studentEmail, fsName, fslType);
        } catch (LogsServiceException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        return new JsonResult("Successful");
    }
}
