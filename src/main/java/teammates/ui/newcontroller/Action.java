package teammates.ui.newcontroller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.UserType;
import teammates.common.util.Config;
import teammates.logic.api.GateKeeper;

/**
 * An "action" to be performed by the system.
 * If the requesting user is allowed to perform the requested action,
 * this object can talk to the back end to perform that action.
 */
public abstract class Action {

    protected HttpServletRequest req;
    private AuthType authType;

    /**
     * Initializes the action object based on the HTTP request.
     */
    public void init(HttpServletRequest req) {
        this.req = req;
        initAuthInfo(req);
    }

    /**
     * Returns true if the requesting user has sufficient authority to access the resource.
     */
    public boolean checkAccessControl() {
        if (authType.getLevel() < getMinAuthLevel().getLevel()) {
            // Access control level lower than required
            return false;
        }

        if (getMinAuthLevel() == AuthType.UNAUTHENTICATED) {
            // No authentication necessary for this resource
            return true;
        }

        if (authType == AuthType.ALL_ACCESS) {
            // All-access pass granted
            return true;
        }

        // All other cases: to be dealt in case-by-case basis
        return checkSpecificAccessControl();
    }

    private void initAuthInfo(HttpServletRequest req) {
        if (Config.BACKDOOR_KEY.equals(req.getParameter("backdoorkey"))) {
            authType = AuthType.ALL_ACCESS;
            return;
        }

        UserType userType = new GateKeeper().getCurrentUser();
        if (userType != null) {
            authType = AuthType.REGISTERED;
            return;
        }

        String regkey = req.getParameter("regkey");
        authType = regkey == null ? AuthType.UNAUTHENTICATED : AuthType.UNREGISTERED;
    }

    /**
     * Returns the log message in the special format used for generating the 'activity log' for the Admin.
     */
    public String getLogMessage() {
        // TODO
        return "Test log message";
    }

    /**
     * Gets the minimum access control level required to access the resource.
     */
    protected abstract AuthType getMinAuthLevel();

    /**
     * Checks the specific access control needs for the resource.
     */
    protected abstract boolean checkSpecificAccessControl();

    /**
     * Executes the action.
     */
    protected abstract ActionResult execute(HttpServletResponse resp);

}
