package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates an account request.
 */
public class UpdateAccountRequestAction extends AdminOnlyAction {

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

        AccountRequestUpdateRequest accountRequestUpdateRequest =
                getAndValidateRequestBody(AccountRequestUpdateRequest.class);

        if (accountRequestUpdateRequest.getStatus() == AccountRequestStatus.APPROVED
                && (accountRequest.getStatus() == AccountRequestStatus.PENDING
                || accountRequest.getStatus() == AccountRequestStatus.REJECTED)) {

            if (!sqlLogic.getAccountsForEmailWithTransaction(accountRequest.getEmail()).isEmpty()) {
                throw new InvalidOperationException(String.format("An account with email %s already exists. "
                        + "Please reject or delete the account request instead.",
                        accountRequest.getEmail()));
            }

            if (!sqlLogic.getApprovedAccountRequestsForEmailWithTransaction(accountRequest.getEmail()).isEmpty()) {
                throw new InvalidOperationException(String.format(
                    "An account request with email %s has already been approved. "
                        + "Please reject or delete the account request instead.",
                        accountRequest.getEmail()));
            }

            try {
                // should not need to update other fields for an approval
                accountRequest.setStatus(accountRequestUpdateRequest.getStatus());
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
        } else {
            try {
                accountRequest.setName(accountRequestUpdateRequest.getName());
                accountRequest.setEmail(accountRequestUpdateRequest.getEmail());
                accountRequest.setInstitute(accountRequestUpdateRequest.getInstitute());
                accountRequest.setStatus(accountRequest.getStatus());
                accountRequest.setComments(accountRequestUpdateRequest.getComments());
                accountRequest = sqlLogic.updateAccountRequestWithTransaction(accountRequest);
                taskQueuer.scheduleAccountRequestForSearchIndexing(accountRequest.getId().toString());
            } catch (InvalidParametersException e) {
                throw new InvalidHttpRequestBodyException(e);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        }

        return new JsonResult(new AccountRequestData(accountRequest));
    }
}
