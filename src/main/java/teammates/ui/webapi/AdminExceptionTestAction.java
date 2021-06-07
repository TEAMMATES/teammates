package teammates.ui.webapi;

import com.google.cloud.datastore.DatastoreException;
import com.google.rpc.Code;

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
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!Config.isDevServer()) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
        String error = getNonNullRequestParamValue(Const.ParamsNames.ERROR);
        if (error.equals(UnauthorizedAccessException.class.getSimpleName())) {
            throw new UnauthorizedAccessException("UnauthorizedAccessException testing");
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException") // deliberately done for testing
    JsonResult execute() {
        String error = getNonNullRequestParamValue(Const.ParamsNames.ERROR);
        if (error.equals(AssertionError.class.getSimpleName())) {
            assert false : "AssertionError testing";
        }
        if (error.equals(NullPointerException.class.getSimpleName())) {
            throw new NullPointerException("NullPointerException testing");
        }
        if (error.equals(DatastoreException.class.getSimpleName())) {
            throw new DatastoreException(Code.DEADLINE_EXCEEDED_VALUE, "DatastoreException testing",
                    Code.DEADLINE_EXCEEDED.name());
        }
        if (error.equals(InvalidHttpParameterException.class.getSimpleName())) {
            throw new InvalidHttpParameterException("InvalidHttpParameterException testing");
        }
        if (error.equals(EntityNotFoundException.class.getSimpleName())) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("EntityNotFoundException testing"));
        }
        return new JsonResult("Test output");
    }

}
