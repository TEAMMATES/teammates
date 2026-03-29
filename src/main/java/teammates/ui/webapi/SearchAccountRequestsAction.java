package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

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

        List<AccountRequestData> accountRequestDataList = accountRequests.stream()
                .map(AccountRequestData::new)
                .collect(Collectors.toList());

        AccountRequestsData accountRequestsData = new AccountRequestsData();
        accountRequestsData.setAccountRequests(accountRequestDataList);

        return new JsonResult(accountRequestsData);
    }
}
