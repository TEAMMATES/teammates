package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;

/**
 * Action: creates a URL for uploading an image in admin email.
 */
public class AdminEmailCreateImageUploadUrlAction extends CreateImageUploadUrlAction {

    @Override
    protected ActionResult execute() {
        verifyPrivileges();
        return createAjaxResult(getCreateImageUploadUrlPageData());
    }

    private void verifyPrivileges() {
        gateKeeper.verifyAdminPrivileges(account);
    }

    @Override
    protected String getUploadUrl() {
        return GoogleCloudStorageHelper.getNewUploadUrl(Const.ActionURIs.ADMIN_EMAIL_IMAGE_UPLOAD);
    }

}
