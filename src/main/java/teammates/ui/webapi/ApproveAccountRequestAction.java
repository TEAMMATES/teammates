package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityDoesNotExistException;
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
        String id = getNonNullRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);
        UUID accountRequestId = getUuidFromString(Const.ParamsNames.ACCOUNT_REQUEST_ID, id);

        AccountRequest accountRequest = sqlLogic.getAccountRequest(accountRequestId);

        if (accountRequest == null) {
            String errorMessage = String.format(Const.ACCOUNT_REQUEST_NOT_FOUND, accountRequestId.toString());
            throw new EntityNotFoundException(errorMessage);
        }

        if (accountRequest.getStatus() != AccountRequestStatus.PENDING
                && accountRequest.getStatus() != AccountRequestStatus.REJECTED) {
            throw new InvalidOperationException(String.format(
                    "Account request with id " + accountRequestId + " is not pending or rejected and cannot be approved."));
        }

        if (!sqlLogic.getApprovedAccountRequestsForEmailAndInstitute(accountRequest.getEmail(),
                accountRequest.getInstitute()).isEmpty()) {
            throw new InvalidOperationException(String.format(
            "An account request with email %s and institute %s has already been approved. "
                + "Please reject or delete the account request instead.",
                accountRequest.getEmail(), accountRequest.getInstitute()));
        }

        try {
            accountRequest.setStatus(AccountRequestStatus.APPROVED);
            accountRequest = sqlLogic.updateAccountRequest(accountRequest);
            EmailWrapper email = sqlEmailGenerator.generateNewInstructorAccountJoinEmail(
                    accountRequest.getEmail(), accountRequest.getName(), accountRequest.getRegistrationUrl());
            emailSender.sendEmail(email);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult(new AccountRequestData(accountRequest));
    }
}
