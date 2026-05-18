package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.AccountRequest;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestRejectionRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Rejects an account request.
 */
public class RejectAccountRequestAction extends AdminOnlyAction {
    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        UUID accountRequestId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);

        AccountRequest accountRequest;
        try {
            accountRequest = logic.rejectAccountRequest(accountRequestId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidOperationException(e);
        }

        AccountRequestRejectionRequest rejectionRequest =
                getAndValidateRequestBody(AccountRequestRejectionRequest.class);

        if (rejectionRequest.checkHasReason()) {
            EmailWrapper email = emailGenerator.generateAccountRequestRejectionEmail(
                    accountRequest, rejectionRequest.getReasonTitle(), rejectionRequest.getReasonBody());
            emailSender.sendEmail(email);
        }

        return new JsonResult(new AccountRequestData(accountRequest));
    }
}
