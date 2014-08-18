package teammates.ui.controller;

import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.common.util.Const;

/**
 * Action: generates the UploadUrl for pictures given by students.
 *         A dynamic generation is done to avoid the 10 minute time 
 *         limit to such URLs
 */
public class StudentProfileCreateFormUrlAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        boolean isError = false;
        String formPostUrl = "";
        UploadOptions uploadOptions = UploadOptions.Builder.withDefaults()
                .googleStorageBucketName(Config.GCS_BUCKETNAME)
                .maxUploadSizeBytes(Const.SystemParams.MAX_PROFILE_PIC_LIMIT_FOR_BLOBSTOREAPI);
        
        try {
            formPostUrl = BlobstoreServiceFactory.getBlobstoreService()
                    .createUploadUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE_UPLOAD, uploadOptions);
            statusToAdmin = "Created Url successfully: " + formPostUrl;
        } catch(BlobstoreFailureException e) {
            // This branch is not tested as this error is difficult 
            // to reproduce in the dev server
            isError = true;
            statusToAdmin = "Failed to create profile picture upload-url: " + e.getMessage();
        } catch(IllegalArgumentException e) {
            // This branch is not tested as this error should never occur
            // and cannot be reproduced in normal circumstances
            log.severe(Const.ActionURIs.STUDENT_PROFILE_PICTURE_UPLOAD 
                    + " was found to be illegal success path. Error: "
                    + e.getMessage());
            statusToAdmin = "Failed to create profile picture upload-url: " + e.getMessage();
        }
        
        StudentProfileCreateFormUrlAjaxPageData data = new StudentProfileCreateFormUrlAjaxPageData(account, formPostUrl); 
        data.isError = isError;
        return createAjaxResult("", data);
    }

}
