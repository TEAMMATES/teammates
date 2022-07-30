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
            String startTimeString = getNonNullRequestParamValue(Const.ParamsNames.ACCOUNT_REQUESTS_START_TIME);
            String endTimeString = getNonNullRequestParamValue(Const.ParamsNames.ACCOUNT_REQUESTS_END_TIME);
            validateStartAndEndTime(startTimeString, endTimeString);
            accountRequests = logic.getAccountRequestsSubmittedWithinPeriod(
                    Instant.ofEpochMilli(Long.parseLong(startTimeString)),
                    Instant.ofEpochMilli(Long.parseLong(endTimeString)));
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        return new JsonResult(new AccountRequestsData(accountRequests));
    }

    private void validateStartAndEndTime(String startTimeString, String endTimeString) {
        long startTime;
        try {
            startTime = Long.parseLong(startTimeString);
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid start time", e);
        }
        long endTime;
        try {
            endTime = Long.parseLong(endTimeString);
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid end time", e);
        }

        if (endTime < startTime) {
            throw new InvalidHttpParameterException("End date cannot be earlier than start date");
        }
    }

}
