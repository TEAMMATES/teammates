package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.AccountVerificationRequest;
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
        AccountVerificationRequestUpdateRequest updateRequest =
                getAndValidateRequestBody(AccountVerificationRequestUpdateRequest.class);

        try {
            AccountVerificationRequest accountVerificationRequest = logic.updateAccountVerificationRequestDetails(
                    accountVerificationRequestId,
                    updateRequest.getName(),
                    updateRequest.getEmail(),
                    updateRequest.getInstitute(),
                    updateRequest.getCountry(),
                    updateRequest.getComments());
            return new JsonResult(new AccountVerificationRequestData(accountVerificationRequest));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }
    }
}
