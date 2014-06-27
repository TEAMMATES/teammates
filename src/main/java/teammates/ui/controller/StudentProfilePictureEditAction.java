package teammates.ui.controller;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
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

public class StudentProfilePictureEditAction extends Action {

    private GcsService gcsService;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        validatePostParameters();
        
        String leftXString = getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_LEFTX);
        String topYString = getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_TOPY);
        String rightXString = getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_RIGHTX);
        String bottomYString = getRequestParamValue(Const.ParamsNames.PROFILE_PICTURE_BOTTOMY);
        String height = getRequestParamValue("pictureheight");
        String width = getRequestParamValue("picturewidth");
        BlobKey blobKey = new BlobKey(getRequestParamValue(Const.ParamsNames.BLOB_KEY));
        
        if (leftXString.isEmpty() || topYString.isEmpty()
                || rightXString.isEmpty() || bottomYString.isEmpty()
                || height.isEmpty() || width.isEmpty()) {
            isError=true;
            statusToUser.add("Given crop locations were not valid. Please try again");            
            statusToAdmin = Const.ACTION_RESULT_FAILURE + 
                    " : One or more of the given coords were empty.";
        }
        
        
        GcsFilename fileName = new GcsFilename(Config.GCS_BUCKETNAME, account.googleId);
        gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        try {
            byte[] transformedImage = this.transformImage(leftXString, topYString, rightXString, 
                    bottomYString, height, width, blobKey);
            
            GcsOutputChannel outputChannel =
                    gcsService.createOrReplace(fileName, GcsFileOptions.getDefaultInstance());
            
            outputChannel.write(ByteBuffer.wrap(transformedImage));
            outputChannel.close();
            
            BlobKey newPictureKey = BlobstoreServiceFactory.getBlobstoreService()
                    .createGsBlobKey("/gs/"+Config.GCS_BUCKETNAME + "/" + account.googleId);
            logic.updateStudentProfilePicture(account.googleId, newPictureKey.getKeyString());
        } catch (IOException e) {
            isError=true;
            statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_PIC_SERVICE_DOWN);            
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " : Writing transformed image to file failed.";
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
        
        oldImage = ImagesServiceFactory.makeImageFromBlob(blobKey);
        
        Transform resize = ImagesServiceFactory.makeCrop(leftX, topY, rightX, bottomY);
        OutputSettings settings = new OutputSettings(ImagesService.OutputEncoding.PNG);
        //settings.setQuality(100);
        
        Image newImage = imagesService.applyTransform(resize, oldImage, settings);

        return  newImage.getImageData();
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
        Assumption.assertPostParamNotNull("picturewidth", 
                getRequestParamValue("picturewidth"));
        Assumption.assertPostParamNotNull("pictureheight", 
                getRequestParamValue("pictureheight"));
    }

}
