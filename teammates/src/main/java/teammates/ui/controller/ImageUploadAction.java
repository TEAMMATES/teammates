package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.ui.pagedata.FileUploadPageData;

/**
 * Action: uploads an image to Google Cloud Storage.
 */
public class ImageUploadAction extends Action {

    private static final Logger log = Logger.getLogger();

    private FileUploadPageData data;

    @Override
    protected ActionResult execute() {
        data = prepareData();

        return createAjaxResult(data);
    }

    protected String getImageKeyParam() {
        return Const.ParamsNames.IMAGE_TO_UPLOAD;
    }

    protected FileUploadPageData prepareData() {
        FileUploadPageData data = new FileUploadPageData(account, sessionToken);
        BlobInfo blobInfo = extractImageKey(getImageKeyParam());

        if (blobInfo == null) {
            data.isFileUploaded = false;
            data.fileSrcUrl = null;
            log.warning("Image Upload Failed");
            statusToAdmin = "Image Upload Failed";

            return data;
        }

        BlobKey blobKey = blobInfo.getBlobKey();

        data.isFileUploaded = true;
        AppUrl fileSrcUrl = Config.getAppUrl(Const.ActionURIs.PUBLIC_IMAGE_SERVE)
                .withParam(Const.ParamsNames.BLOB_KEY, blobKey.getKeyString());
        String absoluteFileSrcUrl = fileSrcUrl.toAbsoluteString();
        data.fileSrcUrl = fileSrcUrl.toString();

        log.info("New Image Uploaded : " + absoluteFileSrcUrl);
        statusToAdmin = "New Image Uploaded : " + "<a href=" + data.fileSrcUrl + " target=\"_blank\">"
                + absoluteFileSrcUrl + "</a>";
        data.ajaxStatus = "Image Successfully Uploaded to Google Cloud Storage";

        return data;
    }

    /**
     * Extracts the image metadata by the passed image key parameter.
     */
    protected BlobInfo extractImageKey(String param) {
        try {
            Map<String, List<BlobInfo>> blobsMap = BlobstoreServiceFactory.getBlobstoreService().getBlobInfos(request);
            List<BlobInfo> blobs = blobsMap.get(param);

            if (blobs == null || blobs.isEmpty()) {
                data.ajaxStatus = Const.StatusMessages.NO_IMAGE_GIVEN;
                isError = true;
                return null;
            }

            BlobInfo image = blobs.get(0);
            return validateImage(image);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * Validates the image by size and content type.
     */
    protected BlobInfo validateImage(BlobInfo image) {
        if (image.getSize() > Const.SystemParams.MAX_PROFILE_PIC_SIZE) {
            deleteImage(image.getBlobKey());
            isError = true;
            data.ajaxStatus = Const.StatusMessages.IMAGE_TOO_LARGE;
            return null;
        } else if (!image.getContentType().contains("image/")) {
            deleteImage(image.getBlobKey());
            isError = true;
            data.ajaxStatus = Const.StatusMessages.FILE_NOT_A_PICTURE;
            return null;
        }

        return image;
    }

    /**
     * Deletes the uploaded image.
     */
    protected void deleteImage(BlobKey blobKey) {
        if (blobKey.equals(new BlobKey(""))) {
            return;
        }

        try {
            deleteUploadedFile(blobKey);
        } catch (BlobstoreFailureException bfe) {
            statusToAdmin = Const.ACTION_RESULT_FAILURE
                    + " : Unable to delete picture (possible unused picture with key: "
                    + blobKey.getKeyString()
                    + " || Error Message: "
                    + bfe.getMessage() + System.lineSeparator();
        }
    }

    /**
     * Deletes the uploaded file from Google Cloud Storage.
     */
    protected void deleteUploadedFile(BlobKey blobKey) {
        logic.deleteUploadedFile(blobKey);
    }
}
