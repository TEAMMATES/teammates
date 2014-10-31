package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.NullPostParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

import com.google.apphosting.api.DeadlineExceededException;

public class AdminExceptionTestAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        new GateKeeper().verifyAdminPrivileges(account);

        String error = getRequestParamValue(Const.ParamsNames.ERROR);

        if (error.equals(AssertionError.class.getSimpleName())) {
            throw new AssertionError("AssertionError Testing");

        } else if (error.equals(EntityDoesNotExistException.class.getSimpleName())) {
            throw new EntityDoesNotExistException("EntityDoesNotExistException Testing");

        } else if (error.equals(UnauthorizedAccessException.class.getSimpleName())) {
            throw new UnauthorizedAccessException();

        } else if (error.equals(NullPointerException.class.getSimpleName())) {
            throw new NullPointerException();
            
        } else if (error.equals(DeadlineExceededException.class.getSimpleName())) {
            throw new DeadlineExceededException();
        } else if (error.equals(NullPostParameterException.class.getSimpleName())) {
            throw new NullPostParameterException("test null post param exception");
        }

        statusToAdmin = "adminExceptionTest";
                
        return createRedirectResult(Const.ActionURIs.ADMIN_HOME_PAGE);
    }

}
