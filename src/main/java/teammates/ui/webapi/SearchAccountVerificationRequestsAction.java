package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.util.Const;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.ui.output.AccountVerificationRequestData;
import teammates.ui.output.AccountVerificationRequestsData;

/**
 * Searches for account requests.
 */
public class SearchAccountVerificationRequestsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);

        List<AccountVerificationRequest> accountVerificationRequests = logic.searchAccountVerificationRequestsInWholeSystem(searchKey);

        List<AccountVerificationRequestData> accountVerificationRequestDataList = accountVerificationRequests.stream()
                .map(AccountVerificationRequestData::new)
                .collect(Collectors.toList());

        AccountVerificationRequestsData accountVerificationRequestsData = new AccountVerificationRequestsData();
        accountVerificationRequestsData.setAccountVerificationRequests(accountVerificationRequestDataList);

        return new JsonResult(accountVerificationRequestsData);
    }
}
