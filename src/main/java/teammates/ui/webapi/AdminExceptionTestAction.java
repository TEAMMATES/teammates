package teammates.ui.webapi;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.Const;

/**
 * Action specifically created for testing exception handling at API servlet.
 */
class AdminExceptionTestAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        if (!Config.isDevServer()) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException") // deliberately done for testing
    JsonResult execute() {
        String error = getNonNullRequestParamValue(Const.ParamsNames.ERROR);
        if (error.equals(AssertionError.class.getSimpleName())) {
            throw new AssertionError("AssertionError testing");
        }
        if (error.equals(NullPointerException.class.getSimpleName())) {
            throw new NullPointerException("NullPointerException testing");
        }
        if (error.equals(DeadlineExceededException.class.getSimpleName())) {
            throw new DeadlineExceededException("DeadlineExceededException testing");
        }
        if (error.equals(DatastoreTimeoutException.class.getSimpleName())) {
            throw new DatastoreTimeoutException("DatastoreTimeoutException testing");
        }
        if (error.equals(InvalidHttpParameterException.class.getSimpleName())) {
            throw new InvalidHttpParameterException("InvalidHttpParameterException testing");
        }
        if (error.equals(UnauthorizedAccessException.class.getSimpleName())) {
            throw new UnauthorizedAccessException("UnauthorizedAccessException testing");
        }
        if (error.equals(EntityNotFoundException.class.getSimpleName())) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("EntityNotFoundException testing"));
        }
        return new JsonResult("Test output");
    }

}
