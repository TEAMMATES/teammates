package teammates.ui.webapi;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountVerificationRequestData;
import teammates.ui.request.AccountCreateRequest;

/**
 * Creates a new account verification request.
 */
public class CreateAccountVerificationRequestAction extends LoggedInAction {

    @Override
    void checkSpecificAccessControl() {
        // Any logged in user can create an account verification request.
    }

    @Override
    public JsonResult execute()
            throws InvalidHttpRequestBodyException, InvalidOperationException {
        AccountCreateRequest createRequest = getAndValidateRequestBody(AccountCreateRequest.class);

        String instructorName = createRequest.getInstructorName().trim();
        String instructorEmail = createRequest.getInstructorEmail().trim();
        String instructorInstitution = createRequest.getInstructorInstitution().trim();
        String instructorCountry = createRequest.getInstructorCountry().trim();
        String comments = createRequest.getInstructorComments();
        if (comments != null) {
            comments = comments.trim();
        }
        Account account = getCurrentAccount();

        try {
            AccountVerificationRequest accountVerificationRequest = logic.createAccountVerificationRequest(
                    instructorName, instructorEmail, instructorInstitution, instructorCountry, comments, account.getId());
            EmailWrapper adminAlertEmail = emailGenerator
                    .generateNewAccountVerificationRequestAdminAlertEmail(accountVerificationRequest);
            EmailWrapper userAcknowledgementEmail = emailGenerator
                    .generateNewAccountVerificationRequestAcknowledgementEmail(accountVerificationRequest);
            emailQueueService.enqueuePriority(adminAlertEmail);
            emailQueueService.enqueuePriority(userAcknowledgementEmail);
            return new JsonResult(new AccountVerificationRequestData(accountVerificationRequest));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        }
    }

}
