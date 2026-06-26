package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.AccountVerificationRequestQuery;
import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.util.Const;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.ui.output.AccountVerificationRequestsData;

/**
 * Action: Gets account verification requests.
 */
public class GetAccountVerificationRequestsAction extends AdminOnlyAction {
    @Override
    public JsonResult execute() {
        AccountVerificationRequestQuery query = new AccountVerificationRequestQuery(
                getNullableUuidRequestParamValue(Const.ParamsNames.INSTITUTE_ID),
                getNullableUuidRequestParamValue(Const.ParamsNames.ACCOUNT_ID),
                getNullableStatusRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_STATUS),
                getRequestParamValue(Const.ParamsNames.SEARCH_KEY),
                getLimitParamValue());

        List<AccountVerificationRequest> accountVerificationRequests = logic.getAccountVerificationRequests(query);
        return new JsonResult(new AccountVerificationRequestsData(accountVerificationRequests));
    }

    private AccountVerificationRequestStatus getNullableStatusRequestParamValue(String paramName) {
        return getNullableEnumRequestParamValue(paramName, AccountVerificationRequestStatus.class);
    }

}
