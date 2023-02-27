package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidOperationException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.JoinLinkData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new account request.
 */
class CreateAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute()
        throws InvalidHttpRequestBodyException, teammates.ui.webapi.InvalidOperationException {
        AccountCreateRequest createRequest = getAndValidateRequestBody(AccountCreateRequest.class);

        String instructorName = createRequest.getInstructorName().trim();
        String instructorEmail = createRequest.getInstructorEmail().trim();
        String instructorInstitution = createRequest.getInstructorInstitution().trim();

        AccountRequest accountRequest = null;

        try {
            accountRequest = sqlLogic.createAccountRequest(instructorName, instructorEmail, instructorInstitution);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        } catch (EntityAlreadyExistsException eaee) {
            // Use existing account request
            accountRequest = sqlLogic.getAccountRequest(instructorEmail, instructorInstitution);
        } catch (InvalidOperationException ioe) {
            throw new teammates.ui.webapi.InvalidOperationException(ioe.getMessage());
        }

        assert accountRequest != null;

        String joinLink = accountRequest.getRegistrationUrl();

        EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                instructorEmail, instructorName, joinLink);
        emailSender.sendEmail(email);

        JoinLinkData output = new JoinLinkData(joinLink);
        return new JsonResult(output);
    }

}
