package teammates.ui.newcontroller;

import java.io.IOException;

import org.apache.http.HttpStatus;

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

/**
 * Action: edits the profile picture based on the coordinates of
 *         the cropped photo.
 */
public class PutStudentProfilePictureAction extends Action {

    private BlobKey blobKey;
    private String widthString;
    private String heightString;
    private String bottomYString;
    private String rightXString;
    private String topYString;
    private String leftXString;
    private String rotateString;

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Anyone logged in can edit a student's profile picture
    }

    @Override
    public ActionResult execute() {
        readAllPostParameterValuesToFields();
        try {
            validatePostParameters();
            byte[] transformedImage = this.transformImage();
            // this branch is covered in UiTests (look at todo in transformImage())
            GoogleCloudStorageHelper.writeImageDataToGcs(userInfo.id, transformedImage);
            return new JsonResult("Profile picture successfully edited.", HttpStatus.SC_OK);
        } catch (IllegalArgumentException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        } catch (RuntimeException re) {
            return new JsonResult(Const.StatusMessages.STUDENT_PROFILE_PICTURE_EDIT_FAILED, HttpStatus.SC_BAD_REQUEST);
        } catch (IOException io) {
            // Happens when GCS Service is down
            return new JsonResult(Const.StatusMessages.STUDENT_PROFILE_PIC_SERVICE_DOWN, HttpStatus.SC_BAD_REQUEST);
        }
    }

    private byte[] transformImage() {
        /*
         * This branch is covered in UiTests as the following method does
         * not behave the same in dev as in staging.
         * TODO: find a way to cover it in Action Tests.
         */
        Image newImage = getTransformedImage();
        return newImage.getImageData();
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
    private void validatePostParameters() throws IllegalArgumentException {
        if (leftXString.isEmpty() || topYString.isEmpty()
                || rightXString.isEmpty() || bottomYString.isEmpty()
                || heightString.isEmpty() || widthString.isEmpty()
                || Double.parseDouble(widthString) == 0
                || Double.parseDouble(heightString) == 0) {
            throw new IllegalArgumentException("Given crop locations were not valid. Please try again");
        }
    }

    /**
     * Gets all the parameters from the Request and ensures that they are not null.
     */
    private void readAllPostParameterValuesToFields() {
        leftXString = getNonNullRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_LEFTX);
        topYString = getNonNullRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_TOPY);
        rightXString = getNonNullRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_RIGHTX);
        bottomYString = getNonNullRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_BOTTOMY);
        heightString = getNonNullRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_HEIGHT);
        widthString = getNonNullRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_WIDTH);
        blobKey = new BlobKey(getNonNullRequestParamValue(Const.ParamsNames.BLOB_KEY));
        rotateString = getNonNullRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_ROTATE);
    }
}
