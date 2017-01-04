package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.logic.api.GateKeeper;

/**
 * Action: creates a URL for uploading an image in email
 */
public class AdminEmailCreateImageUploadUrlAction extends CreateImageUploadUrlAction {

    @Override
    protected ActionResult execute() {
        return createAjaxResult(getCreateImageUploadUrlPageData());
    }

    @Override
    protected void verifyPrivileges() {
        new GateKeeper().verifyAdminPrivileges(account);
    }

    @Override
    protected String getUploadUrl() {
        return GoogleCloudStorageHelper.getNewUploadUrl(Const.ActionURIs.ADMIN_EMAIL_IMAGE_UPLOAD);
    }

}
