package teammates.ui.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

public class AdminEmailFileUploadAction extends Action {
    
    AdminEmailPageData data = null;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        GateKeeper.inst().verifyAdminPrivileges(account);
       
        String imageKey = "";
        BlobKey blobKey = new BlobKey("");
        BlobInfo blobInfo = null;
        
        data = new AdminEmailPageData(account);
        
        blobInfo = extractImageKey();
        
        if(blobInfo == null){
            data.isFileUploaded = false;
            data.fileSrcUrl = null;            
            log.info("Image Upload Failed");
            statusToAdmin = "Image Upload Failed";
            
            data.nextUploadUrl = data.getNewUploadUrl();
            while(data.nextUploadUrl == null){
                data.nextUploadUrl = data.getNewUploadUrl();
            }
            
            return createAjaxResult(Const.ViewURIs.ADMIN_EMAIL, data);
        }
        
        
        blobKey = blobInfo.getBlobKey();

        try {
            imageKey = renameFileToAdminImages(blobInfo);
        } catch (IOException e) {
            deleteImage(blobKey);
            data.isFileUploaded = false;
            data.fileSrcUrl = null;            
            log.info("Image Upload to Google Cloud Storage Failed");
            statusToAdmin = "Image Upload to Google Cloud Storage Failed";      
            data.ajaxStatus = "Image Upload to Google Cloud Storage Failed";
            
            data.nextUploadUrl = data.getNewUploadUrl();
            while(data.nextUploadUrl == null){
                data.nextUploadUrl = data.getNewUploadUrl();
            }
            
            return createAjaxResult(Const.ViewURIs.ADMIN_EMAIL, data);
        }          
        
      
        data.isFileUploaded = true;
        data.fileSrcUrl = Config.APP_URL + 
                          Const.ActionURIs.PUBLIC_EMAIL_FILE_SERVE +
                          "?blob-key=" + 
                          imageKey;
        
        log.info("New File Uploaded : " + data.fileSrcUrl);
        statusToAdmin = "New File Uploaded : " + "<a href=" +
                        data.fileSrcUrl + " target=blank>" +
                        data.fileSrcUrl + "</a>";
        data.ajaxStatus = "Image Successfully Uploaded to Google Cloud Storage";
        
        data.nextUploadUrl = data.getNewUploadUrl();
        while(data.nextUploadUrl == null){
            data.nextUploadUrl = data.getNewUploadUrl();
        }
        
        return createAjaxResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }
    
    
    private String renameFileToAdminImages(BlobInfo blobInfo) throws IOException {
        Assumption.assertNotNull(blobInfo);
        
        BlobKey blobKey = blobInfo.getBlobKey();
        InputStream blobStream = new BlobstoreInputStream(blobKey);
        byte[] imageData = new byte[(int) blobInfo.getSize()];
        blobStream.read(imageData);
        blobStream.close();
        
        String newKey = uploadFileToGcs(imageData);
        deleteImage(blobKey);
        return newKey;
    }
    
    /**
     * Uploads the given image data to the cloud storage into a file
     * with the uploaded date as the name. Returns a blobKey that can be used to 
     * identify the file
     * 
     * @param fileName
     * @param transformedImage
     * @return BlobKey
     * @throws IOException
     * TODO: use the function 'writeDataToGcs' in GoogleCloudStorageHelper to achieve this 
     */
    private String uploadFileToGcs(byte[] transformedImage)
            throws IOException {
        
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(Const.SystemParams.ADMIN_TIME_ZONE));
        cal = TimeHelper.convertToUserTimeZone(cal, Const.SystemParams.ADMIN_TIMZE_ZONE_DOUBLE);
        
        
        GcsFilename fileName = new GcsFilename(Config.GCS_BUCKETNAME, cal.getTime().toString());
        
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        GcsOutputChannel outputChannel =
                gcsService.createOrReplace(fileName, new GcsFileOptions.Builder().mimeType("image/png").build());
        
        outputChannel.write(ByteBuffer.wrap(transformedImage));
        outputChannel.close();
        
        return BlobstoreServiceFactory.getBlobstoreService()
                .createGsBlobKey("/gs/"+Config.GCS_BUCKETNAME + "/" + cal.getTime().toString()).getKeyString();
    }

    private BlobInfo extractImageKey() {
        try {
            Map<String, List<BlobInfo>> blobsMap = BlobstoreServiceFactory.getBlobstoreService().getBlobInfos(request);
            List<BlobInfo> blobs = blobsMap.get(Const.ParamsNames.ADMIN_EMAIL_IMAGE_TO_UPLOAD);
            
            if(blobs != null && blobs.size() > 0) {
                BlobInfo image = blobs.get(0);
                return validateImage(image);
            } else{
                data.ajaxStatus = Const.StatusMessages.NO_IMAGE_GIVEN;
                isError = true;
                return null;
            }
        } catch (IllegalStateException e) {
            return null;
        }
    }

    private BlobInfo validateImage (BlobInfo image) {
        if (image.getSize() > Const.SystemParams.MAX_PROFILE_PIC_SIZE) {
            deleteImage(image.getBlobKey());
            isError = true;
            data.ajaxStatus = Const.StatusMessages.IMAGE_TOO_LARGE;
            return null;
        } else if(!image.getContentType().contains("image/")) {
            deleteImage(image.getBlobKey());
            isError = true;
            data.ajaxStatus = (Const.StatusMessages.FILE_NOT_A_PICTURE);
            return null;
        } else {
            return image;
        }
        
    }
    
    private void deleteImage(BlobKey blobKey) {
        if (blobKey == new BlobKey("")) return;
        
        try {
            logic.deletePicture(blobKey);
        } catch (BlobstoreFailureException bfe) {
            statusToAdmin = Const.ACTION_RESULT_FAILURE 
                    + " : Unable to delete picture (possible unused picture with key: "
                    + blobKey.getKeyString()
                    + " || Error Message: "
                    + bfe.getMessage() + Const.EOL;
        }
    }
    
}
