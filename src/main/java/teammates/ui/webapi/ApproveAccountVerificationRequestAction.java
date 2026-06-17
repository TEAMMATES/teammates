package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.InvalidVerificationRequestStateException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.LinksUtil;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountVerificationRequestData;

/**
 * Approves an account verification request.
 */
public class ApproveAccountVerificationRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        UUID accountVerificationRequestId = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID);

        try {
            AccountVerificationRequest accountVerificationRequest =
                    logic.approveAccountVerificationRequest(accountVerificationRequestId);
            EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                    accountVerificationRequest.getEmail(), accountVerificationRequest.getName(),
                    LinksUtil.getInstructorWelcomeUrl(accountVerificationRequest.getId()));
            emailQueueService.enqueuePriority(email);
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
