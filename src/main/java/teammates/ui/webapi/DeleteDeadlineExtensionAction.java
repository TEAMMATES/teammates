package teammates.ui.webapi;

import teammates.common.util.Const;

/**
 * Deletes an existing deadline extension.
 */
class DeleteDeadlineExtensionAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String userEmail = getNonNullRequestParamValue(Const.ParamsNames.USER_EMAIL);
        boolean isInstructor = Boolean.parseBoolean(getNonNullRequestParamValue(Const.ParamsNames.IS_INSTRUCTOR));
        logic.deleteDeadlineExtension(courseId, feedbackSessionName, userEmail, isInstructor);
        return new JsonResult("Deadline extension successfully deleted.");
    }

}
