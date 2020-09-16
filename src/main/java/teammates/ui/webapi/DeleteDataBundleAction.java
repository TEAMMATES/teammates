package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;

/**
 * Deletes a data bundle from the DB.
 */
public class DeleteDataBundleAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.ALL_ACCESS;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!Config.isDevServer()) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public JsonResult execute() {
        DataBundle dataBundle = JsonUtils.fromJson(getRequestBody(), DataBundle.class);
        logic.removeDataBundle(dataBundle);
        return new JsonResult("Data bundle successfully persisted.", HttpStatus.SC_OK);
    }

}
