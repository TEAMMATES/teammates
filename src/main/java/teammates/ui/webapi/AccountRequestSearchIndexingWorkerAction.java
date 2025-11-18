package teammates.ui.webapi;

import java.util.UUID;

import org.apache.http.HttpStatus;

import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const.ParamsNames;
import teammates.storage.sqlentity.AccountRequest;

/**
 * Task queue worker action: performs account request search indexing.
 */
public class AccountRequestSearchIndexingWorkerAction extends AdminOnlyAction {

    @Override
    public ActionResult execute() {
        String id = getNonNullRequestParamValue(ParamsNames.ACCOUNT_REQUEST_ID);
        UUID accountRequestId;

        try {
            accountRequestId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(e.getMessage(), e);
        }

        AccountRequest accRequest = monitorDatabaseOperation(
            "getAccountRequest", () -> sqlLogic.getAccountRequest(accountRequestId)
        );

        try {
            monitorDatabaseOperationVoid(
                "putAccountRequestDocument", () -> {
                    try {
                        sqlLogic.putAccountRequestDocument(accRequest);
                    } catch (SearchServiceException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SearchServiceException) {
                return new JsonResult("Failure", HttpStatus.SC_BAD_GATEWAY);
            }
            throw e;
        }

        return new JsonResult("Successful");
    }
}
