package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.User;
import teammates.ui.output.RegkeyValidityData;
import teammates.ui.request.Intent;

/**
 * Action: checks whether the provided registration key is valid for the logged in user.
 *
 * <p>This does not log in or log out the user.
 */
public class GetRegkeyValidityAction extends PublicAction {

    @Override
    public JsonResult execute() {
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        String regKey = getNonNullRequestParamValue(Const.ParamsNames.REGKEY);

        User regKeyOwner = null;

        if (intent == Intent.STUDENT_SUBMISSION || intent == Intent.STUDENT_RESULT) {
            regKeyOwner = logic.getStudentByRegistrationKey(regKey);
        } else if (intent == Intent.INSTRUCTOR_SUBMISSION || intent == Intent.INSTRUCTOR_RESULT) {
            regKeyOwner = logic.getInstructorByRegistrationKey(regKey);
        }

        if (regKeyOwner == null) {
            // The rest does not matter if regKeyOwner is null. (i.e. the regkey is invalid)
            return new JsonResult(new RegkeyValidityData(false, false, false));
        }

        UUID linkedAccountId = regKeyOwner.getAccountId();
        boolean isUsed = linkedAccountId != null;
        // If registration key has not been used, always allow access.
        // Otherwise, the signed in user needs to match.
        boolean isAllowedAccess = !isUsed || linkedAccountId.equals(getCurrentUserAccountId());

        return new JsonResult(new RegkeyValidityData(true, isUsed, isAllowedAccess));
    }

}
