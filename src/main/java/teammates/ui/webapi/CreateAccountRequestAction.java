package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.JoinLinkData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new account request.
 */
class CreateAccountRequestAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // TODO: check based on intent
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        AccountCreateRequest createRequest = getAndValidateRequestBody(AccountCreateRequest.class);

        String instructorName = createRequest.getInstructorName().trim();
        String instructorInstitute = createRequest.getInstructorInstitute().trim();
        String instructorCountry = createRequest.getInstructorCountry().trim();
        String instructorEmail = createRequest.getInstructorEmail().trim();
        String instructorHomePageUrl = createRequest.getInstructorHomePageUrl().trim();
        String otherComments = createRequest.getOtherComments().trim();

        AccountRequestAttributes accountRequestToCreate = AccountRequestAttributes
                .builder(instructorName, instructorInstitute, instructorCountry, instructorEmail,
                        instructorHomePageUrl, otherComments)
                .build();
        AccountRequestAttributes accountRequestAttributes;

        try {
            accountRequestAttributes = logic.createAccountRequest(accountRequestToCreate);
            // only schedule for search indexing if account request created successfully
            taskQueuer.scheduleAccountRequestForSearchIndexing(accountRequestAttributes.getEmail(),
                    accountRequestAttributes.getInstitute());
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe); // invalid parameters are caught here
        } catch (EntityAlreadyExistsException eaee) {
            throw new InvalidOperationException("Account request already exists.", eaee);

            // Use existing account request
//            accountRequestAttributes = logic.getAccountRequest(instructorEmail, instructorInstitute);
        }

        assert accountRequestAttributes != null;

        if (accountRequestAttributes.getRegisteredAt() != null) {
            // shouldn't be executed
            throw new InvalidOperationException("Cannot create account request as instructor has already registered.");
        }

        String joinLink = accountRequestAttributes.getRegistrationUrl();

        EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                instructorEmail, instructorName, joinLink);
        emailSender.sendEmail(email);

        JoinLinkData output = new JoinLinkData(joinLink);
        return new JsonResult(output);
    }

}
