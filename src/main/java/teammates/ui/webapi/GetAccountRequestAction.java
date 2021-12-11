package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.output.AccountRequestData;

/**
 * Gets account request information.
 */
class GetAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String email = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);

        AccountRequestAttributes accountRequestInfo;
        try {
            accountRequestInfo = logic.getAccountRequest(email, institute);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }

        AccountRequestData output = new AccountRequestData(accountRequestInfo);
        return new JsonResult(output);
    }

}
