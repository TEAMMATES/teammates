package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.output.AccountRequestsData;

/**
 * Searches for account requests.
 */
public class SearchAccountRequestsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);

        List<AccountRequest> accountRequests;
        try {
            accountRequests = sqlLogic.searchAccountRequestsInWholeSystem(searchKey);
        } catch (SearchServiceException e) {
            return new JsonResult(e.getMessage(), e.getStatusCode());
        }

        List<AccountRequestAttributes> requestsDatastore;
        try {
            requestsDatastore = logic.searchAccountRequestsInWholeSystem(searchKey);
        } catch (SearchServiceException e) {
            return new JsonResult(e.getMessage(), e.getStatusCode());
        }

        List<AccountRequestData> accountRequestDataList = new ArrayList<>();
        for (AccountRequest accountRequest : accountRequests) {
            AccountRequestData accountRequestData = new AccountRequestData(accountRequest);
            accountRequestDataList.add(accountRequestData);
        }

        for (AccountRequestAttributes request : requestsDatastore) {
            if (accountRequestDataList.stream().noneMatch(data -> data.getEmail().equals(request.getEmail()))) {
                AccountRequestData accountRequestData = new AccountRequestData(request);
                accountRequestDataList.add(accountRequestData);
            }
        }

        AccountRequestsData accountRequestsData = new AccountRequestsData();
        accountRequestsData.setAccountRequests(accountRequestDataList);

        return new JsonResult(accountRequestsData);
    }
}
