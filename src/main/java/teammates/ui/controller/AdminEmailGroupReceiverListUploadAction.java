package teammates.ui.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Text;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminEmailGroupReceiverListUploadAction extends Action {
    
    static final int MAX_READING_LENGTH = 900000; 
    
    AdminEmailComposePageData data = null;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        GateKeeper.inst().verifyAdminPrivileges(account);
        
        BlobKey blobKey = new BlobKey("");
        BlobInfo blobInfo = null;
        
        data = new AdminEmailComposePageData(account);    
        blobInfo = extractGroupReceiverListFileKey();
        
        
        try {
            getReceiverList(blobInfo);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        if(blobInfo == null){
            data.isFileUploaded = false;
            data.fileSrcUrl = null;            
            
            log.info("Group Receiver List Upload Failed");
            statusToAdmin = "Group Receiver List Upload Failed";
            
            return createAjaxResult(Const.ViewURIs.ADMIN_EMAIL, data);
        }
        
        
        blobKey = blobInfo.getBlobKey();     
        
        
        data.groupReceiverListFileKey = blobKey.getKeyString();
        
        data.isFileUploaded = true;
        statusToAdmin = "New Group Receiver List Uploaded";
        
        data.ajaxStatus = "Group Receiver List Successfully Uploaded to Google Cloud Storage";

        return createAjaxResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }
    
    private void getReceiverList(BlobInfo blobInfo) throws IOException{
        Assumption.assertNotNull(blobInfo);
        
        BlobKey blobKey = blobInfo.getBlobKey();
        
        
        int offset = 0;
        int size = (int) blobInfo.getSize();
        
        List<List<String>> listOfList = new LinkedList<List<String>>();
        
        
        while(size > 0){
            int bytesToRead = size > this.MAX_READING_LENGTH ? this.MAX_READING_LENGTH : size;
            InputStream blobStream = new BlobstoreInputStream(blobKey, offset);
            byte[] array = new byte[bytesToRead];
            
            blobStream.read(array);
            offset += this.MAX_READING_LENGTH;
            size -= this.MAX_READING_LENGTH;
            
            String readString = new String(array);
            
            List<String> newList = Arrays.asList(readString.split(","));
            
            
            if(listOfList.isEmpty()){
                listOfList.add(newList);
                
            } else {
            
                List<String> lastAddedList = listOfList.get(listOfList.size() -1);
                String lastStringOfLastAddedList = lastAddedList.get(lastAddedList.size() - 1);
                String firstStringOfNewList = newList.get(0);
                
                if(!lastStringOfLastAddedList.contains("@")||
                   !firstStringOfNewList.contains("@")){
                    
                   listOfList.get(listOfList.size() -1)
                             .set(lastAddedList.size() - 1,
                                  lastStringOfLastAddedList + 
                                  firstStringOfNewList);
                   
                   listOfList.add(newList.subList(1, newList.size()));
                } else {
                
                   listOfList.add(newList);
                }              
            }
        }
        
        int i = 0;
        
        for(List<String> list : listOfList){
            for(String str : list){
                System.out.print(str + "      " + i + " \n");
                log.info(str + "      " + i + " \n");

                i ++;
            }
        }
    }
    
    
    private BlobInfo extractGroupReceiverListFileKey() {
        try {
            Map<String, List<BlobInfo>> blobsMap = BlobstoreServiceFactory.getBlobstoreService().getBlobInfos(request);
            List<BlobInfo> blobs = blobsMap.get(Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_TO_UPLOAD);
            
            if(blobs != null && blobs.size() > 0) {
                BlobInfo groupReceiverListFile = blobs.get(0);
                return validateGroupReceiverListFile(groupReceiverListFile);
            } else{
                data.ajaxStatus = Const.StatusMessages.NO_GROUP_RECEIVER_LIST_FILE_GIVEN;
                isError = true;
                return null;
            }
        } catch (IllegalStateException e) {
            return null;
        }
    }

    private BlobInfo validateGroupReceiverListFile (BlobInfo groupReceiverListFile) {
//        if (groupReceiverListFile.getSize() > Const.SystemParams.MAX_ADMIN_EMAIL_FILE_LIMIT_FOR_BLOBSTOREAPI) {
//            deleteGroupReceiverListFile(groupReceiverListFile.getBlobKey());
//            isError = true;
//            data.ajaxStatus = Const.StatusMessages.RECEIVER_LIST_FILE_TOO_LARGE;
//            return null;
//        } else 
            
            if(!groupReceiverListFile.getContentType().contains("text/")) {
            deleteGroupReceiverListFile(groupReceiverListFile.getBlobKey());
            isError = true;
            data.ajaxStatus = (Const.StatusMessages.NOT_A_RECEIVER_LIST_FILE);
            return null;
        } else {
            return groupReceiverListFile;
        }
        
    }
    
    private void deleteGroupReceiverListFile(BlobKey blobKey) {
        if (blobKey == new BlobKey("")) return;
        
        try {
            logic.deleteAdminEmailUploadedFile(blobKey);
        } catch (BlobstoreFailureException bfe) {
            statusToAdmin = Const.ACTION_RESULT_FAILURE 
                    + " : Unable to delete group receiver list file (possible unused file with key: "
                    + blobKey.getKeyString()
                    + " || Error Message: "
                    + bfe.getMessage() + Const.EOL;
        }
    }
    
}
