package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.ui.output.AccountRequestsData;

/**
 * Searches for account requests.
 */
class SearchAccountRequestsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        List<AccountRequestAttributes> accountRequests;
        try {
            accountRequests = logic.searchAccountRequestsInWholeSystem(searchKey);
        } catch (SearchServiceException e) {
            return new JsonResult(e.getMessage(), e.getStatusCode());
        }

        return new JsonResult(new AccountRequestsData(accountRequests));
    }
}
