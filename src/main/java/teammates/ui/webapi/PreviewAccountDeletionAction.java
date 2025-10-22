package teammates.ui.webapi;

import teammates.common.datatransfer.DeletionPreviewData;
import teammates.common.util.Const;
import teammates.logic.core.DeletionPreviewService;
import teammates.ui.output.DeletionPreviewOutput;

/**
 * Preview the deletion of an account to show what will be affected.
 */
public class PreviewAccountDeletionAction extends Action {

    private final DeletionPreviewService deletionPreviewService = DeletionPreviewService.inst();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.ACCOUNT_ID);

        DeletionPreviewData previewData = deletionPreviewService.previewAccountDeletion(googleId);

        return new JsonResult(new DeletionPreviewOutput(previewData));
    }
}
