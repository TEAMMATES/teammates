package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;


public class AdminEmailImageUploadAction extends Action {
    
    AdminEmailComposePageData data = null;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        GateKeeper.inst().verifyAdminPrivileges(account);
       
        BlobKey blobKey = new BlobKey("");
        BlobInfo blobInfo = null;
        
        data = new AdminEmailComposePageData(account);    
        blobInfo = extractImageKey();
        
        if(blobInfo == null){
            data.isFileUploaded = false;
            data.fileSrcUrl = null;            
            log.info("Image Upload Failed");
            statusToAdmin = "Image Upload Failed";
            
            return createAjaxResult(data);
        }
        
        
        blobKey = blobInfo.getBlobKey();     
        
      
        data.isFileUploaded = true;
        data.fileSrcUrl = Config.APP_URL + 
                          Const.ActionURIs.PUBLIC_EMAIL_FILE_SERVE +
                          "?blob-key=" + 
                          blobKey.getKeyString();
        
        log.info("New Image Uploaded : " + data.fileSrcUrl);
        statusToAdmin = "New Image Uploaded : " + "<a href=" +
                        data.fileSrcUrl + " target=blank>" +
                        data.fileSrcUrl + "</a>";
        data.ajaxStatus = "Image Successfully Uploaded to Google Cloud Storage";

        return createAjaxResult(data);
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
            logic.deleteAdminEmailUploadedFile(blobKey);
        } catch (BlobstoreFailureException bfe) {
            statusToAdmin = Const.ACTION_RESULT_FAILURE 
                    + " : Unable to delete picture (possible unused picture with key: "
                    + blobKey.getKeyString()
                    + " || Error Message: "
                    + bfe.getMessage() + Const.EOL;
        }
    }
    
}
