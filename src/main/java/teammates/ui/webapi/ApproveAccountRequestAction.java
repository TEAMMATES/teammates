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
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Approves an account request.
 */
public class ApproveAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        UUID accountRequestId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);

        AccountRequest accountRequest;
        try {
            accountRequest = logic.approveAccountRequest(accountRequestId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidOperationException(e);
        }

        EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                accountRequest.getEmail(), accountRequest.getName(), accountRequest.getRegistrationUrl());
        emailSender.sendEmail(email);

        return new JsonResult(new AccountRequestData(accountRequest));
    }
}
