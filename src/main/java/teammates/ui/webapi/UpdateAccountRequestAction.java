package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates an account request.
 */
public class UpdateAccountRequestAction extends AdminOnlyAction {
    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        UUID accountRequestId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);

        AccountRequest accountRequest = sqlLogic.getAccountRequest(accountRequestId);

        if (accountRequest == null) {
            String errorMessage = String.format("Account request with id = %s not found", accountRequestId.toString());
            throw new EntityNotFoundException(errorMessage);
        }

        AccountRequestUpdateRequest accountRequestUpdateRequest =
                getAndValidateRequestBody(AccountRequestUpdateRequest.class);

        try {
            accountRequest.setName(accountRequestUpdateRequest.getName());
            accountRequest.setEmail(accountRequestUpdateRequest.getEmail());
            accountRequest.setInstitute(accountRequestUpdateRequest.getInstitute());
            // This action is for updating the account request details.
            // Approval or rejection are handled in their respective actions, so status should not be updated here.
            accountRequest.setStatus(accountRequest.getStatus());
            accountRequest.setComments(accountRequestUpdateRequest.getComments());
            accountRequest = sqlLogic.updateAccountRequest(accountRequest);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        return new JsonResult(new AccountRequestData(accountRequest));
    }
}
