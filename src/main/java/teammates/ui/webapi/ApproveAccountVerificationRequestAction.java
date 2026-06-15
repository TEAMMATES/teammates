package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.LinksUtil;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountVerificationRequestData;

/**
 * Approves an account verification request.
 */
public class ApproveAccountVerificationRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        UUID accountVerificationRequestId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID);

        AccountVerificationRequest accountVerificationRequest = logic.getAccountVerificationRequest(accountVerificationRequestId);

        if (accountVerificationRequest == null) {
            String errorMessage = String.format("Account verification request with id = %s not found", accountVerificationRequestId.toString());
            throw new EntityNotFoundException(errorMessage);
        }

        if (accountVerificationRequest.getStatus() == AccountVerificationRequestStatus.APPROVED) {
            throw new InvalidOperationException(
                    "Account verification request with id " + accountVerificationRequestId + " is already approved.");
        }

        try {
            accountVerificationRequest.setStatus(AccountVerificationRequestStatus.APPROVED);
            accountVerificationRequest = logic.updateAccountVerificationRequest(accountVerificationRequest);
            EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                    accountVerificationRequest.getEmail(), accountVerificationRequest.getName(),
                    LinksUtil.getInstructorWelcomeUrl(accountVerificationRequest.getId()));
            emailSender.sendEmail(email);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        return new JsonResult(new AccountVerificationRequestData(accountVerificationRequest));
    }
}
