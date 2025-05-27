package teammates.ui.webapi;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Deletes a data bundle from the DB.
 */
public class DeleteSqlDataBundleAction extends Action {

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
        SqlDataBundle dataBundle = JsonUtils.fromJson(getRequestBody(), SqlDataBundle.class);

        try {
            sqlLogic.removeDataBundle(dataBundle);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }
        return new JsonResult("Data bundle successfully persisted.");
    }

}
