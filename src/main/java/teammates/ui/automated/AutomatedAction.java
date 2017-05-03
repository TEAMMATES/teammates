package teammates.ui.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.api.EmailSender;
import teammates.logic.api.Logic;
import teammates.logic.api.TaskQueuer;

/**
 * An automated "action" to be performed by the system, triggered by cron jobs or task queues.
 * <p>
 * This class of action is different from the non-automated ones in the following manner:
 * <ul>
 *     <li>Non-administrators are barred from performing it.</li>
 *     <li>The limit for request is 10 minutes instead of 1 minute.</li>
 * </ul>
 * </p>
 */
public abstract class AutomatedAction {

    protected Logic logic;
    protected TaskQueuer taskQueuer;
    protected EmailSender emailSender;

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    protected void initialiseAttributes(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.logic = new Logic();
        setTaskQueuer(new TaskQueuer());
        setEmailSender(new EmailSender());
    }

    public TaskQueuer getTaskQueuer() {
        return taskQueuer;
    }

    public void setTaskQueuer(TaskQueuer taskQueuer) {
        this.taskQueuer = taskQueuer;
    }

    public EmailSender getEmailSender() {
        return emailSender;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    protected String getRequestParamValue(String paramName) {
        return HttpRequestHelper.getValueFromRequestParameterMap(request, paramName);
    }

    /**
     * Returns the value for the specified parameter expected to be present in the http request.
     * Assumption: the requested parameter is not null.
     *
     * @param paramName  a constant from the {@link teammates.common.util.Const.ParamsNames} class.
     */
    protected String getNonNullRequestParamValue(String paramName) {
        return getNonNullRequestParamValues(paramName)[0];
    }

    protected String[] getRequestParamValues(String paramName) {
        return HttpRequestHelper.getValuesFromRequestParameterMap(request, paramName);
    }

    /**
     * Returns the values for the specified parameter expected to be present in the http request.
     * Assumption: the requested parameter is not null.
     *
     * @param paramName  a constant from the {@link teammates.common.util.Const.ParamsNames} class.
     */
    protected String[] getNonNullRequestParamValues(String paramName) {
        String[] values = getRequestParamValues(paramName);
        Assumption.assertPostParamNotNull(paramName, values);
        return values;
    }

    protected void setForRetry() {
        // Sets an arbitrary retry code outside of the range 200-299 so GAE will automatically retry upon failure
        response.setStatus(100);
    }

    protected abstract String getActionDescription();

    protected abstract String getActionMessage();

    /** Executes the action. */
    public abstract void execute();

}
