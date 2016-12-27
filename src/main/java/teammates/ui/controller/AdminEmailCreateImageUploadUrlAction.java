package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.logic.api.GateKeeper;

public class AdminEmailCreateImageUploadUrlAction extends CreateImageUploadUrlAction {

    @Override
    protected ActionResult execute() {
        return createAjaxResult(getCreateImageUploadUrlPageData());
    }

    protected void verifyPrivileges() {
        new GateKeeper().verifyAdminPrivileges(account);
    }

    protected String getUploadUrl() {
        return GoogleCloudStorageHelper.getNewUploadUrl(Const.ActionURIs.ADMIN_EMAIL_IMAGE_UPLOAD);
    }

}
