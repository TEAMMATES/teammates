package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.output.AccountData;

/**
 * Gets account's information.
 */
class GetAccountAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        AccountAttributes accountInfo = logic.getAccount(googleId);
        if (accountInfo == null) {
            return new JsonResult("Account does not exist.", HttpStatus.SC_NOT_FOUND);
        }

        AccountData output = new AccountData(accountInfo);
        return new JsonResult(output);
    }

}
