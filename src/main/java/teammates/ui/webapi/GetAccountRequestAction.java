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
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTE);

        AccountRequestAttributes accountRequestInfo = logic.getAccountRequest(email, institute);

        if (accountRequestInfo == null) {
            throw new EntityNotFoundException("Account request for email: "
                    + email + " and institute: " + institute + " not found.");
        }

        AccountRequestData output = new AccountRequestData(accountRequestInfo);
        return new JsonResult(output);
    }

}
