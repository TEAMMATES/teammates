package teammates.ui.controller;

import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.common.util.Const;

/**
 * Action: generates the UploadUrl for pictures given by students.
 *         A dynamic generation is done to circumvent the 10 minute 
 *         time limit for such URLs
 */
public class StudentProfileCreateFormUrlAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        StudentProfileCreateFormUrlAjaxPageData data =
                new StudentProfileCreateFormUrlAjaxPageData(account, getUploadUrl(), isError);
        return createAjaxResult(data);
    }

    private UploadOptions generateUploadOptions() {
        UploadOptions uploadOptions =
                UploadOptions.Builder.withDefaults().googleStorageBucketName(Config.GCS_BUCKETNAME)
                                     .maxUploadSizeBytes(Const.SystemParams.MAX_PROFILE_PIC_LIMIT_FOR_BLOBSTOREAPI);
        return uploadOptions;
    }

    private String getUploadUrl() {
        try {
            return generateNewUploadUrl();
        } catch (BlobstoreFailureException e) {
            isError = true;
            statusToAdmin = "Failed to create profile picture upload-url: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            // This branch is not tested as this error can and should never occur
            isError = true;
            log.severe(Const.ActionURIs.STUDENT_PROFILE_PICTURE_UPLOAD
                       + " was found to be illegal success path. Error: " + e.getMessage());
            statusToAdmin = "Failed to create profile picture upload-url: " + e.getMessage();
        }
        return "";
    }

    private String generateNewUploadUrl() {
        UploadOptions uploadOptions = generateUploadOptions();
        String formPostUrl = BlobstoreServiceFactory.getBlobstoreService()
                                                    .createUploadUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE_UPLOAD,
                                                                     uploadOptions);
        statusToAdmin = "Created Url successfully: " + formPostUrl;
        return formPostUrl;
    }

}
