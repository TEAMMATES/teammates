package teammates.ui.controller;

import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.common.util.Const;

public class AdminEmailPageData extends PageData {

    public AdminEmailPageData(AccountAttributes account) {
        super(account);
    }
    
    public boolean isFileUploaded;
    public String fileSrcUrl;
    public String nextUploadUrl;
    public String ajaxStatus = null;
    
    public String getNewUploadUrl() throws EntityDoesNotExistException {     
        try {
            return generateNewUploadUrl();
        } catch(BlobstoreFailureException e) {
            return null;
        } 
    }

    private String generateNewUploadUrl() {
        UploadOptions uploadOptions = generateUploadOptions();
   
        String formPostUrl = BlobstoreServiceFactory
                             .getBlobstoreService()
                             .createUploadUrl(Const.ActionURIs.ADMIN_EMAIL_FILE_UPLOAD, uploadOptions);
        return formPostUrl;
    }
    
    private UploadOptions generateUploadOptions() {
        UploadOptions uploadOptions = UploadOptions.Builder.withDefaults()
                                      .googleStorageBucketName(Config.GCS_BUCKETNAME)
                                      .maxUploadSizeBytes(Const.SystemParams.MAX_PROFILE_PIC_LIMIT_FOR_BLOBSTOREAPI);
        return uploadOptions;
    }
}
