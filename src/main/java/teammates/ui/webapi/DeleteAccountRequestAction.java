package teammates.ui.webapi;

import teammates.common.util.Const;

/**
 * Deletes an existing account request.
 */
class DeleteAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String email = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);
        logic.deleteAccountRequest(email, institute);
        return new JsonResult("Account request successfully deleted.");
    }

}
