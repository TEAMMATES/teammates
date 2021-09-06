package teammates.ui.webapi;

import java.security.SecureRandom;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.ui.output.JoinLinkData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Creates a new account request.
 */
class CreateAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        AccountCreateRequest createRequest = getAndValidateRequestBody(AccountCreateRequest.class);

        String instructorName = createRequest.getInstructorName().trim();
        String instructorEmail = createRequest.getInstructorEmail().trim();
        String instructorInstitution = createRequest.getInstructorInstitution().trim();
        String registrationKey = generateRegistrationKey(instructorEmail);

        AccountRequestAttributes accountRequestAttributes = AccountRequestAttributes
                .builder(instructorEmail)
                .withName(instructorName)
                .withRegistrationKey(registrationKey)
                .withInstitute(instructorInstitution)
                .build();

        try {
            logic.createOrUpdateAccountRequest(accountRequestAttributes);
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        }

        String joinLink = Config.getFrontEndAppUrl(Const.WebPageURIs.CREATE_ACCOUNT_PAGE)
                .withRegistrationKey(StringHelper.encrypt(registrationKey))
                .toAbsoluteString();

        EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                instructorEmail, instructorName, joinLink);
        emailSender.sendEmail(email);

        JoinLinkData output = new JoinLinkData(joinLink);
        return new JsonResult(output);
    }

    /**
     * Generate unique registration key for the account.
     * The key contains random elements to avoid being guessed.
     */
    private String generateRegistrationKey(String uniqueId) {
        SecureRandom prng = new SecureRandom();

        return uniqueId + prng.nextInt();
    }

}
