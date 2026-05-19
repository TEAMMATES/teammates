package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Logger;
import teammates.storage.entity.AccountRequest;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;

/**
 * Creates a new instructor account with sample courses.
 */
public class CreateAccountAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        // Any user can create instructor account as long as the registration key is valid.
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        String registrationKey = getNonNullRequestParamValue(Const.ParamsNames.REGKEY);
        String timezone = getRequestParamValue(Const.ParamsNames.TIMEZONE);

        if (timezone == null || !FieldValidator.getInvalidityInfoForTimeZone(timezone).isEmpty()) {
            // Use default timezone instead
            timezone = Const.DEFAULT_TIME_ZONE;
        }

        AccountRequest accountRequest = logic.getAccountRequestByRegistrationKey(registrationKey);

        if (accountRequest == null) {
            throw new EntityNotFoundException("Account request with registration key "
                    + registrationKey + " could not be found");
        }

        if (accountRequest.getRegisteredAt() != null) {
            throw new InvalidOperationException("The registration key " + registrationKey + " has already been used.");
        }

        try {
            logic.createAccountWithDemoCourse(authContext.account().getGoogleId(), accountRequest.getId(), timezone);
        } catch (InvalidParametersException | EntityDoesNotExistException | EntityAlreadyExistsException e) {
            log.severe("Unexpected error creating account with demo course", e);
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        return new JsonResult("Account successfully created", HttpStatus.SC_OK);
    }
}
