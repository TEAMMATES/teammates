package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.ui.pagedata.AdminEmailComposePageData;
import teammates.ui.pagedata.FileUploadPageData;

import com.google.appengine.api.blobstore.BlobKey;

/**
 * Action: uploads an image for admin email.
 */
public class AdminEmailImageUploadAction extends ImageUploadAction {

    @Override
    protected ActionResult execute() {
        gateKeeper.verifyAdminPrivileges(account);

        FileUploadPageData uploadPageData = prepareData();
        AdminEmailComposePageData data = new AdminEmailComposePageData(account);
        data.isFileUploaded = uploadPageData.isFileUploaded;
        data.fileSrcUrl = uploadPageData.fileSrcUrl;
        data.ajaxStatus = uploadPageData.ajaxStatus;

        return createAjaxResult(data);
    }

    @Override
    protected String getImageKeyParam() {
        return Const.ParamsNames.ADMIN_EMAIL_IMAGE_TO_UPLOAD;
    }

    @Override
    protected void deleteUploadedFile(BlobKey blobKey) {
        logic.deleteAdminEmailUploadedFile(blobKey);
    }
}
