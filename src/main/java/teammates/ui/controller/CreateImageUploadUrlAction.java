package teammates.ui.controller;

import com.google.appengine.api.blobstore.BlobstoreFailureException;

import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.ui.pagedata.CreateImageUploadUrlAjaxPageData;

/**
 * Action: creates a URL for uploading an image.
 */
public class CreateImageUploadUrlAction extends Action {

    @Override
    protected ActionResult execute() {
        return createAjaxResult(getCreateImageUploadUrlPageData());
    }

    protected final CreateImageUploadUrlAjaxPageData getCreateImageUploadUrlPageData() {
        CreateImageUploadUrlAjaxPageData data = new CreateImageUploadUrlAjaxPageData(account);

        try {
            data.nextUploadUrl = getUploadUrl();
            data.ajaxStatus = "Image upload url created, proceed to uploading";
        } catch (BlobstoreFailureException | IllegalArgumentException e) {
            data.nextUploadUrl = null;
            isError = true;
            data.ajaxStatus = "An error occurred when creating upload URL, please try again";
        }

        return data;
    }

    protected String getUploadUrl() {
        return GoogleCloudStorageHelper.getNewUploadUrl(Const.ActionURIs.IMAGE_UPLOAD);
    }

}
