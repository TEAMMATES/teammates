package teammates.ui.webapi;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new account request.
 */
public class CreateAccountRequestAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Nothing needs to be done here because anybody should be able to create an account request.
    }

    @Override
    public boolean isTransactionNeeded() {
        return false;
    }

    @Override
    public JsonResult execute()
            throws InvalidHttpRequestBodyException, InvalidOperationException {
        AccountCreateRequest createRequest = getAndValidateRequestBody(AccountCreateRequest.class);

        if (userInfo == null || !userInfo.isAdmin) {
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
            accountRequest = sqlLogic.createAccountRequestWithTransaction(instructorName, instructorEmail,
                    instructorInstitution, AccountRequestStatus.PENDING, comments);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        }

        taskQueuer.scheduleAccountRequestForSearchIndexing(accountRequest.getId().toString());

        assert accountRequest != null;

        if (userInfo == null || !userInfo.isAdmin) {
            EmailWrapper adminAlertEmail = sqlEmailGenerator.generateNewAccountRequestAdminAlertEmail(accountRequest);
            EmailWrapper userAcknowledgementEmail = sqlEmailGenerator
                    .generateNewAccountRequestAcknowledgementEmail(accountRequest);
            emailSender.sendEmail(adminAlertEmail);
            emailSender.sendEmail(userAcknowledgementEmail);
        }

        AccountRequestData output = new AccountRequestData(accountRequest);
        return new JsonResult(output);
    }

}
