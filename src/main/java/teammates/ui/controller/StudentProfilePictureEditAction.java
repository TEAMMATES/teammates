package teammates.ui.controller;

import java.io.IOException;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.CompositeTransform;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;
import com.google.appengine.api.images.Transform;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

/**
 * Action: edits the profile picture based on the coordinates of
 *         the cropped photograph.
 */
public class StudentProfilePictureEditAction extends Action {

    private BlobKey blobKey;
    private String widthString;
    private String heightString;
    private String bottomYString;
    private String rightXString;
    private String topYString;
    private String leftXString;
    private String rotateString;

    @Override
    protected ActionResult execute() {
        gateKeeper.verifyLoggedInUserPrivileges();
        readAllPostParameterValuesToFields();
        if (!validatePostParameters()) {
            return createRedirectResult(Const.ActionURIs.STUDENT_PROFILE_PAGE);
        }

        try {
            byte[] transformedImage = this.transformImage();
            if (!isError) {
                // this branch is covered in UiTests (look at todo in transformImage())
                GoogleCloudStorageHelper.writeImageDataToGcs(account.googleId, transformedImage);
            }
        } catch (IOException e) {
            // Happens when GCS Service is down
            isError = true;
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_PIC_SERVICE_DOWN,
                                               StatusMessageColor.DANGER));
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " : Writing transformed image to file failed. Error: "
                          + e.getMessage();
        }

        return createRedirectResult(Const.ActionURIs.STUDENT_PROFILE_PAGE);
    }

    private byte[] transformImage() {
        try {
            /*
             * This branch is covered in UiTests as the following method does
             * not behave the same in dev as in staging.
             * TODO: find a way to cover it in Action Tests.
             */
            Image newImage = getTransformedImage();
            return newImage.getImageData();
        } catch (RuntimeException re) {
            isError = true;
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_PICTURE_EDIT_FAILED,
                                               StatusMessageColor.DANGER));
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " : Reading and transforming image failed."
                          + re.getMessage();
        }

        return new byte[0];
    }

    private Image getTransformedImage() {
        Assumption.assertNotNull(blobKey);

        Image oldImage = ImagesServiceFactory.makeImageFromBlob(blobKey);
        CompositeTransform finalTransform = getCompositeTransformToApply();
        OutputSettings settings = new OutputSettings(ImagesService.OutputEncoding.PNG);

        return ImagesServiceFactory.getImagesService().applyTransform(finalTransform, oldImage, settings);
    }

    private Transform getScaleTransform() {
        Double width = Double.parseDouble(widthString);
        Double height = Double.parseDouble(heightString);
        return ImagesServiceFactory.makeResize((int) Math.round(width), (int) Math.round(height));
    }

    private Transform getRotateTransform() {
        Double rotate = Double.parseDouble(rotateString);
        return ImagesServiceFactory.makeRotate((int) Math.round(rotate));
    }

    private Transform getCropTransform() {
        Double height = Double.parseDouble(heightString);
        Double width = Double.parseDouble(widthString);
        Double leftX = Double.parseDouble(leftXString) / width;
        Double topY = Double.parseDouble(topYString) / height;
        Double rightX = Double.parseDouble(rightXString) / width;
        Double bottomY = Double.parseDouble(bottomYString) / height;
        return ImagesServiceFactory.makeCrop(leftX, topY, rightX, bottomY);
    }

    private CompositeTransform getCompositeTransformToApply() {
        Transform standardCompress = ImagesServiceFactory.makeResize(150, 150);
        return ImagesServiceFactory.makeCompositeTransform()
                .concatenate(getScaleTransform())
                .concatenate(getRotateTransform())
                .concatenate(getCropTransform())
                .concatenate(standardCompress);
    }

    /**
     * Checks that the information given via POST is valid.
     */
    private boolean validatePostParameters() {
        if (leftXString.isEmpty() || topYString.isEmpty()
                || rightXString.isEmpty() || bottomYString.isEmpty()) {
            isError = true;
            statusToUser.add(new StatusMessage("Given crop locations were not valid. Please try again",
                                               StatusMessageColor.DANGER));
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " : One or more of the given coords were empty.";
            return false;
        } else if (heightString.isEmpty() || widthString.isEmpty()) {
            isError = true;
            statusToUser.add(new StatusMessage("Given crop locations were not valid. Please try again",
                                               StatusMessageColor.DANGER));
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " : One or both of the image dimensions were empty.";
            return false;
        } else if (Double.parseDouble(widthString) == 0
                || Double.parseDouble(heightString) == 0) {
            isError = true;
            statusToUser.add(new StatusMessage("Given crop locations were not valid. Please try again",
                                               StatusMessageColor.DANGER));
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " : One or both of the image dimensions were zero.";
            return false;
        }
        return true;
    }

    /**
     * Gets all the parameters from the Request and ensures that they are not null.
     */
    private void readAllPostParameterValuesToFields() {
        leftXString = getLeftXString();
        topYString = getTopYString();
        rightXString = getRightXString();
        bottomYString = getBottomYString();
        heightString = getPictureHeight();
        widthString = getPictureWidth();
        blobKey = getBlobKey();
        rotateString = getRotateString();
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

    private String getRotateString() {
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_ROTATE,
                                          getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_ROTATE));
        return getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_ROTATE);
    }
}
