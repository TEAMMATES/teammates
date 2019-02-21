package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.AccountData;

/**
 * Gets account's information.
 */
public class GetAccountAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Only admins can downgrade accounts
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

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
