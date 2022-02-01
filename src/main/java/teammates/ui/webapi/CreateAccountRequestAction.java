package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.ui.output.JoinLinkData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new account request.
 */
class CreateAccountRequestAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        AccountCreateRequest createRequest = getAndValidateRequestBody(AccountCreateRequest.class);

        String instructorName = createRequest.getInstructorName().trim();
        String instructorEmail = createRequest.getInstructorEmail().trim();
        String instructorInstitution = createRequest.getInstructorInstitution().trim();

        AccountRequestAttributes accountRequestToCreate = AccountRequestAttributes
                .builder(instructorEmail, instructorInstitution, instructorName)
                .build();
        AccountRequestAttributes accountRequestAttributes;

        try {
            accountRequestAttributes = logic.createAccountRequest(accountRequestToCreate);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        } catch (EntityAlreadyExistsException eaee) {
            // Use existing account request
            accountRequestAttributes = logic.getAccountRequest(instructorEmail, instructorInstitution);
        }

        if (accountRequestAttributes == null) {
            String errorMessage = "Account Request for instructor with email " + instructorEmail
                    + " and institute " + instructorInstitution + " could not be found";
            log.severe("Unexpected error: " + errorMessage);
            return new JsonResult(errorMessage, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        String joinLink = accountRequestAttributes.getRegistrationUrl();

        EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                instructorEmail, instructorName, joinLink);
        emailSender.sendEmail(email);

        JoinLinkData output = new JoinLinkData(joinLink);
        return new JsonResult(output);
    }

}
