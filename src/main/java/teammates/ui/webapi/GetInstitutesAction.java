package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Institute;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstitutesData;

/**
 * Gets the institutes for which the given account has been verified.
 */
public class GetInstitutesAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID accountId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_ID);
        gateKeeper.verifyAdminOrOwnAccount(requestContext, accountId);
    }

    @Override
    public JsonResult execute() {
        UUID accountId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_ID);
        List<Institute> institutes = logic.getApprovedInstitutesForAccount(accountId);
        return new JsonResult(new InstitutesData(institutes));
    }
}
