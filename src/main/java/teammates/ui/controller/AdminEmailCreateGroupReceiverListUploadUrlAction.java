package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.blobstore.BlobstoreFailureException;

public class AdminEmailCreateGroupReceiverListUploadUrlAction extends Action {

    @Override
    protected ActionResult execute() {
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData data =
                new AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData(account);
        
        try {
            data.nextUploadUrl =
                    GoogleCloudStorageHelper.getNewUploadUrl(Const.ActionURIs.ADMIN_EMAIL_GROUP_RECEIVER_LIST_UPLOAD);
            data.ajaxStatus = "Group receiver list upload url created, proceed to uploading";
        } catch (BlobstoreFailureException | IllegalArgumentException e) {
            data.nextUploadUrl = null;
            isError = true;
            data.ajaxStatus = "An error occurred when creating upload URL, please try again";
        }
          
        return createAjaxResult(data);
        
    }

}
