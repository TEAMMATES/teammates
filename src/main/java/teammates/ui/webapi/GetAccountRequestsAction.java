package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.ui.output.AccountRequestsData;
import teammates.ui.request.AccountRequestsGetIntent;

/**
 * Gets all account requests pending processing or recently processed.
 */
class GetAccountRequestsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        AccountRequestsGetIntent intent =
                AccountRequestsGetIntent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        List<AccountRequestAttributes> accountRequests;

        switch (intent) {
        case PENDING_PROCESSING:
            accountRequests = logic.getAccountRequestsPendingProcessing();
            break;
        case RECENTLY_PROCESSED:
            // accountRequests = logic.getAccountRequestsRecentlyProcessed();
            // break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        return new JsonResult(new AccountRequestsData(accountRequests));
    }

}
