package teammates.ui.controller;

import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.NullPostParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

public class AdminExceptionTestAction extends Action {

    /**
     * This method throws an exception and redirects the user to an error page
     * depending on the type of error in the user request.
     */
    @Override
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException") // deliberately done for testing
    protected ActionResult execute() throws EntityDoesNotExistException {

        gateKeeper.verifyAdminPrivileges(account);

        String error = getRequestParamValue(Const.ParamsNames.ERROR);

        if (error.equals(AssertionError.class.getSimpleName())) {
            throw new AssertionError("AssertionError Testing");
        } else if (error.equals(EntityDoesNotExistException.class.getSimpleName())) {
            throw new EntityDoesNotExistException("EntityDoesNotExistException Testing");
        } else if (error.equals(UnauthorizedAccessException.class.getSimpleName())) {
            throw new UnauthorizedAccessException("UnauthorizedAccessException Testing");
        } else if (error.equals(NullPointerException.class.getSimpleName())) {
            throw new NullPointerException("NullPointerException Testing");
        } else if (error.equals(DeadlineExceededException.class.getSimpleName())) {
            throw new DeadlineExceededException("DeadlineExceededException Testing");
        } else if (error.equals(NullPostParameterException.class.getSimpleName())) {
            throw new NullPostParameterException("NullPostParameterException Testing");
        }

        statusToAdmin = "adminExceptionTest";
        return createRedirectResult(Const.ActionURIs.ADMIN_HOME_PAGE);
    }

}
