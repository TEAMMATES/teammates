package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.output.AccountRequestsData;

/**
 * Action: Gets pending account requests.
 */
public class GetAccountRequestsAction extends AdminOnlyAction {
    @Override
    public JsonResult execute() {
        String accountRequestStatus = getNonNullRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_STATUS);
        String pending = AccountRequestStatus.PENDING.name(); // 'PENDING'
        if (!pending.equalsIgnoreCase(accountRequestStatus)) {
            throw new InvalidHttpParameterException("Only 'pending' is allowed for account request status.");
        }

        List<AccountRequest> accountRequests = sqlLogic.getPendingAccountRequests();
        List<AccountRequestData> accountRequestDatas = accountRequests
                .stream()
                .map(ar -> new AccountRequestData(ar))
                .collect(Collectors.toList());

        AccountRequestsData output = new AccountRequestsData();
        output.setAccountRequests(accountRequestDatas);
        return new JsonResult(output);
    }
}
