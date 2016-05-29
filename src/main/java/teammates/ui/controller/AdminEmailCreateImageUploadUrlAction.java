package teammates.ui.controller;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;

public class AdminEmailCreateImageUploadUrlAction extends Action {

    @Override
    protected ActionResult execute() {
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        AdminEmailCreateImageUploadUrlAjaxPageData data = new AdminEmailCreateImageUploadUrlAjaxPageData(account);
        
        data.nextUploadUrl = getNewUploadUrl();
        
        if (data.nextUploadUrl == null) {
            data.nextUploadUrl = getNewUploadUrl();
        }
        
        //re-try creating upload url
        if (data.nextUploadUrl == null) {
            isError = true;
            data.ajaxStatus = "An error occurred when creating upload URL, please try again";
        } else {
            isError = false;
            data.ajaxStatus = "Image upload url created, proceed to uploading";
        }
          
        return createAjaxResult(data);
        
    }

    public String getNewUploadUrl() {
        try {
            return generateNewUploadUrl();
        } catch (BlobstoreFailureException e) {
            return null;
        }
    }

    private String generateNewUploadUrl() {
        UploadOptions uploadOptions = generateUploadOptions();
   
        String formPostUrl = BlobstoreServiceFactory
                             .getBlobstoreService()
                             .createUploadUrl(Const.ActionURIs.ADMIN_EMAIL_IMAGE_UPLOAD, uploadOptions);
        return formPostUrl;
    }
    
    private UploadOptions generateUploadOptions() {
        UploadOptions uploadOptions = UploadOptions.Builder
                                      .withGoogleStorageBucketName(Config.GCS_BUCKETNAME)
                                      .maxUploadSizeBytes(Const.SystemParams.MAX_PROFILE_PIC_LIMIT_FOR_BLOBSTOREAPI);
        return uploadOptions;
    }

}
