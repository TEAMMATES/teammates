package teammates.ui.webapi;

import java.util.UUID;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.exception.UnexpectedServerException;

/**
 * Creates a new demo course with a demo instructor and student.
 */
public class CreateDemoCourseAction extends LoggedInAction {

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID);
        gateKeeper.verifyCanViewAccountVerificationRequest(requestContext, id);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID id = getUuidRequestParamValue(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID);
        String timezone = getRequestParamValue(Const.ParamsNames.TIMEZONE);

        try {
            logic.createDemoCourse(id, timezone, getCurrentAccount());
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException(e);
        } catch (InvalidParametersException e) {
            throw new UnexpectedServerException(e);
        }

        return new JsonResult("Demo course successfully created", HttpStatus.SC_OK);
    }
}
