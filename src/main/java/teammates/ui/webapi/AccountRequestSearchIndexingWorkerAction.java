package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const.ParamsNames;

/**
 * Task queue worker action: performs account request search indexing.
 */
public class AccountRequestSearchIndexingWorkerAction extends AdminOnlyAction {

    @Override
    public ActionResult execute() {
        String email = getNonNullRequestParamValue(ParamsNames.INSTRUCTOR_EMAIL);
        String institute = getNonNullRequestParamValue(ParamsNames.INSTRUCTOR_INSTITUTE);

        AccountRequestAttributes accountRequest = logic.getAccountRequest(email, institute);

        try {
            logic.putAccountRequestDocument(accountRequest);
        } catch (SearchServiceException e) {
            // Set an arbitrary retry code outside of the range 200-299 to trigger automatic retry
            return new JsonResult("Failure", HttpStatus.SC_BAD_GATEWAY);
        }

        return new JsonResult("Successful");
    }
}
