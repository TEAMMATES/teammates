package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;

/**
 * Puts searchable documents from the data bundle into the DB.
 */
class PutDataBundleDocumentsAction extends Action {

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
        try {
            logic.putDocuments(dataBundle);
        } catch (SearchServiceException e) {
            return new JsonResult("Failed to add data bundle documents.", HttpStatus.SC_BAD_GATEWAY);
        }
        return new JsonResult("Data bundle documents successfully added.");
    }

}
