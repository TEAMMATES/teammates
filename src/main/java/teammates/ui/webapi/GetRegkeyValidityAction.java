package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.RegkeyValidityData;
import teammates.ui.request.Intent;

/**
 * Action: checks whether the provided registration key is valid for the logged in user.
 *
 * <p>This does not log in or log out the user.
 */
public class GetRegkeyValidityAction extends Action {

    @Override
    public AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Regkey information is available to everyone
    }

    @Override
    public ActionResult execute() {
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        String regkey = getNonNullRequestParamValue(Const.ParamsNames.REGKEY);

        if (intent == Intent.STUDENT_SUBMISSION || intent == Intent.STUDENT_RESULT) {
            boolean isValid = false;
            StudentAttributes student = logic.getStudentForRegistrationKey(regkey);

            if (student != null) {
                if (StringHelper.isEmpty(student.googleId)) {
                    // If registration key has not been used, always allow access
                    isValid = true;
                } else {
                    // If the registration key has been used to register, the logged in user needs to match
                    // Block access to not logged in user and mismatched user
                    isValid = userInfo != null && student.googleId.equals(userInfo.id);
                }
            }

            return new JsonResult(new RegkeyValidityData(isValid));
        }

        // Other intents are invalid for this purpose.
        // This includes instructor submission/result intents, because instructors are expected to be registered
        // in order to use the system.
        return new JsonResult(new RegkeyValidityData(false));
    }

}
