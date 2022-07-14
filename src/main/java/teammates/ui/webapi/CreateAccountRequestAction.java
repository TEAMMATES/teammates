package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.AccountRequestCreateResponseData;
import teammates.ui.request.AccountRequestCreateIntent;
import teammates.ui.request.AccountRequestCreateRequest;
import teammates.ui.request.AccountRequestType;
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
        AccountRequestCreateIntent intent =
                AccountRequestCreateIntent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case ADMIN_CREATE:
            if (userInfo == null || !userInfo.isAdmin) {
                throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
            }
            break;
        case PUBLIC_CREATE:
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        AccountRequestCreateIntent intent =
                AccountRequestCreateIntent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        if (!intent.equals(AccountRequestCreateIntent.ADMIN_CREATE)) {
            String userCaptchaResponse = getRequestParamValue(Const.ParamsNames.USER_CAPTCHA_RESPONSE);
            if (userCaptchaResponse == null || !recaptchaVerifier.isVerificationSuccessful(userCaptchaResponse)) {
                throw new InvalidHttpParameterException("Please check the \"I'm not a robot\" box.");
            }
        }

        String type = getRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_TYPE);
        if (type == null || AccountRequestType.valueOf(type) != AccountRequestType.INSTRUCTOR_ACCOUNT) {
            throw new InvalidHttpParameterException("Only instructor accounts can be created.");
        }

        AccountRequestCreateRequest createRequest = getAndValidateRequestBody(AccountRequestCreateRequest.class);

        String instructorName = createRequest.getInstructorName().trim();
        String instructorInstitute = createRequest.getInstructorInstitute().trim();
        String instructorCountry = createRequest.getInstructorCountry().trim();
        String instructorEmail = createRequest.getInstructorEmail().trim();
        String instructorHomePageUrl = createRequest.getInstructorHomePageUrl().trim();
        String otherComments = createRequest.getOtherComments().trim();

        AccountRequestAttributes accountRequestToCreate;
        AccountRequestAttributes accountRequestAttributes;
        AccountRequestCreateResponseData output = new AccountRequestCreateResponseData();

        try {
            switch (intent) {
            case ADMIN_CREATE:
                accountRequestToCreate = AccountRequestAttributes
                        .builder(instructorName, instructorInstitute, instructorEmail, instructorHomePageUrl, otherComments)
                        .build();
                accountRequestAttributes = logic.createAndApproveAccountRequest(accountRequestToCreate);
                // only schedule for search indexing if account request created successfully
                taskQueuer.scheduleAccountRequestForSearchIndexing(accountRequestAttributes.getEmail(),
                        accountRequestAttributes.getInstitute());

                String joinLink = accountRequestAttributes.getRegistrationUrl();
                EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                        instructorEmail, instructorName, joinLink);
                emailSender.sendEmail(email);

                output.setMessage("Account request successfully created and approved.");
                output.setJoinLink(joinLink);
                break;

            case PUBLIC_CREATE:
                accountRequestToCreate = AccountRequestAttributes
                        .builder(instructorName, instructorInstitute, instructorCountry, instructorEmail,
                                instructorHomePageUrl, otherComments)
                        .build();
                accountRequestAttributes = logic.createAccountRequest(accountRequestToCreate);
                // only schedule for search indexing if account request created successfully
                taskQueuer.scheduleAccountRequestForSearchIndexing(accountRequestAttributes.getEmail(),
                        accountRequestAttributes.getInstitute());

                output.setMessage("Account request successfully created.");
                break;

            default:
                throw new InvalidHttpParameterException("Unknown intent " + intent);
            }
        } catch (InvalidParametersException ipe) { // invalid parameters are caught here
            throw new InvalidHttpRequestBodyException(ipe);
        } catch (EntityAlreadyExistsException eaee) {
            throw new InvalidOperationException(generateExistingAccountRequestErrorMessage(
                    intent, instructorEmail, instructorInstitute
            ));
        } catch (EntityDoesNotExistException ednee) {
            return new JsonResult(ednee.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        return new JsonResult(output);
    }

    private String generateExistingAccountRequestErrorMessage(AccountRequestCreateIntent intent,
                                                              String instructorEmail, String instructorInstitute) {
        switch (intent) {
        case ADMIN_CREATE:
            AccountRequestAttributes accountRequest = logic.getAccountRequest(instructorEmail, instructorInstitute);
            return "An account request already exists with status " + accountRequest.getStatus() + ".";
        case PUBLIC_CREATE:
            return "Oops, an account request already exists."
                    + " Please check if you have entered your personal information correctly."
                    + " If you think this shouldn't happen, contact us at the email address given above.";
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

}
