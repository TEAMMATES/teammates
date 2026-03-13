package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Puts searchable documents from the data bundle into the DB.
 */
public class PutDataBundleDocumentsAction extends Action {

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
            sqlLogic.putDocuments(dataBundle);
        } catch (SearchServiceException e) {
            return new JsonResult("Failed to add data bundle documents.", HttpStatus.SC_BAD_GATEWAY);
        }
        return new JsonResult("Data bundle documents successfully added.");
    }
}
