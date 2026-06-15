package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountVerificationRequestData;
import teammates.ui.request.AccountVerificationRequestRejectionRequest;

/**
 * Rejects an account verification request.
 */
public class RejectAccountVerificationRequestAction extends AdminOnlyAction {
    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        UUID accountVerificationRequestId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID);

        AccountVerificationRequest accountVerificationRequest = logic.getAccountVerificationRequest(accountVerificationRequestId);

        if (accountVerificationRequest == null) {
            String errorMessage = String.format("Account verification request with id = %s not found", accountVerificationRequestId.toString());
            throw new EntityNotFoundException(errorMessage);
        }

        if (accountVerificationRequest.getStatus() != AccountVerificationRequestStatus.PENDING) {
            throw new InvalidOperationException(
                    "Account verification request with id " + accountVerificationRequestId + " is not in pending state and cannot be rejected.");
        }

        AccountVerificationRequestRejectionRequest accountVerificationRequestRejectionRequest =
                getAndValidateRequestBody(AccountVerificationRequestRejectionRequest.class);
        AccountVerificationRequestStatus initialStatus = accountVerificationRequest.getStatus();

        try {
            accountVerificationRequest.setStatus(AccountVerificationRequestStatus.REJECTED);
            accountVerificationRequest = logic.updateAccountVerificationRequest(accountVerificationRequest);
            if (accountVerificationRequestRejectionRequest.checkHasReason()
                    && initialStatus != AccountVerificationRequestStatus.REJECTED) {
                EmailWrapper email = emailGenerator.generateAccountVerificationRequestRejectionEmail(accountVerificationRequest,
                        accountVerificationRequestRejectionRequest.getReasonTitle(), accountVerificationRequestRejectionRequest.getReasonBody());
                emailSender.sendEmail(email);
            }
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        return new JsonResult(new AccountVerificationRequestData(accountVerificationRequest));
    }
}
