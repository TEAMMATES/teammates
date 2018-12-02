package teammates.ui.automated;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;

/**
 * Action specifically created for testing exception handling at API servlet.
 */
public class AutomatedActionExceptionTestAction extends AutomatedAction {

    @Override
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException") // deliberately done for testing
    public void execute() {
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
    }

}
