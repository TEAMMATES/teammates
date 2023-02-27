package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidOperationException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.JoinLinkData;

/**
 * Action: resets an account request.
 */
class ResetAccountRequestAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() throws teammates.ui.webapi.InvalidOperationException {
        String instructorEmail = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);

        AccountRequest accountRequest = null;
        try {
            accountRequest = sqlLogic.updateAccountRequest(instructorEmail, institute);
        } catch (InvalidParametersException e) {
            // InvalidParametersException should not be thrown as validity of params verified when fetching entity.
            log.severe("Unexpected error", e);
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee.getMessage());
        } catch (InvalidOperationException ioe) {
            throw new teammates.ui.webapi.InvalidOperationException(ioe.getMessage());
        }

        String joinLink = accountRequest.getRegistrationUrl();
        EmailWrapper email = emailGenerator.generateNewInstructorAccountJoinEmail(
                accountRequest.getEmail(), accountRequest.getName(), joinLink);
        emailSender.sendEmail(email);

        JoinLinkData output = new JoinLinkData(joinLink);
        return new JsonResult(output);
    }

}
