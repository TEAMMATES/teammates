package teammates.ui.webapi;

// import org.apache.http.HttpStatus;

// import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
// import teammates.common.datatransfer.attributes.StudentAttributes;
// import teammates.common.util.Const;
// import teammates.ui.output.FeedbackSessionLogType;

/**
 * Action: creates a feedback session log for the purposes of tracking and auditing.
 */
class CreateFeedbackSessionLogAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        // No specific access control restrictions on creating feedback session logs
    }

    @Override
    JsonResult execute() {
        // TODO: uncomment, commented out to pass lint
        /*
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String fsName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(fsName, courseId);
        if (feedbackSession == null) {
            return new JsonResult("No feedback session with name " + fsName
                    + " for course " + courseId, HttpStatus.SC_NOT_FOUND);
        }

        String fslTypeStr = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE);
        FeedbackSessionLogType fslType;
        try {
            fslType = FeedbackSessionLogType.valueOf(fslTypeStr);
        } catch (IllegalArgumentException e) {
            return new JsonResult("Invalid feedback session log type.", HttpStatus.SC_BAD_REQUEST);
        }

        StudentAttributes studentAttributes = getStudentOfCourseFromRequest(courseId);
        String email = studentAttributes.getEmail();

        logic.createFeedbackSessionLog(courseId, email, fsName, fslType);
        */
        return new JsonResult("Successful");
    }
}
