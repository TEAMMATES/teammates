package teammates.ui.webapi;

import java.time.Instant;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.ui.output.JoinLinkData;

/**
 * Action: resets an account request.
 */
class ResetAccountRequestAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() throws InvalidOperationException {
        String email = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);

        AccountRequestAttributes accountRequest = logic.getAccountRequest(email, institute);

        if (accountRequest == null) {
            throw new EntityNotFoundException("Account request with email: " + email
                    + " and institute: " + institute + " does not exist.");
        }

        if (!accountRequest.hasRegistrationKeyBeenUsedToJoin()) {
            throw new InvalidOperationException("Unable to reset account request as instructor is still unregistered.");
        }

        try {
            accountRequest = logic.updateAccountRequest(AccountRequestAttributes
                .updateOptionsBuilder(email, institute)
                .withStatus(AccountRequestStatus.APPROVED)
                .withLastProcessedAt(Instant.now())
                .withRegisteredAt(null)
                .build());
        } catch (InvalidParametersException | EntityDoesNotExistException | EntityAlreadyExistsException e) {
            // InvalidParametersException should not be thrown as validity of params verified when fetching entity.
            // EntityDoesNoExistException shuold not be thrown as existence of entity has just been validated.
            log.severe("Unexpected error", e);
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        String joinLink = accountRequest.getRegistrationUrl();
        EmailWrapper joinEmail = emailGenerator.generateNewInstructorAccountJoinEmail(
                accountRequest.getEmail(), accountRequest.getName(), joinLink);
        emailSender.sendEmail(joinEmail);

        JoinLinkData output = new JoinLinkData(joinLink);
        return new JsonResult(output);
    }

}
