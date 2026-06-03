package teammates.ui.webapi;

import teammates.common.datatransfer.DataBundleDeletionIds;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.InvalidHttpRequestBodyException;

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
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        DataBundleDeletionIds dataBundleDeletionIds = JsonUtils.fromJson(getRequestBody(), DataBundleDeletionIds.class);

        try {
            logic.removeDataBundle(dataBundleDeletionIds);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }
        return new JsonResult("Data bundle successfully persisted.");
    }

}
