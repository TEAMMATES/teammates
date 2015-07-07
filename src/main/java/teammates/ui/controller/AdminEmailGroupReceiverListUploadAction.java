package teammates.ui.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
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
        
        if(blobInfo == null){
            data.isFileUploaded = false;
            data.fileSrcUrl = null;            
            
            log.info("Group Receiver List Upload Failed");
            statusToAdmin = "Group Receiver List Upload Failed";
            data.ajaxStatus = "Group receiver list upload failed. Please try again.";
            return createAjaxResult(data);
        }
        
        try {
            checkGroupReceiverListFile(blobInfo);
        } catch (IOException e) {
            data.isFileUploaded = false;
            data.fileSrcUrl = null;            
            
            log.info("Group Receiver List Upload Failed: uploaded file is corrupted");
            statusToAdmin = "Group Receiver List Upload Failed: uploaded file is corrupted";
            data.ajaxStatus = "Group receiver list upload failed: uploaded file is corrupted. "
                              + "Please make sure the txt file contains only email addresses "
                              + "separated by comma";
            deleteGroupReceiverListFile(blobInfo.getBlobKey());
            return createAjaxResult(data);
        }     
        
        blobKey = blobInfo.getBlobKey();     
        
        
        data.groupReceiverListFileKey = blobKey.getKeyString();
        
        data.isFileUploaded = true;
        statusToAdmin = "New Group Receiver List Uploaded";  
        data.ajaxStatus = "Group receiver list successfully uploaded to Google Cloud Storage";

        return createAjaxResult(data);
    }
    
    
    /**
     * This method: <br>
     * 1.goes through the just uploaded list file by splitting the content of the txt file(email addresses separated by comma)
     * into separated email addresses, which makes sure the file content is intact and of correct format
     * 2.if no error, log each email address into system. 
     * 
     * @param blobInfo
     * @throws IOException
     */
    private void checkGroupReceiverListFile(BlobInfo blobInfo) throws IOException{
        Assumption.assertNotNull(blobInfo);
        
        BlobKey blobKey = blobInfo.getBlobKey();
        
        //it turns out that error will occur if we read more than around 900000 bytes of data per time
        //from the blobstream, which also brings problems when this large number of emails are all stored in one
        //list. As a result, to prevent unexpected errors, we read the txt file several times and each time
        //at most 900000 bytes are read, after which a new list is created to store all the emails addresses that
        //happen to be in the newly read bytes. 
        
        //For email address which happens to be broken according to two consecutive reading, a check will be done
        //before storing all emails separated from the second reading into a new list. Broken email will be fixed by
        //deleting the first item of the email list from current reading  AND 
        //appending it to the last item of the email list from last reading
        
        //the email list from each reading is inserted into a upper list(list of list).
        //the structure is as below:
        
        //ListOfList: 
        //      ListFromReading_1 : 
        //                     [example@email.com]
        //                            ...
        //      ListFromReading_2 : 
        //                     [example@email.com]
        //                            ...
        
        //offset is needed for remembering where it stops from last reading
        int offset = 0;
        //file size is needed to track the number of unread bytes 
        int size = (int)blobInfo.getSize();    
        
        //this is the list of list
        List<List<String>> listOfList = new LinkedList<List<String>>();
   
        while(size > 0){
            //makes sure not to over-read
            int bytesToRead = size > MAX_READING_LENGTH ? MAX_READING_LENGTH : size;
            InputStream blobStream = new BlobstoreInputStream(blobKey, offset);
            byte[] array = new byte[bytesToRead];
            
            blobStream.read(array);
            
            //remember where it stops reading
            offset += MAX_READING_LENGTH;
            //decrease unread bytes
            size -= MAX_READING_LENGTH;
            
            //get the read bytes into string and split it by ","
            String readString = new String(array);
            List<String> newList = Arrays.asList(readString.split(","));
            
            
            if(listOfList.isEmpty()){
                //this is the first time reading
                listOfList.add(newList);
            } else {
                //check if the last reading stopped in the middle of a email address string
                List<String> lastAddedList = listOfList.get(listOfList.size() -1);
                //get the last item of the list from last reading
                String lastStringOfLastAddedList = lastAddedList.get(lastAddedList.size() - 1);
                //get the first item of the list from current reading
                String firstStringOfNewList = newList.get(0);
                
                if(!lastStringOfLastAddedList.contains("@")||
                   !firstStringOfNewList.contains("@")){
                   //either the left part or the right part of the broken email string 
                   //does not contains a "@".
                   //simply append the right part to the left part(last item of the list from last reading)
                   listOfList.get(listOfList.size() -1)
                             .set(lastAddedList.size() - 1,
                                  lastStringOfLastAddedList + 
                                  firstStringOfNewList);
                   
                   //and also needs to delete the right part which is the first item of the list from current reading
                   listOfList.add(newList.subList(1, newList.size() - 1));
                } else {
                   
                   //no broken email from last reading found, simply add the list
                   //from current reading into the upper list.
                   listOfList.add(newList);
                }              
            }
            
            blobStream.close();
        }
        
        //log all email addresses retrieved from the txt file 
        int i = 0;
        
        for(List<String> list : listOfList){
            for(String str : list){
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
