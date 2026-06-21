package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.AccountVerificationRequestQuery;
import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.util.Const;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.ui.exception.InvalidHttpParameterException;
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
                getNullablePositiveIntRequestParamValue(Const.ParamsNames.LIMIT));

        List<AccountVerificationRequest> accountVerificationRequests = logic.getAccountVerificationRequests(query);
        return new JsonResult(new AccountVerificationRequestsData(accountVerificationRequests));
    }

    private AccountVerificationRequestStatus getNullableStatusRequestParamValue(String paramName) {
        String value = getRequestParamValue(paramName);
        if (value == null) {
            return null;
        }
        try {
            return AccountVerificationRequestStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(
                    "Expected AccountVerificationRequestStatus value for " + paramName
                    + " parameter, but found: [" + value + "]", e);
        }
    }

    private Integer getNullablePositiveIntRequestParamValue(String paramName) {
        String value = getRequestParamValue(paramName);
        if (value == null) {
            return null;
        }
        int parsed;
        try {
            parsed = Integer.parseInt(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(
                    "Expected integer value for " + paramName + " parameter, but found: [" + value + "]", e);
        }
        if (parsed <= 0) {
            throw new InvalidHttpParameterException(
                    "Expected positive integer value for " + paramName + " parameter, but found: [" + value + "]");
        }
        return parsed;
    }
}
