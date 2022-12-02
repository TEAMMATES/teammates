package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.Logger;
import teammates.ui.output.AccountRequestCreateErrorResults;
import teammates.ui.output.JoinLinkData;
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
        String comments = createRequest.getComments().trim();

        AccountRequestAttributes accountRequestToCreate;
        AccountRequestAttributes accountRequestAttributes;

        switch (intent) {
        case ADMIN_CREATE:
            accountRequestToCreate = AccountRequestAttributes
                    .builder(instructorName, instructorInstitute, instructorEmail, instructorHomePageUrl, comments)
                    .build();

            try {
                accountRequestAttributes = logic.createAndApproveAccountRequest(accountRequestToCreate);
                // only schedule for search indexing if account request created successfully
                taskQueuer.scheduleAccountRequestForSearchIndexing(accountRequestAttributes.getEmail(),
                        accountRequestAttributes.getInstitute());
            } catch (EntityAlreadyExistsException eaee) {
                AccountRequestAttributes accountRequest = logic.getAccountRequest(instructorEmail, instructorInstitute);
                throw new InvalidOperationException(
                        "An account request already exists with status " + accountRequest.getStatus() + ".", eaee);
            } catch (InvalidParametersException ipe) {
                throw new InvalidHttpRequestBodyException(ipe);
            }

            String joinLink = accountRequestAttributes.getRegistrationUrl();
            EmailWrapper joinEmail = emailGenerator.generateNewInstructorAccountJoinEmail(
                    instructorEmail, instructorName, joinLink);
            emailSender.sendEmail(joinEmail);

            return new JsonResult(new JoinLinkData(joinLink));

        case PUBLIC_CREATE:
            AccountRequestCreateErrorResults errorResults = new AccountRequestCreateErrorResults();
            if (!validateAccountRequestAndPopulateErrorResults(instructorName, instructorInstitute, instructorCountry,
                    instructorEmail, instructorHomePageUrl, comments, errorResults)) {
                log.warning("Account request fails to be created: invalid request.",
                        new InvalidHttpRequestBodyException("Account request fails to be created: invalid request."));
                return new JsonResult(errorResults, HttpStatus.SC_BAD_REQUEST);
            }
            accountRequestToCreate = AccountRequestAttributes
                    .builder(instructorName, generateInstitute(instructorInstitute, instructorCountry), instructorEmail,
                            instructorHomePageUrl, comments)
                    .build();

            try {
                accountRequestAttributes = logic.createAccountRequest(accountRequestToCreate);
                // only schedule for search indexing if account request created successfully
                taskQueuer.scheduleAccountRequestForSearchIndexing(accountRequestAttributes.getEmail(),
                        accountRequestAttributes.getInstitute());

                return new JsonResult("Account request successfully created.");
            } catch (EntityAlreadyExistsException eaee) {
                throw new InvalidOperationException(eaee);
            } catch (InvalidParametersException ipe) {
                // account request has been validated before so this exception should not happen
                log.severe("Encountered exception when creating account request: " + ipe.getMessage(), ipe);
                return new JsonResult("The server encountered an error when processing your request.",
                        HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }

        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    /**
     * Generates the AccountRequest {@code institute} field by combining {@code arInstitute} and {@code arCountry}.
     */
    public static String generateInstitute(String arInstitute, String arCountry) {
        assert arInstitute != null;
        assert arCountry != null;

        return arInstitute + ", " + arCountry;
    }

    private boolean validateAccountRequestAndPopulateErrorResults(
            String name, String arInstitute, String arCountry, String email, String homePageUrl, String comments,
            AccountRequestCreateErrorResults errorResults) {
        boolean isValid = true;
        String invalidityInfo;

        invalidityInfo = FieldValidator.getInvalidityInfoForPersonName(name);
        if (!invalidityInfo.isEmpty()) {
            isValid = false;
            errorResults.setInvalidNameMessage(invalidityInfo);
        }

        invalidityInfo = FieldValidator.getInvalidityInfoForAccountRequestInstituteName(arInstitute);
        if (!invalidityInfo.isEmpty()) {
            isValid = false;
            errorResults.setInvalidInstituteMessage(invalidityInfo);
        }

        invalidityInfo = FieldValidator.getInvalidityInfoForAccountRequestCountryName(arCountry);
        if (!invalidityInfo.isEmpty()) {
            isValid = false;
            errorResults.setInvalidCountryMessage(invalidityInfo);
        }

        invalidityInfo = FieldValidator.getInvalidityInfoForEmail(email);
        if (!invalidityInfo.isEmpty()) {
            isValid = false;
            errorResults.setInvalidEmailMessage(invalidityInfo);
        }

        invalidityInfo = FieldValidator.getInvalidityInfoForAccountRequestHomePageUrl(homePageUrl);
        if (!invalidityInfo.isEmpty()) {
            isValid = false;
            errorResults.setInvalidHomePageUrlMessage(invalidityInfo);
        }

        invalidityInfo = FieldValidator.getInvalidityInfoForAccountRequestComments(comments);
        if (!invalidityInfo.isEmpty()) {
            isValid = false;
            errorResults.setInvalidCommentsMessage(invalidityInfo);
        }

        return isValid;
    }

}
