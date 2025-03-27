package teammates.ui.webapi;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Persists a data bundle into the DB.
 */
public class PutDataBundleAction extends Action {

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
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        DataBundle dataBundle = JsonUtils.fromJson(getRequestBody(), DataBundle.class);

        try {
            dataBundle = logic.persistDataBundle(dataBundle);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        return new JsonResult(JsonUtils.toJson(dataBundle));
    }
}
