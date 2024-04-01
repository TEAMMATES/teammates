package teammates.ui.webapi;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.JoinLinkData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new account request.
 */
public class CreateAccountRequestAction extends AdminOnlyAction {

    @Override
    public boolean isTransactionNeeded() {
        return false;
    }

    @Override
    public JsonResult execute()
            throws InvalidHttpRequestBodyException, InvalidOperationException {
        AccountCreateRequest createRequest = getAndValidateRequestBody(AccountCreateRequest.class);

        String instructorName = createRequest.getInstructorName().trim();
        String instructorEmail = createRequest.getInstructorEmail().trim();
        String instructorInstitution = createRequest.getInstructorInstitution().trim();

        AccountRequest accountRequest;

        try {
            accountRequest =
                sqlLogic.createAccountRequestWithTransaction(instructorName, instructorEmail, instructorInstitution);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        }

        taskQueuer.scheduleAccountRequestForSearchIndexing(instructorEmail, instructorInstitution);

        if (accountRequest.getRegisteredAt() != null) {
            throw new InvalidOperationException("Cannot create account request as instructor has already registered.");
        }

        String joinLink = accountRequest.getRegistrationUrl();

        EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                instructorEmail, instructorName, joinLink);
        emailSender.sendEmail(email);

        JoinLinkData output = new JoinLinkData(joinLink);
        return new JsonResult(output);
    }

}
