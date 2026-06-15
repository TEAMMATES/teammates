package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.util.Const;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.output.AccountVerificationRequestData;
import teammates.ui.output.AccountVerificationRequestsData;

/**
 * Action: Gets pending account verification requests.
 */
public class GetAccountVerificationRequestsAction extends AdminOnlyAction {
    @Override
    public JsonResult execute() {
        String accountVerificationRequestStatus = getNonNullRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_STATUS);
        String pending = AccountVerificationRequestStatus.PENDING.name(); // 'PENDING'
        if (!pending.equalsIgnoreCase(accountVerificationRequestStatus)) {
            throw new InvalidHttpParameterException("Only 'pending' is allowed for account verification request status.");
        }

        List<AccountVerificationRequest> accountVerificationRequests = logic.getPendingAccountVerificationRequests();
        List<AccountVerificationRequestData> accountVerificationRequestDatas = accountVerificationRequests
                .stream()
                .map(ar -> new AccountVerificationRequestData(ar))
                .collect(Collectors.toList());

        AccountVerificationRequestsData output = new AccountVerificationRequestsData();
        output.setAccountVerificationRequests(accountVerificationRequestDatas);
        return new JsonResult(output);
    }
}
