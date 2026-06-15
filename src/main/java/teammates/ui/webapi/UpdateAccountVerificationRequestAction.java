package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Institute;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountVerificationRequestData;
import teammates.ui.request.AccountVerificationRequestUpdateRequest;

/**
 * Updates an account verification request.
 */
public class UpdateAccountVerificationRequestAction extends AdminOnlyAction {
    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        UUID accountVerificationRequestId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID);

        AccountVerificationRequest accountVerificationRequest = logic.getAccountVerificationRequest(accountVerificationRequestId);

        if (accountVerificationRequest == null) {
            String errorMessage = String.format("Account verification request with id = %s not found", accountVerificationRequestId.toString());
            throw new EntityNotFoundException(errorMessage);
        }

        AccountVerificationRequestUpdateRequest accountVerificationRequestUpdateRequest =
                getAndValidateRequestBody(AccountVerificationRequestUpdateRequest.class);

        try {
            Institute institute = logic.getOrCreateInstitute(
                    accountVerificationRequestUpdateRequest.getInstitute(), accountVerificationRequestUpdateRequest.getCountry());
            accountVerificationRequest.setName(accountVerificationRequestUpdateRequest.getName());
            accountVerificationRequest.setEmail(accountVerificationRequestUpdateRequest.getEmail());
            accountVerificationRequest.setInstitute(institute);
            // This action is for updating the account verification request details.
            // Approval or rejection are handled in their respective actions, so status should not be updated here.
            accountVerificationRequest.setStatus(accountVerificationRequest.getStatus());
            accountVerificationRequest.setComments(accountVerificationRequestUpdateRequest.getComments());
            accountVerificationRequest = logic.updateAccountVerificationRequest(accountVerificationRequest);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        return new JsonResult(new AccountVerificationRequestData(accountVerificationRequest));
    }
}
