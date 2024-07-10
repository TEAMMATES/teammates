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

        AccountRequest accRequest = sqlLogic.getAccountRequest(accountRequestId);

        try {
            sqlLogic.putAccountRequestDocument(accRequest);
        } catch (SearchServiceException e) {
            // Set an arbitrary retry code outside of the range 200-299 to trigger automatic retry
            return new JsonResult("Failure", HttpStatus.SC_BAD_GATEWAY);
        }

        return new JsonResult("Successful");
    }
}
