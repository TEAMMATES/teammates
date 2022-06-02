package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.ui.output.DeadlineExtensionData;

/**
 * Gets deadline extension information.
*/
class GetDeadlineExtensionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.ALL_ACCESS;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!Config.IS_DEV_SERVER) {
            throw new UnauthorizedAccessException("Not authorised to access this resource.");
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String userEmail = getNonNullRequestParamValue(Const.ParamsNames.USER_EMAIL);
        boolean isInstructor = Boolean.parseBoolean(getNonNullRequestParamValue(Const.ParamsNames.IS_INSTRUCTOR));

        DeadlineExtensionAttributes deadlineExtension =
                logic.getDeadlineExtension(courseId, feedbackSessionName, userEmail, isInstructor);

        if (deadlineExtension == null) {
            throw new EntityNotFoundException(
                    "Deadline extension for course id: " + courseId
                    + " and feedback session name: " + feedbackSessionName
                    + " and " + (isInstructor ? "instructor" : "student")
                    + " email: " + userEmail
                    + " not found.");
        }

        return new JsonResult(new DeadlineExtensionData(deadlineExtension));
    }

}
