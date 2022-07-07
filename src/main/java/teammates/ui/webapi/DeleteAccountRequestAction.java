package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;

/**
 * Deletes an existing account request.
 */
class DeleteAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException {
        String email = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String instituteWithCountry = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTE_WITH_COUNTRY); // TODO: check frontend

        AccountRequestAttributes accountRequest = logic.getAccountRequest(email, instituteWithCountry);
        if (accountRequest != null && accountRequest.getRegisteredAt() != null) {
            // instructor is registered
            throw new InvalidOperationException("Account request of a registered instructor cannot be deleted.");
        }

        logic.deleteAccountRequest(email, instituteWithCountry);
        return new JsonResult("Account request successfully deleted.");
    }

}
