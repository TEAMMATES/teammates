package teammates.ui.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.common.util.FieldValidator.FieldType;
import teammates.logic.api.GateKeeper;
import teammates.logic.core.TaskQueuesLogic;

public class AdminEmailComposeSendAction extends Action {
    
    private List<String> addressReceiver = new ArrayList<String>();
    private List<String> groupReceiver = new ArrayList<String>();
    
    private final int MAX_READING_LENGTH = 900000; 
    
    private boolean addressModeOn = false;
    private boolean groupModeOn = false;
    
    //params needed to move heavy jobs into a address mode task
    private String addressReceiverListString = null;
    
    //params needed to move heavy jobs into a group mode task
    private String groupReceiverListFileKey = null;
    private String emailId = null;
    
    @Override
    protected ActionResult execute() {
        
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailComposePageData data = new AdminEmailComposePageData(account);
        
        String emailContent = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_CONTENT);
        String subject = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_SUBJECT);
        
        addressReceiverListString = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEVIERS);
        addressModeOn = addressReceiverListString != null && !addressReceiverListString.isEmpty();
        
        emailId = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ID);
        
        groupReceiverListFileKey = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);    
        
        groupModeOn = (groupReceiverListFileKey != null) && !groupReceiverListFileKey.isEmpty();
        
        
        
        if(groupModeOn){     
            try {
                groupReceiver.add(groupReceiverListFileKey);
                checkGroupReceiverListFile(groupReceiverListFileKey);
            } catch (Exception e) {
                isError = true;
                setStatusForException(e, "An error occurred when retrieving receiver list, please try again");
            }     
        }
        
        if(addressModeOn){
            addressReceiver.add(addressReceiverListString);          
            try {
                checkAddressReceiverString(addressReceiverListString);
            } catch (InvalidParametersException e) {
                isError = true;
                setStatusForException(e);
            }  
        } 
        
        if(!addressModeOn && !groupModeOn){
            isError = true;
            statusToAdmin = "Error : No reciver address or file given";
            statusToUser.add("Error : No reciver address or file given");       
        }
        
        if(isError){
            data.emailToEdit = new AdminEmailAttributes(subject,
                                                        addressReceiver,
                                                        groupReceiver,
                                                        new Text(emailContent),
                                                        null);
            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
        }
        
        boolean isEmailDraft = emailId != null && !emailId.isEmpty();
        

        if(!isEmailDraft) {
            recordNewSentEmail(subject, addressReceiver, groupReceiver, emailContent);
        } else {
            updateDraftEmailToSent(emailId, subject, addressReceiver, groupReceiver, emailContent);
        }
 

        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }
    
    private void checkAddressReceiverString(String addressReceiverString) throws InvalidParametersException{
       FieldValidator validator = new FieldValidator();
       
       String[] emails = addressReceiverString.split(",");
       for(String email : emails){
           String error = validator.getInvalidityInfo(FieldType.EMAIL, email);
           if(error != null && !error.isEmpty()){
               isError = true;
               statusToUser.add(error);
               throw new InvalidParametersException("<strong>Email Format Error</strong>");
           }
       }
       
    }
    
    private void checkGroupReceiverListFile(String listFileKey) throws IOException {
        Assumption.assertNotNull(listFileKey);

        BlobKey blobKey = new BlobKey(listFileKey);
        
       
        int offset = 0;
        int size = (int) getFileSize(listFileKey);
        
        List<List<String>> listOfList = new LinkedList<List<String>>();
        
        
        while(size > 0){
            int bytesToRead = size > MAX_READING_LENGTH ? MAX_READING_LENGTH : size;
            InputStream blobStream = new BlobstoreInputStream(blobKey, offset);
            byte[] array = new byte[bytesToRead];
            
            blobStream.read(array);
            offset += MAX_READING_LENGTH;
            size -= MAX_READING_LENGTH;
            
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
                   
                   listOfList.add(newList.subList(1, newList.size() - 1));
                } else {
                
                   listOfList.add(newList);
                }              
            }
        }
        
        
    }
    
    private long getFileSize(String blobkeyString){
        BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
        BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(new BlobKey(blobkeyString));
        long blobSize = blobInfo.getSize();
        return blobSize;
    }
    
    private void moveJobToGroupModeTaskQueue(){
        if(!groupModeOn){
            return;
        }
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();     
        
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
        paramMap.put(ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY, groupReceiverListFileKey);
        paramMap.put(ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_LIST_INDEX, "0");
        paramMap.put(ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_INDEX, "0");
        paramMap.put(ParamsNames.ADMIN_EMAIL_TASK_QUEUE_MODE, Const.ADMIN_EMAIL_TASK_QUEUE_GROUP_MODE);
        
        taskQueueLogic.createAndAddTask(SystemParams.ADMIN_PREPARE_EMAIL_TASK_QUEUE,
                Const.ActionURIs.ADMIN_EMAIL_PREPARE_TASK_QUEUE_WORKER, paramMap); 
                

        
    }
    
    private void moveJobToAddressModeTaskQueue(){
        
        if(!addressModeOn){
            return;
        }
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();     
        
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
        paramMap.put(ParamsNames.ADMIN_EMAIL_TASK_QUEUE_MODE, Const.ADMIN_EMAIL_TASK_QUEUE_ADDRESS_MODE);
        paramMap.put(ParamsNames.ADMIN_EMAIL_ADDRESS_RECEVIERS, addressReceiverListString);
        
        taskQueueLogic.createAndAddTask(SystemParams.ADMIN_PREPARE_EMAIL_TASK_QUEUE,
                Const.ActionURIs.ADMIN_EMAIL_PREPARE_TASK_QUEUE_WORKER, paramMap); 
               
        
    }
    
    
    private void recordNewSentEmail(String subject,
                                    List<String> addressReceiver,
                                    List<String> groupReceiver,
                                    String content) {

        AdminEmailAttributes newDraft = new AdminEmailAttributes(subject,
                                                                 addressReceiver,
                                                                 groupReceiver,
                                                                 new Text(content),
                                                                 new Date());
        try {
            Date createDate = logic.createAdminEmail(newDraft);
            emailId = logic.getAdminEmail(subject, createDate).getEmailId();       
        } catch (Exception e) {
            deleteGroupReceiverFiles(groupReceiver);
            isError = true;
            setStatusForException(e, e.getMessage());
            return;
        }
        
        moveJobToGroupModeTaskQueue();
        moveJobToAddressModeTaskQueue();
    }
    
    
    private void updateDraftEmailToSent(String emailId,
                                        String subject,
                                        List<String> addressReceiver,
                                        List<String> groupReceiver,
                                        String content){
        
        AdminEmailAttributes fanalisedEmail = new AdminEmailAttributes(subject,
                                            addressReceiver,
                                            groupReceiver,
                                            new Text(content),
                                            new Date());
        
        try {
            logic.updateAdminEmailById(fanalisedEmail, emailId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            deleteGroupReceiverFiles(groupReceiver);
            setStatusForException(e);
            return;
        }     
        moveJobToGroupModeTaskQueue();
        moveJobToAddressModeTaskQueue();
        
    }
    
    private void deleteGroupReceiverFiles(List<String> groupReceiver){
        for(String key : groupReceiver){
            BlobKey blobKey = new BlobKey(key);
            logic.deleteAdminEmailUploadedFile(blobKey);
        }
    }

}
