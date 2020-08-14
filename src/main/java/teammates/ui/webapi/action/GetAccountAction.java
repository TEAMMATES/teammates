package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.output.AccountData;

/**
 * Gets account's information.
 */
public class GetAccountAction extends AdminOnlyAction {

    @Override
    public ActionResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        AccountAttributes accountInfo = logic.getAccount(googleId);
        if (accountInfo == null) {
            return new JsonResult("Account does not exist.", HttpStatus.SC_NOT_FOUND);
        }

        AccountData output = new AccountData(accountInfo);
        return new JsonResult(output);
    }

}
