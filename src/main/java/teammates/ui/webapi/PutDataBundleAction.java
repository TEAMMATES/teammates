package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;

/**
 * Persists a data bundle into the DB.
 */
class PutDataBundleAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.ALL_ACCESS;
    }

    @Override
    void checkSpecificAccessControl() {
        if (!Config.isDevServer()) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    JsonResult execute() {
        DataBundle dataBundle = JsonUtils.fromJson(getRequestBody(), DataBundle.class);

        try {
            dataBundle = logic.persistDataBundle(dataBundle);
        } catch (InvalidParametersException e) {
            return new JsonResult("Error when persisting data bundle: " + e.getMessage(),
                    HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        return new JsonResult(JsonUtils.toJson(dataBundle));
    }
}
