package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.AccountRequest;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;
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

        AccountRequestUpdateRequest updateRequest = getAndValidateRequestBody(AccountRequestUpdateRequest.class);

        AccountRequest accountRequest;
        try {
            accountRequest = logic.updateAccountRequestDetails(accountRequestId,
                    updateRequest.getName(), updateRequest.getEmail(),
                    updateRequest.getInstitute(), updateRequest.getComments());
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        return new JsonResult(new AccountRequestData(accountRequest));
    }
}
