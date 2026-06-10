package teammates.ui.webapi;

import teammates.ui.output.ConfigData;

/**
 * Gets the application configuration.
 */
public class GetConfigAction extends PublicAction {

    @Override
    public JsonResult execute() {
        return new JsonResult(new ConfigData());
    }

}
