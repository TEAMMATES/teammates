package teammates.ui.webapi;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Persists a data bundle into the DB.
 */
public class PutSqlDataBundleAction extends Action {

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
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        SqlDataBundle dataBundle = JsonUtils.fromJson(getRequestBody(), SqlDataBundle.class);

        try {
            dataBundle = sqlLogic.persistDataBundle(dataBundle);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException("Some entities in the databundle already exist", e);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult(JsonUtils.toJson(dataBundle));
    }
}
