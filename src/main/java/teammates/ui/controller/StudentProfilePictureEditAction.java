package teammates.ui.controller;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
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
import teammates.logic.api.GateKeeper;

/**
 * Action: edits the profile picture based on the coordinates of 
 *         the cropped photograph.
 */
public class StudentProfilePictureEditAction extends Action {

    private GcsService gcsService;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        new GateKeeper().verifyLoggedInUserPrivileges();

        validatePostParameters();
        
        String leftXString = getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_LEFTX);
        String topYString = getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_TOPY);
        String rightXString = getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_RIGHTX);
        String bottomYString = getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_BOTTOMY);
        String height = getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_HEIGHT);
        String width = getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_WIDTH);
        BlobKey blobKey = new BlobKey(getRequestParamValue(Const.ParamsNames.BLOB_KEY));
        
        if (leftXString.isEmpty() || topYString.isEmpty()
                || rightXString.isEmpty() || bottomYString.isEmpty()
                || height.isEmpty() || width.isEmpty() 
                || Double.parseDouble(width) == 0 || Double.parseDouble(height) == 0) {
            isError=true;
            statusToUser.add("Given crop locations were not valid. Please try again");            
            statusToAdmin = Const.ACTION_RESULT_FAILURE + 
                    " : One or more of the given coords were empty.";
            return createRedirectResult(Const.ActionURIs.STUDENT_PROFILE_PAGE);
        }
        
        GcsFilename fileName = new GcsFilename(Config.GCS_BUCKETNAME, account.googleId);
        gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        try {
            byte[] transformedImage = this.transformImage(leftXString, topYString, rightXString, 
                    bottomYString, height, width, blobKey);
            
            String newPictureKey = BlobstoreServiceFactory.getBlobstoreService()
                    .createGsBlobKey("/gs/"+Config.GCS_BUCKETNAME + "/" + account.googleId).getKeyString();
            logic.updateStudentProfilePicture(account.googleId, newPictureKey);
            
            if (!isError) {
                GcsOutputChannel outputChannel =
                        gcsService.createOrReplace(fileName, new GcsFileOptions.Builder().mimeType("image/png").build());
                
                outputChannel.write(ByteBuffer.wrap(transformedImage));
                outputChannel.close();
            }
        } catch (IOException e) {
            // this branch is difficult to reproduce during testing 
            // and hence is not covered
            // TODO: find a way to cover this branch
            isError=true;
            statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_PIC_SERVICE_DOWN);            
            statusToAdmin = Const.ACTION_RESULT_FAILURE 
                    + " : Writing transformed image to file failed. Error: "
                    + e.getMessage();
        }
        
        return createRedirectResult(Const.ActionURIs.STUDENT_PROFILE_PAGE);
    }
    
    private byte[] transformImage(String leftXString, String topYString, String rightXString, 
            String bottomYString, String heightString, String widthString, BlobKey blobKey) {
        
        Double height = Double.parseDouble(heightString);
        Double width = Double.parseDouble(widthString);
        Double leftX = Double.parseDouble(leftXString)/width;
        Double topY = Double.parseDouble(topYString)/height;
        Double rightX = Double.parseDouble(rightXString)/width;
        Double bottomY = Double.parseDouble(bottomYString)/height;
        
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        Image oldImage;
        
        try {
            oldImage = ImagesServiceFactory.makeImageFromBlob(blobKey);
            
            Transform crop = ImagesServiceFactory.makeCrop(leftX, topY, rightX, bottomY);
            Transform resize = ImagesServiceFactory.makeResize(150, 150);
            
            CompositeTransform finalTransform = ImagesServiceFactory
                    .makeCompositeTransform()
                    .concatenate(crop)
                    .concatenate(resize);
            
            OutputSettings settings = new OutputSettings(ImagesService.OutputEncoding.PNG);        
            Image newImage = imagesService.applyTransform(finalTransform, oldImage, settings);

            return  newImage.getImageData();
        } catch (RuntimeException re) {
            isError=true;
            statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_PICTURE_EDIT_FAILED);            
            statusToAdmin = Const.ACTION_RESULT_FAILURE
                    + " : Reading and transforming image failed."
                    + re.getMessage();
        }
        
        return null;
    }

    private void validatePostParameters() {
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_LEFTX, 
                getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_LEFTX));
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_TOPY, 
                getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_TOPY));
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_RIGHTX, 
                getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_RIGHTX));
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_BOTTOMY, 
                getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_BOTTOMY));
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_WIDTH, 
                getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_WIDTH));
        Assumption.assertPostParamNotNull(Const.ParamsNames.PROFILE_PICTURE_HEIGHT, 
                getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_HEIGHT));
        Assumption.assertPostParamNotNull(Const.ParamsNames.BLOB_KEY,
                getRequestParamValue(Const.ParamsNames.BLOB_KEY));
    }

}
