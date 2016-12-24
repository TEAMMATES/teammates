package teammates.ui.controller;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;

public class AdminEmailImageUploadAction extends ImageUploadAction {
    
    AdminEmailComposePageData data;
    
    @Override
    protected ActionResult execute() {
        GateKeeper.inst().verifyAdminPrivileges(account);

        data = new AdminEmailComposePageData(account);
        BlobInfo blobInfo = extractImageKey(Const.ParamsNames.ADMIN_EMAIL_IMAGE_TO_UPLOAD);

        if (blobInfo == null) {
            data.isFileUploaded = false;
            data.fileSrcUrl = null;
            log.warning("Image Upload Failed");
            statusToAdmin = "Image Upload Failed";

            return createAjaxResult(data);
        }

        BlobKey blobKey = blobInfo.getBlobKey();

        data.isFileUploaded = true;
        data.fileSrcUrl = Const.ActionURIs.PUBLIC_EMAIL_FILE_SERVE + "?blob-key=" + blobKey.getKeyString();
        String absoluteFileSrcUrl = Config.getAppUrl(data.fileSrcUrl).toAbsoluteString();

        log.info("New Image Uploaded : " + absoluteFileSrcUrl);
        statusToAdmin = "New Image Uploaded : " + "<a href="
                + data.fileSrcUrl + " target=blank>" + absoluteFileSrcUrl + "</a>";
        data.ajaxStatus = "Image Successfully Uploaded to Google Cloud Storage";

        return createAjaxResult(data);
    }
    
    protected void deleteUploadedFile(BlobKey blobKey) {
        logic.deleteAdminEmailUploadedFile(blobKey);
    }
}
