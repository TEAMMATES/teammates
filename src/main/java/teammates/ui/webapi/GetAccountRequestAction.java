package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.ui.output.AccountRequestData;

/**
 * Gets account request information.
 */
class GetAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String email = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String instituteWithCountry = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTE_WITH_COUNTRY); // TODO: check frontend

        AccountRequestAttributes accountRequestInfo = logic.getAccountRequest(email, instituteWithCountry);

        if (accountRequestInfo == null) {
            throw new EntityNotFoundException("Account request for email: "
                    + email + " and institute: " + instituteWithCountry + " not found."); // TODO: update message
        }

        AccountRequestData output = new AccountRequestData(accountRequestInfo);
        return new JsonResult(output);
    }

}
