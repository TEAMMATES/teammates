package teammates.ui.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.logic.api.GateKeeper;
import teammates.logic.core.Emails;
import teammates.logic.core.TaskQueuesLogic;

public class AdminEmailComposeSendAction extends Action {
    
    private List<String> addressReceiver = new ArrayList<String>();
    private List<String> groupReceiver = new ArrayList<String>();
    
    private final int MAX_READING_LENGTH = 900000; 
    
    //params needed to move heavy jobs into a queue task
    private String groupReceiverListFileKey = null;
    private String groupReceiverListFileSize = null;
    private String emailId = null;
    
    @Override
    protected ActionResult execute() {
        
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailComposePageData data = new AdminEmailComposePageData(account);
        
        String emailContent = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_CONTENT);
        String subject = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_SUBJECT);
        String receiver = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_RECEVIER);
        
        emailId = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ID);
        
        groupReceiverListFileKey = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);    
        groupReceiverListFileSize = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_SIZE);
        
        try {
            checkReceiverList(groupReceiverListFileKey,groupReceiverListFileSize);
            
        } catch (IOException e) {
            setStatusForException(e, "An error occurred when retrieving receiver list, please try again");
            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
        }
        
        
        boolean isEmailDraft = emailId != null && !emailId.isEmpty();
        
        addressReceiver.add(receiver);
        groupReceiver.add(groupReceiverListFileKey);
        

        if(!isEmailDraft) {
            recordNewSentEmail(subject, addressReceiver, groupReceiver, emailContent);
        } else {
            updateDraftEmailToSent(emailId, subject, addressReceiver, groupReceiver, emailContent);
        }
 

        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }
    
    private void checkReceiverList(String listFileKey, String sizeAsString) throws IOException {
        Assumption.assertNotNull(listFileKey);
        Assumption.assertNotNull(sizeAsString);
       
        BlobKey blobKey = new BlobKey(listFileKey);
        
       
        int offset = 0;
        int size = Integer.parseInt(sizeAsString);
        
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
    
    private void moveJobToTaskQueue(){
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();     
        
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
        paramMap.put(ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY, groupReceiverListFileKey);
        paramMap.put(ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_SIZE, groupReceiverListFileSize);
        
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
            moveJobToTaskQueue();
            
        } catch (InvalidParametersException e) {
            isError = true;
            setStatusForException(e, e.getMessage());
        }
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
            moveJobToTaskQueue();
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            setStatusForException(e);
        }
        
    }

}
