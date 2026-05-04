package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Approves an account request.
 */
public class ApproveAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        UUID accountRequestId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);

        AccountRequest accountRequest = logic.getAccountRequest(accountRequestId);

        if (accountRequest == null) {
            String errorMessage = String.format("Account request with id = %s not found", accountRequestId.toString());
            throw new EntityNotFoundException(errorMessage);
        }

        if (accountRequest.getStatus() == AccountRequestStatus.APPROVED
                || accountRequest.getStatus() == AccountRequestStatus.REGISTERED) {
            throw new InvalidOperationException(
                    "Account request with id " + accountRequestId + " is already approved or registered.");
        }

        if (!logic.getApprovedAccountRequestsForEmailAndInstitute(accountRequest.getEmail(),
                accountRequest.getInstitute()).isEmpty()) {
            throw new InvalidOperationException(String.format(
            "An account request with email %s and institute %s has already been approved. "
                + "Please reject or delete the account request instead.",
                accountRequest.getEmail(), accountRequest.getInstitute()));
        }

        if (logic.getInstructorForEmailAndInstitute(accountRequest.getEmail(), accountRequest.getInstitute())
                != null) {
            throw new InvalidOperationException(String.format(
                "An instructor with email %s and institute %s already exists. "
                    + "Please reject or delete the account request instead.",
                accountRequest.getEmail(), accountRequest.getInstitute()));
        }

        try {
            accountRequest.setStatus(AccountRequestStatus.APPROVED);
            accountRequest = logic.updateAccountRequest(accountRequest);
            EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                    accountRequest.getEmail(), accountRequest.getName(), accountRequest.getRegistrationUrl());
            emailSender.sendEmail(email);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        return new JsonResult(new AccountRequestData(accountRequest));
    }
}
