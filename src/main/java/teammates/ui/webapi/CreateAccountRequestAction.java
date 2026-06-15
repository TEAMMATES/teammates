package teammates.ui.webapi;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountCreateRequest;

/**
 * Creates a new account request.
 */
public class CreateAccountRequestAction extends LoggedInAction {

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
        AccountRequest accountRequest;

        try {
            accountRequest = logic.createAccountRequest(instructorName, instructorEmail,
                    instructorInstitution, instructorCountry, AccountRequestStatus.PENDING, comments,
                    account.getId());
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        }

        assert accountRequest != null;

        EmailWrapper adminAlertEmail = emailGenerator.generateNewAccountRequestAdminAlertEmail(accountRequest);
        EmailWrapper userAcknowledgementEmail = emailGenerator
                .generateNewAccountRequestAcknowledgementEmail(accountRequest);
        emailSender.sendEmail(adminAlertEmail);
        emailSender.sendEmail(userAcknowledgementEmail);

        AccountRequestData output = new AccountRequestData(accountRequest);
        return new JsonResult(output);
    }

}
