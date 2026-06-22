package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.InvalidVerificationRequestStateException;
import teammates.common.util.Const;
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
        AccountVerificationRequestRejectionRequest rejectionRequest = getRequestBody().isBlank()
                ? new AccountVerificationRequestRejectionRequest(null, null)
                : getAndValidateRequestBody(AccountVerificationRequestRejectionRequest.class);

        try {
            AccountVerificationRequest accountVerificationRequest =
                    logic.rejectAccountVerificationRequest(accountVerificationRequestId,
                            rejectionRequest.getReasonTitle(), rejectionRequest.getReasonBody());
            return new JsonResult(new AccountVerificationRequestData(accountVerificationRequest));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidVerificationRequestStateException e) {
            throw new InvalidOperationException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }
    }
}
