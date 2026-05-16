package teammates.ui.webapi;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.AccountRequest;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new account request.
 */
public class CreateAccountRequestAction extends PublicAction {

    @Override
    public JsonResult execute()
            throws InvalidHttpRequestBodyException, InvalidOperationException {
        AccountCreateRequest createRequest = getAndValidateRequestBody(AccountCreateRequest.class);

        if (authContext == null || !authContext.isAdmin()) {
            String userCaptchaResponse = createRequest.getCaptchaResponse();
            if (!recaptchaVerifier.isVerificationSuccessful(userCaptchaResponse)) {
                throw new InvalidHttpRequestBodyException("Something went wrong with "
                        + "the reCAPTCHA verification. Please try again.");
            }
        }

        String instructorName = createRequest.getInstructorName().trim();
        String instructorEmail = createRequest.getInstructorEmail().trim();
        String instructorInstitution = createRequest.getInstructorInstitution().trim();
        String comments = createRequest.getInstructorComments();
        if (comments != null) {
            comments = comments.trim();
        }
        AccountRequest accountRequest;

        try {
            accountRequest = logic.createAccountRequest(instructorName, instructorEmail,
                    instructorInstitution, AccountRequestStatus.PENDING, comments);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        }

        assert accountRequest != null;

        if (authContext == null || !authContext.isAdmin()) {
            EmailWrapper adminAlertEmail = emailGenerator.generateNewAccountRequestAdminAlertEmail(accountRequest);
            EmailWrapper userAcknowledgementEmail = emailGenerator
                    .generateNewAccountRequestAcknowledgementEmail(accountRequest);
            emailSender.sendEmail(adminAlertEmail);
            emailSender.sendEmail(userAcknowledgementEmail);
        }

        AccountRequestData output = new AccountRequestData(accountRequest);
        return new JsonResult(output);
    }

}
