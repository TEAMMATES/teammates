package teammates.ui.webapi;

import java.util.UUID;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates an account request.
 */
public class UpdateAccountRequestAction extends AdminOnlyAction {

    static final String ACCOUNT_REQUEST_NOT_FOUND = "Account request with id = %s not found";
    static final String ACCOUNT_REQUEST_APPROVED_EMAIL_FAILED =
            "Failed to send approval email to instructor, status reverted to PENDING, please try again";

    @Override
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        String id = getNonNullRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);
        UUID accountRequestId;

        try {
            accountRequestId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(e.getMessage(), e);
        }

        AccountRequest accountRequest = sqlLogic.getAccountRequest(accountRequestId);

        if (accountRequest == null) {
            String errorMessage = String.format(ACCOUNT_REQUEST_NOT_FOUND, accountRequestId.toString());
            return new JsonResult(errorMessage, HttpStatus.SC_NOT_FOUND);
        }

        AccountRequestUpdateRequest accountRequestUpdateRequest =
                getAndValidateRequestBody(AccountRequestUpdateRequest.class);

        if (accountRequestUpdateRequest.getStatus() == AccountRequestStatus.APPROVED
                && (accountRequest.getStatus() == AccountRequestStatus.PENDING
                || accountRequest.getStatus() == AccountRequestStatus.REJECTED)) {
            try {
                // should not need to update other fields for an approval
                accountRequest.setStatus(accountRequestUpdateRequest.getStatus());
                sqlLogic.updateAccountRequest(accountRequest);
                boolean emailSent = sendEmail(
                        accountRequest.getRegistrationUrl(), accountRequest.getEmail(), accountRequest.getName());
                if (!emailSent) {
                    // revert status to PENDING if email sending failed, so it can be re-triggered by approving again
                    accountRequest.setStatus(AccountRequestStatus.PENDING);
                    sqlLogic.updateAccountRequest(accountRequest);
                    return new JsonResult(ACCOUNT_REQUEST_APPROVED_EMAIL_FAILED);
                }
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
                sqlLogic.updateAccountRequest(accountRequest);
            } catch (InvalidParametersException e) {
                throw new InvalidHttpRequestBodyException(e);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        }

        return new JsonResult(new AccountRequestData(accountRequest));
    }

    /**
     * Sends the approval email to the instructor.
     *
     * @return The true if email was sent successfully or false otherwise.
     */
    private boolean sendEmail(String registrationUrl, String instructorEmail, String instructorName) {
        EmailWrapper email = sqlEmailGenerator.generateNewInstructorAccountJoinEmail(
                registrationUrl, instructorEmail, instructorName);
        EmailSendingStatus status = emailSender.sendEmail(email);
        return status.isSuccess();
    }
}
