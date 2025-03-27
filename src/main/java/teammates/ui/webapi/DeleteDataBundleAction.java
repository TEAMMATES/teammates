package teammates.ui.webapi;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;

/**
 * Deletes a data bundle from the DB.
 */
public class DeleteDataBundleAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.ALL_ACCESS;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!Config.IS_DEV_SERVER) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public JsonResult execute() {
        DataBundle dataBundle = JsonUtils.fromJson(getRequestBody(), DataBundle.class);
        logic.removeDataBundle(dataBundle);
        return new JsonResult("Data bundle successfully persisted.");
    }

}
