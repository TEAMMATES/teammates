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
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.StatusMessageColor;
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
            statusToUser.add(new StatusMessage("Error : No reciver address or file given", StatusMessageColor.DANGER));       
        }
        
        if(isError){
            data.emailToEdit = new AdminEmailAttributes(subject,
                                                        addressReceiver,
                                                        groupReceiver,
                                                        new Text(emailContent),
                                                        null);
            data.emailToEdit.emailId = emailId;
            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
        }
        
        
        boolean isEmailDraft = emailId != null && !emailId.isEmpty();
        
        if(!isEmailDraft) {
            recordNewSentEmail(subject, addressReceiver, groupReceiver, emailContent);
        } else {
            updateDraftEmailToSent(emailId, subject, addressReceiver, groupReceiver, emailContent);
        }
 
        if(isError){
            data.emailToEdit = new AdminEmailAttributes(subject,
                                                        addressReceiver,
                                                        groupReceiver,
                                                        new Text(emailContent),
                                                        null);
            data.emailToEdit.emailId = emailId;
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
               statusToUser.add(new StatusMessage(error, StatusMessageColor.DANGER));
               throw new InvalidParametersException("<strong>Email Format Error</strong>");
           }
       }
       
    }
    
    /**
     * This method: <br>
     * 1. makes sure the list file given the blobkey exists in the Google Cloud Storage<br>
     * 2. goes through the list file by splitting the content of the txt file(email addresses separated by comma)
     * into separated email addresses, which makes sure the file content is intact and of correct format
     * <br><br>
     * 
     * @param listFileKey
     * @throws IOException
     */
    private void checkGroupReceiverListFile(String listFileKey) throws IOException {
        Assumption.assertNotNull(listFileKey);

        BlobKey blobKey = new BlobKey(listFileKey);
        
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
        int size = (int) getFileSize(listFileKey);    
        
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
            isError = true;
            setStatusForException(e);
            return;
        }     
        moveJobToGroupModeTaskQueue();
        moveJobToAddressModeTaskQueue();     
    }

}
