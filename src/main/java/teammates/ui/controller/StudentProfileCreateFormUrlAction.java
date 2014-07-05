package teammates.ui.controller;

import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.Const;

public class StudentProfileCreateFormUrlAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        if(isUnregistered) {
            throw new UnauthorizedAccessException("User is not registered");
        }
        
        UploadOptions uploadOptions = UploadOptions.Builder.withDefaults()
                .googleStorageBucketName(Config.GCS_BUCKETNAME)
                .maxUploadSizeBytes(Const.SystemParams.MAX_PROFILE_PIC_LIMIT_FOR_BLOBSTOREAPI);
        String formPostUrl = BlobstoreServiceFactory.getBlobstoreService()
                .createUploadUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE_UPLOAD, uploadOptions);
        
        StudentProfileCreateFormUrlAjaxPageData data = new StudentProfileCreateFormUrlAjaxPageData(account, formPostUrl); 
        return createAjaxResult("", data);
    }

}
