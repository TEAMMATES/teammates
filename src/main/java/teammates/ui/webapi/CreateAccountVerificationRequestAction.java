package teammates.ui.webapi;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountVerificationRequestData;
import teammates.ui.request.AccountCreateRequest;

/**
 * Creates a new account request.
 */
public class CreateAccountVerificationRequestAction extends LoggedInAction {

    @Override
    void checkSpecificAccessControl() {
        // Any logged in user can create an account request.
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
        AccountVerificationRequest accountVerificationRequest;

        try {
            accountVerificationRequest = logic.createAccountVerificationRequest(instructorName, instructorEmail,
                    instructorInstitution, instructorCountry, AccountVerificationRequestStatus.PENDING, comments,
                    account.getId());
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        }

        assert accountVerificationRequest != null;

        EmailWrapper adminAlertEmail = emailGenerator.generateNewAccountVerificationRequestAdminAlertEmail(accountVerificationRequest);
        EmailWrapper userAcknowledgementEmail = emailGenerator
                .generateNewAccountVerificationRequestAcknowledgementEmail(accountVerificationRequest);
        emailSender.sendEmail(adminAlertEmail);
        emailSender.sendEmail(userAcknowledgementEmail);

        AccountVerificationRequestData output = new AccountVerificationRequestData(accountVerificationRequest);
        return new JsonResult(output);
    }

}
