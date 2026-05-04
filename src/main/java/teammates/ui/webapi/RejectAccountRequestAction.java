package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.logic.entity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestRejectionRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Rejects an account request.
 */
public class RejectAccountRequestAction extends AdminOnlyAction {
    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        UUID accountRequestId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);

        AccountRequest accountRequest = logic.getAccountRequest(accountRequestId);

        if (accountRequest == null) {
            String errorMessage = String.format("Account request with id = %s not found", accountRequestId.toString());
            throw new EntityNotFoundException(errorMessage);
        }

        if (accountRequest.getStatus() != AccountRequestStatus.PENDING) {
            throw new InvalidOperationException(
                    "Account request with id " + accountRequestId + " is not in pending state and cannot be rejected.");
        }

        AccountRequestRejectionRequest accountRequestRejectionRequest =
                getAndValidateRequestBody(AccountRequestRejectionRequest.class);
        AccountRequestStatus initialStatus = accountRequest.getStatus();

        try {
            accountRequest.setStatus(AccountRequestStatus.REJECTED);
            accountRequest = logic.updateAccountRequest(accountRequest);
            if (accountRequestRejectionRequest.checkHasReason()
                    && initialStatus != AccountRequestStatus.REJECTED) {
                EmailWrapper email = emailGenerator.generateAccountRequestRejectionEmail(accountRequest,
                        accountRequestRejectionRequest.getReasonTitle(), accountRequestRejectionRequest.getReasonBody());
                emailSender.sendEmail(email);
            }
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        return new JsonResult(new AccountRequestData(accountRequest));
    }
}
