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
    public boolean isTransactionNeeded() {
        return false;
    }

    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        String id = getNonNullRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);
        UUID accountRequestId = getUuidFromString(Const.ParamsNames.ACCOUNT_REQUEST_ID, id);

        AccountRequest accountRequest = sqlLogic.getAccountRequestWithTransaction(accountRequestId);

        if (accountRequest == null) {
            String errorMessage = String.format(Const.ACCOUNT_REQUEST_NOT_FOUND, accountRequestId.toString());
            throw new EntityNotFoundException(errorMessage);
        }

        if (accountRequest.getStatus() == AccountRequestStatus.APPROVED
                || accountRequest.getStatus() == AccountRequestStatus.REGISTERED) {
            throw new InvalidOperationException(
                    "Account request with id " + accountRequestId + " has already been approved.");
        }

        if (!sqlLogic.getApprovedAccountRequestsForEmailWithTransaction(accountRequest.getEmail()).isEmpty()) {
            throw new InvalidOperationException(String.format(
                    "An account request with email %s has already been approved. "
                            + "Please reject or delete the account request instead.",
                    accountRequest.getEmail()));
        }

        try {
            accountRequest.setStatus(AccountRequestStatus.APPROVED);
            accountRequest = sqlLogic.updateAccountRequestWithTransaction(accountRequest);
            EmailWrapper email = sqlEmailGenerator.generateNewInstructorAccountJoinEmail(
                    accountRequest.getEmail(), accountRequest.getName(), accountRequest.getRegistrationUrl());
            taskQueuer.scheduleAccountRequestForSearchIndexing(accountRequest.getId().toString());
            emailSender.sendEmail(email);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult(new AccountRequestData(accountRequest));
    }
}
