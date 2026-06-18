package teammates.ui.webapi;

import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.Account;
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
            AccountVerificationRequestData output = new AccountVerificationRequestData(
                    logic.createAccountVerificationRequest(
                            instructorName, instructorEmail, instructorInstitution,
                            instructorCountry, comments, account.getId()));
            return new JsonResult(output);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        }
    }

}
