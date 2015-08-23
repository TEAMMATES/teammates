package teammates.ui.controller;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.CompositeTransform;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;
import com.google.appengine.api.images.Transform;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

/**
 * Action: edits the profile picture based on the coordinates of 
 *         the cropped photograph.
 */
public class StudentProfilePictureEditAction extends Action {

    private BlobKey _blobKey;
    private String _widthString;
    private String _heightString;
    private String _bottomYString;
    private String _rightXString;
    private String _topYString;
    private String _leftXString;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        new GateKeeper().verifyLoggedInUserPrivileges();
        readAllPostParamterValuesToFields();
        if (!validatePostParameters()) {
            return createRedirectResult(Const.ActionURIs.STUDENT_PROFILE_PAGE);
        }

        try {
            byte[] transformedImage = this.transformImage();
            if (!isError) {
                // this branch is covered in UiTests (look at todo in transformImage())
                uploadFileToGcs(transformedImage);
            }
        } catch (IOException e) {
            // Happens when GCS Service is down
            isError = true;
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_PIC_SERVICE_DOWN, StatusMessageColor.DANGER));
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " : Writing transformed image to file failed. Error: "
                          + e.getMessage();
        }

        return createRedirectResult(Const.ActionURIs.STUDENT_PROFILE_PAGE);
    }

    /**
     * Uploads the given image data to the cloud storage into a file with the
     * user's googleId as the name.
     * Returns a blobKey that can be used to identify the file.
     * 
     * @param fileName
     * @param transformedImage
     * @return BlobKey
     * @throws IOException
     * TODO: use the function 'writeDataToGcs' in GoogleCloudStorageHelper to achieve this 
     */
    private void uploadFileToGcs(byte[] transformedImage) throws IOException {
        GcsFilename fileName = new GcsFilename(Config.GCS_BUCKETNAME, account.googleId);

        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName,
                                                                    new GcsFileOptions.Builder()
                                                                                      .mimeType("image/png").build());

        outputChannel.write(ByteBuffer.wrap(transformedImage));
        outputChannel.close();
    }

    private byte[] transformImage() {
        Double height = Double.parseDouble(_heightString);
        Double width = Double.parseDouble(_widthString);
        Double leftX = Double.parseDouble(_leftXString) / width;
        Double topY = Double.parseDouble(_topYString) / height;
        Double rightX = Double.parseDouble(_rightXString) / width;
        Double bottomY = Double.parseDouble(_bottomYString) / height;

        try {
            /*
             * This branch is covered in UiTests as the following method does
             * not behave the same in dev as in staging.
             * TODO: find a way to cover it in Action Tests.
             */
            Image newImage = getTransformedImage(leftX, topY, rightX, bottomY);
            return newImage.getImageData();
        } catch (RuntimeException re) {
            isError = true;
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_PICTURE_EDIT_FAILED, StatusMessageColor.DANGER));
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " : Reading and transforming image failed."
                          + re.getMessage();
        }

        return null;
    }

    private Image getTransformedImage(Double leftX, Double topY, Double rightX, Double bottomY) {
        Assumption.assertNotNull(_blobKey);

        Image oldImage = ImagesServiceFactory.makeImageFromBlob(_blobKey);
        CompositeTransform finalTransform = getCompositeTransformToApply(leftX, topY, rightX, bottomY);
        OutputSettings settings = new OutputSettings(ImagesService.OutputEncoding.PNG);

        return ImagesServiceFactory.getImagesService().applyTransform(finalTransform, oldImage, settings);
    }

    private CompositeTransform getCompositeTransformToApply(Double leftX, Double topY, Double rightX,
                                                            Double bottomY) {
        Transform crop = ImagesServiceFactory.makeCrop(leftX, topY, rightX, bottomY);
        Transform resize = ImagesServiceFactory.makeResize(150, 150);
        CompositeTransform finalTransform = ImagesServiceFactory.makeCompositeTransform()
                                                                .concatenate(crop)
                                                                .concatenate(resize);
        return finalTransform;
    }

    /**
     * Checks that the information given via POST is valid
     */
    private boolean validatePostParameters() {
        if (_leftXString.isEmpty() || _topYString.isEmpty()
         || _rightXString.isEmpty() || _bottomYString.isEmpty()) {
            isError = true;
            statusToUser.add(new StatusMessage("Given crop locations were not valid. Please try again", StatusMessageColor.DANGER));
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " : One or more of the given coords were empty.";
            return false;
        } else if (_heightString.isEmpty() || _widthString.isEmpty()) {
            isError = true;
            statusToUser.add(new StatusMessage("Given crop locations were not valid. Please try again", StatusMessageColor.DANGER));
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " : One or both of the image dimensions were empty.";
            return false;
        } else if (Double.parseDouble(_widthString) == 0
                || Double.parseDouble(_heightString) == 0) {
            isError = true;
            statusToUser.add(new StatusMessage("Given crop locations were not valid. Please try again", StatusMessageColor.DANGER));
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " : One or both of the image dimensions were zero.";
            return false;
        }
        return true;
    }

    /**
     * Gets all the parameters from the Request and ensures that
     * they are not null
     */
    private void readAllPostParamterValuesToFields() {
        _leftXString = getLeftXString();
        _topYString = getTopYString();
        _rightXString = getRightXString();
        _bottomYString = getBottomYString();
        _heightString = getPictureHeight();
        _widthString = getPictureWidth();
        _blobKey = getBlobKey();
    }

    private BlobKey getBlobKey() {
        Assumption.assertPostParamNotNull(Const.ParamsNames.BLOB_KEY,
                                          getRequestParamValue(Const.ParamsNames.BLOB_KEY));
        return new BlobKey(getRequestParamValue(Const.ParamsNames.BLOB_KEY));
    }

    private String getPictureWidth() {
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_WIDTH,
                                          getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_WIDTH));
        return getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_WIDTH);
    }

    private String getPictureHeight() {
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_HEIGHT,
                                          getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_HEIGHT));
        return getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_HEIGHT);
    }

    private String getBottomYString() {
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_BOTTOMY,
                                          getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_BOTTOMY));
        return getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_BOTTOMY);
    }

    private String getRightXString() {
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_RIGHTX,
                                          getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_RIGHTX));
        return getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_RIGHTX);
    }

    private String getTopYString() {
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_TOPY, 
                                          getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_TOPY));
        return getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_TOPY);
    }

    private String getLeftXString() {
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_LEFTX, 
                                          getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_LEFTX));
        return getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_LEFTX);
    }

}
