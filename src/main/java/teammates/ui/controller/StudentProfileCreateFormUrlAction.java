package teammates.ui.controller;

import com.google.appengine.api.blobstore.BlobstoreFailureException;

import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.Logger;
import teammates.common.util.Url;
import teammates.ui.pagedata.StudentProfileCreateFormUrlAjaxPageData;

/**
 * Action: generates the UploadUrl for pictures given by students.
 *         A dynamic generation is done to circumvent the 10 minute
 *         time limit for such URLs
 */
public class StudentProfileCreateFormUrlAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() {
        StudentProfileCreateFormUrlAjaxPageData data =
                new StudentProfileCreateFormUrlAjaxPageData(account, sessionToken, getUploadUrl(), isError);
        return createAjaxResult(data);
    }

    private String getUploadUrl() {
        String callbackUrl = Url.addParamToUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE_UPLOAD,
                Const.ParamsNames.SESSION_TOKEN, sessionToken);
        try {
            String uploadUrl = GoogleCloudStorageHelper.getNewUploadUrl(callbackUrl);
            statusToAdmin = "Created Url successfully: " + uploadUrl;
            return uploadUrl;
        } catch (BlobstoreFailureException e) {
            isError = true;
            statusToAdmin = "Failed to create profile picture upload-url: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            // This branch is not tested as this error can and should never occur
            isError = true;
            log.severe(callbackUrl + " was found to be illegal success path. Error: " + e.getMessage());
            statusToAdmin = "Failed to create profile picture upload-url: " + e.getMessage();
        }
        return "";
    }

}
