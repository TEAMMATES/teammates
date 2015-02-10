package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class AdminEmailFileUploadAction extends Action {
    
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        GateKeeper.inst().verifyAdminPrivileges(account);
       
        AdminEmailPageData data = new AdminEmailPageData(account);
        
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
        List<BlobKey> blobKeys = blobs.get("adminEmailFile");

        if (blobKeys == null || blobKeys.isEmpty()) {
            //res.sendRedirect("/");
            data.isFileUploaded = false;
            data.fileSrcUrl = null;
        } else {
            //res.sendRedirect("/serve?blob-key=" + blobKeys.get(0).getKeyString());
            data.isFileUploaded = true;
            data.fileSrcUrl = Config.APP_URL + 
                              Const.ActionURIs.ADMIN_EMAIL_FILE_SERVE +
                              "?blob-key=" + 
                              blobKeys.get(0).getKeyString();
        }
        System.out.print(data.fileSrcUrl);
        
        data.nextUploadUrl = blobstoreService.createUploadUrl(Const.ActionURIs.ADMIN_EMAIL_FILE_UPLOAD);
        this.statusToUser.add("Image Uploaded");
        return createAjaxResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }

}
