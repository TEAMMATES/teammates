package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.blobstore.BlobKey;

public class AdminEmailImageUploadAction extends ImageUploadAction {

    private AdminEmailComposePageData data;

    @Override
    protected ActionResult execute() {
        FileUploadPageData uploadPageData = prepareData();
        data = new AdminEmailComposePageData(account, uploadPageData);

        return createAjaxResult(data);
    }

    @Override
    protected void verifyPrivileges() {
        GateKeeper.inst().verifyAdminPrivileges(account);
    }

    @Override
    protected String getImageKeyParam() {
        return Const.ParamsNames.ADMIN_EMAIL_IMAGE_TO_UPLOAD;
    }

    protected void deleteUploadedFile(BlobKey blobKey) {
        logic.deleteAdminEmailUploadedFile(blobKey);
    }
}
