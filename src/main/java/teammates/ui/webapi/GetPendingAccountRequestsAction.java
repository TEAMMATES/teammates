package teammates.ui.webapi;

import java.util.List;

import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.output.AccountRequestsData;

public class GetPendingAccountRequestsAction extends AdminOnlyAction {
    @Override
    public JsonResult execute() {
        List<AccountRequest> accountRequests = sqlLogic.getPendingAccountRequests();
        List<AccountRequestData> accountRequestDatas = accountRequests.stream().map(ar -> new AccountRequestData(ar)).toList();

        AccountRequestsData output = new AccountRequestsData();
        output.setAccountRequests(accountRequestDatas);
        return new JsonResult(output);
    }
}
