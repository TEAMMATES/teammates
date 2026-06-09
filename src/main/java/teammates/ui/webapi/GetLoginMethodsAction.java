package teammates.ui.webapi;

import teammates.common.util.Config;
import teammates.ui.output.LoginMethodsData;

/**
 * Gets the login methods supported by the application.
 */
public class GetLoginMethodsAction extends PublicAction {

    @Override
    public JsonResult execute() {
        return new JsonResult(new LoginMethodsData(Config.getSupportedLoginMethods()));
    }

}
