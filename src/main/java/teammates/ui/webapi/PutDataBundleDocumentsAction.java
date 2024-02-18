package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.DataBundle;
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
        String type = getNonNullRequestParamValue("databundletype");

        switch (type) {
        case "sql":
            return putSqlDataBundleDocuments();
        case "datastore":
            return putDataBundleDocuments();
        default:
            throw new InvalidHttpParameterException("Error: invalid data bundle type");
        }
    }

    private JsonResult putSqlDataBundleDocuments() throws InvalidHttpRequestBodyException {
        SqlDataBundle sqlDataBundle = JsonUtils.fromJson(getRequestBody(), SqlDataBundle.class);

        try {
            sqlLogic.putDocuments(sqlDataBundle);
        } catch (SearchServiceException e) {
            return new JsonResult("Failed to add data bundle documents.", HttpStatus.SC_BAD_GATEWAY);
        }
        return new JsonResult("Data bundle documents successfully added.");
    }

    private JsonResult putDataBundleDocuments() throws InvalidHttpRequestBodyException {
        DataBundle dataBundle = JsonUtils.fromJson(getRequestBody(), DataBundle.class);

        try {
            logic.putDocuments(dataBundle);
        } catch (SearchServiceException e) {
            return new JsonResult("Failed to add data bundle documents.", HttpStatus.SC_BAD_GATEWAY);
        }
        return new JsonResult("Data bundle documents successfully added.");
    }
}
