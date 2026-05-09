package teammates.ui.webapi;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.AccountRequest;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
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
        AccountRequestStatus initialStatus = AccountRequestStatus.PENDING;

        if (userInfo != null && userInfo.isAdmin) {
            initialStatus = AccountRequestStatus.APPROVED;

            if (!logic.getApprovedAccountRequestsForEmailAndInstitute(instructorEmail, instructorInstitution).isEmpty()) {
                throw new InvalidOperationException(String.format(
                        "An account request with email %s and institute %s has already been approved. "
                                + "Please reject or delete the account request instead.",
                        instructorEmail, instructorInstitution));
            }

            if (logic.getInstructorForEmailAndInstitute(instructorEmail, instructorInstitution) != null) {
                throw new InvalidOperationException(String.format(
                        "An instructor with email %s and institute %s already exists. "
                                + "Please reject or delete the account request instead.",
                        instructorEmail, instructorInstitution));
            }
        }

        try {
            accountRequest = logic.createAccountRequest(instructorName, instructorEmail,
                    instructorInstitution, initialStatus, comments);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        }

        assert accountRequest != null;

        if (userInfo == null || !userInfo.isAdmin) {
            EmailWrapper adminAlertEmail = emailGenerator.generateNewAccountRequestAdminAlertEmail(accountRequest);
            EmailWrapper userAcknowledgementEmail = emailGenerator
                    .generateNewAccountRequestAcknowledgementEmail(accountRequest);
            emailSender.sendEmail(adminAlertEmail);
            emailSender.sendEmail(userAcknowledgementEmail);
        } else {
            EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                    accountRequest.getEmail(), accountRequest.getName(), accountRequest.getRegistrationUrl());
            emailSender.sendEmail(email);
        }

        AccountRequestData output = new AccountRequestData(accountRequest);
        return new JsonResult(output);
    }

}
