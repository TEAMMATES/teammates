package teammates.ui.webapi;

import com.google.cloud.datastore.DatastoreException;
import com.google.rpc.Code;

import teammates.common.exception.DeadlineExceededException;
import teammates.common.util.Config;
import teammates.common.util.Const;

/**
 * Action specifically created for testing exception handling at API servlet.
 */
public class AdminExceptionTestAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!Config.IS_DEV_SERVER) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
        String error = getNonNullRequestParamValue(Const.ParamsNames.ERROR);
        if (error.equals(UnauthorizedAccessException.class.getSimpleName())) {
            throw new UnauthorizedAccessException("UnauthorizedAccessException testing");
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException") // deliberately done for testing
    public JsonResult execute() {
        String error = getNonNullRequestParamValue(Const.ParamsNames.ERROR);
        if (error.equals(AssertionError.class.getSimpleName())) {
            assert false : "AssertionError testing";
        }
        if (error.equals(NullPointerException.class.getSimpleName())) {
            throw new NullPointerException("NullPointerException testing");
        }
        if (error.equals(DeadlineExceededException.class.getSimpleName())) {
            throw new DeadlineExceededException();
        }
        if (error.equals(DatastoreException.class.getSimpleName())) {
            throw new DatastoreException(Code.DEADLINE_EXCEEDED_VALUE, "DatastoreException testing",
                    Code.DEADLINE_EXCEEDED.name());
        }
        if (error.equals(InvalidHttpParameterException.class.getSimpleName())) {
            throw new InvalidHttpParameterException("InvalidHttpParameterException testing");
        }
        if (error.equals(EntityNotFoundException.class.getSimpleName())) {
            throw new EntityNotFoundException("EntityNotFoundException testing");
        }
        return new JsonResult("Test output");
    }

}
