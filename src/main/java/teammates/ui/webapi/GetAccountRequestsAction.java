package teammates.ui.webapi;

import java.time.Instant;
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
        case WITHIN_PERIOD:
            long startTime;
            try {
                startTime = getLongRequestParamValue(Const.ParamsNames.ACCOUNT_REQUESTS_START_TIME);
            } catch (InvalidHttpParameterException ihpe) {
                throw new InvalidHttpParameterException("Invalid start time", ihpe);
            }

            long endTime;
            try {
                endTime = getLongRequestParamValue(Const.ParamsNames.ACCOUNT_REQUESTS_END_TIME);
            } catch (InvalidHttpParameterException ihpe) {
                throw new InvalidHttpParameterException("Invalid end time", ihpe);
            }

            if (endTime < startTime) {
                throw new InvalidHttpParameterException("End time cannot be earlier than start time");
            }

            accountRequests = logic.getAccountRequestsSubmittedWithinPeriod(
                    Instant.ofEpochMilli(startTime),
                    Instant.ofEpochMilli(endTime));
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        return new JsonResult(new AccountRequestsData(accountRequests));
    }

}
