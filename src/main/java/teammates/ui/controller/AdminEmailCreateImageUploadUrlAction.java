package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.blobstore.BlobstoreFailureException;

public class AdminEmailCreateImageUploadUrlAction extends Action {

    @Override
    protected ActionResult execute() {
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        AdminEmailCreateImageUploadUrlAjaxPageData data = new AdminEmailCreateImageUploadUrlAjaxPageData(account);
        
        try {
            data.nextUploadUrl = GoogleCloudStorageHelper.getNewUploadUrl(Const.ActionURIs.ADMIN_EMAIL_IMAGE_UPLOAD);
            data.ajaxStatus = "Image upload url created, proceed to uploading";
        } catch (BlobstoreFailureException | IllegalArgumentException e) {
            data.nextUploadUrl = null;
            isError = true;
            data.ajaxStatus = "An error occurred when creating upload URL, please try again";
        }
          
        return createAjaxResult(data);
        
    }

}
