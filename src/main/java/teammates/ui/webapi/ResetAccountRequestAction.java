package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnexpectedServerException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.JoinLinkData;

/**
 * Action: resets an account request.
 */
public class ResetAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_ID);

        AccountRequest accountRequest = sqlLogic.getAccountRequest(id);

        if (accountRequest == null) {
            throw new EntityNotFoundException("Account request with id: " + id.toString() + " does not exist.");
        }
        if (accountRequest.getRegisteredAt() == null) {
            throw new InvalidOperationException("Unable to reset account request as instructor is still unregistered.");
        }

        try {
            accountRequest = sqlLogic.resetAccountRequest(id);
        } catch (InvalidParametersException | EntityDoesNotExistException ue) {
            // InvalidParametersException and EntityDoesNotExistException should not be thrown as
            // validity of params has been verified when fetching entity.
            throw new UnexpectedServerException(ue.getMessage(), ue);
        }

        String joinLink = accountRequest.getRegistrationUrl();
        EmailWrapper email = sqlEmailGenerator.generateNewInstructorAccountJoinEmail(
                accountRequest.getEmail(), accountRequest.getName(), joinLink);
        emailSender.sendEmail(email);

        JoinLinkData output = new JoinLinkData(joinLink);
        return new JsonResult(output);
    }

}
