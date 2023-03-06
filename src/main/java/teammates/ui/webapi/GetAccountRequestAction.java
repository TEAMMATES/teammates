package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;

/**
 * Gets account request information.
 */
class GetAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String email = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);

        AccountRequest accountRequest = sqlLogic.getAccountRequest(email, institute);

        if (accountRequest == null) {
            throw new EntityNotFoundException("Account request for email: "
                    + email + " and institute: " + institute + " not found.");
        }

        AccountRequestData output = new AccountRequestData(accountRequest);
        return new JsonResult(output);
    }

}
