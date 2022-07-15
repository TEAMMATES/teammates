package teammates.ui.webapi;

import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.ui.output.AccountRequestCreateResponseData;
import teammates.ui.request.AccountRequestCreateIntent;
import teammates.ui.request.AccountRequestCreateRequest;
import teammates.ui.request.AccountRequestType;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new account request.
 */
class CreateAccountRequestAction extends Action {

    private static final Logger log = Logger.getLogger();

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
        AccountRequestCreateResponseData.AccountRequestCreateErrorResults errorResults =
                new AccountRequestCreateResponseData.AccountRequestCreateErrorResults();

        AccountRequestCreateIntent intent =
                AccountRequestCreateIntent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        if (!intent.equals(AccountRequestCreateIntent.ADMIN_CREATE)) {
            String userCaptchaResponse = getRequestParamValue(Const.ParamsNames.USER_CAPTCHA_RESPONSE);
            if (userCaptchaResponse == null || !recaptchaVerifier.isVerificationSuccessful(userCaptchaResponse)) {
                throw new InvalidHttpParameterException("Please check the \"I'm not a robot\" box before submission.");
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
                if (!validateAccountRequestAndPopulateErrorResults(intent, accountRequestToCreate, errorResults)) {
                    log.warning("Account request fails to be created: invalid request.",
                            new InvalidHttpRequestBodyException("Account request fails to be created: invalid request."));
                    return new JsonResult(errorResults, HttpStatus.SC_BAD_REQUEST);
                }

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
                if (!validateAccountRequestAndPopulateErrorResults(intent, accountRequestToCreate, errorResults)) {
                    log.warning("Account request fails to be created: invalid request.",
                            new InvalidHttpRequestBodyException("Account request fails to be created: invalid request."));
                    return new JsonResult(errorResults, HttpStatus.SC_BAD_REQUEST);
                }

                accountRequestAttributes = logic.createAccountRequest(accountRequestToCreate);
                // only schedule for search indexing if account request created successfully
                taskQueuer.scheduleAccountRequestForSearchIndexing(accountRequestAttributes.getEmail(),
                        accountRequestAttributes.getInstitute());

                output.setMessage("Account request successfully created.");
                break;

            default:
                throw new InvalidHttpParameterException("Unknown intent " + intent);
            }
        } catch (EntityAlreadyExistsException eaee) {
            throw new InvalidOperationException(generateExistingAccountRequestErrorMessage(
                    intent, instructorEmail, instructorInstitute), eaee);
        } catch (InvalidParametersException ipe) {
            // account request has been validated before so this exception should not happen
            log.severe("Encountered exception when creating account request: " + ipe.getMessage(), ipe);
            return new JsonResult("The server encountered an error when processing your request.",
                    HttpStatus.SC_INTERNAL_SERVER_ERROR);
        } catch (EntityDoesNotExistException ednee) {
            // error has been logged in method createAndApproveAccountRequest()
            return new JsonResult("The server encountered an error when processing your request.",
                    HttpStatus.SC_INTERNAL_SERVER_ERROR);
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
            return "Oops, your submission is unsuccessful because an account request already exists."
                    + " Please check if you have entered your personal information correctly."
                    + " If you think this shouldn't happen, contact us at the email address given above.";
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    private boolean validateAccountRequestAndPopulateErrorResults(
            AccountRequestCreateIntent intent, AccountRequestAttributes accountRequest,
            AccountRequestCreateResponseData.AccountRequestCreateErrorResults errorResults) {
        List<String> invalidityInfo = accountRequest.getInvalidityInfo();
        if (invalidityInfo.isEmpty()) {
            return true;
        }

        for (String i : invalidityInfo) {
            if (i.startsWith(
                    AccountRequestAttributes.generatePrefix(FieldValidator.PERSON_NAME_FIELD_NAME))) {
                i = StringHelper.removeFirstOccurrenceOfSubstring(i,
                        AccountRequestAttributes.generatePrefix(FieldValidator.PERSON_NAME_FIELD_NAME));
                errorResults.setInvalidNameMessage(i);
            } else if (i.startsWith(
                    AccountRequestAttributes.generatePrefix(FieldValidator.ACCOUNT_REQUEST_INSTITUTE_NAME_FIELD_NAME))) {
                i = StringHelper.removeFirstOccurrenceOfSubstring(i,
                        AccountRequestAttributes.generatePrefix(FieldValidator.ACCOUNT_REQUEST_INSTITUTE_NAME_FIELD_NAME));
                errorResults.setInvalidInstituteMessage(i);
            } else if (i.startsWith(
                    AccountRequestAttributes.generatePrefix(FieldValidator.ACCOUNT_REQUEST_COUNTRY_NAME_FIELD_NAME))) {
                i = StringHelper.removeFirstOccurrenceOfSubstring(i,
                        AccountRequestAttributes.generatePrefix(FieldValidator.ACCOUNT_REQUEST_COUNTRY_NAME_FIELD_NAME));
                errorResults.setInvalidCountryMessage(i);
            } else if (intent.equals(AccountRequestCreateIntent.ADMIN_CREATE) && i.startsWith(
                    AccountRequestAttributes.generatePrefix(FieldValidator.INSTITUTE_NAME_FIELD_NAME))) {
                i = StringHelper.removeFirstOccurrenceOfSubstring(i,
                        AccountRequestAttributes.generatePrefix(FieldValidator.INSTITUTE_NAME_FIELD_NAME));
                errorResults.setInvalidInstituteMessage(i);
            } else if (i.startsWith(
                    AccountRequestAttributes.generatePrefix(FieldValidator.EMAIL_FIELD_NAME))) {
                i = StringHelper.removeFirstOccurrenceOfSubstring(i,
                        AccountRequestAttributes.generatePrefix(FieldValidator.EMAIL_FIELD_NAME));
                errorResults.setInvalidEmailMessage(i);
            } else if (i.startsWith(
                    AccountRequestAttributes.generatePrefix(FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_FIELD_NAME))) {
                i = StringHelper.removeFirstOccurrenceOfSubstring(i,
                        AccountRequestAttributes.generatePrefix(FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_FIELD_NAME));
                errorResults.setInvalidHomePageUrlMessage(i);
            } else if (i.startsWith(
                    AccountRequestAttributes.generatePrefix(FieldValidator.ACCOUNT_REQUEST_COMMENTS_FIELD_NAME))) {
                i = StringHelper.removeFirstOccurrenceOfSubstring(i,
                        AccountRequestAttributes.generatePrefix(FieldValidator.ACCOUNT_REQUEST_COMMENTS_FIELD_NAME));
                errorResults.setInvalidCommentsMessage(i);
            }
        }
        return false;
    }

}
