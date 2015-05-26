package teammates.logic.automated;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.apphosting.api.ApiProxy;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.logic.core.AdminEmailsLogic;
import teammates.logic.core.TaskQueuesLogic;

/**
 * This class creates admin email tasks for receiver emails<br>
 * It has two modes : <br>
 * 
 * 1. Address Mode: receiver emails are retrieved from receiver list string 
 * 2. Group Mode: receiver emails are retrieved from a txt file uploaded into Google Cloud Storage.
 */
@SuppressWarnings("serial")
public class AdminEmailPrepareTaskQueueWorkerServlet extends WorkerServlet {
    
    private List<List<String>> processedReceiverEmails = new ArrayList<List<String>>();
    
    final int MAX_READING_LENGTH = 900000; 
    
    
    private String adminEmailTaskQueueMode = null;
    
    //param needed for sending small number of emails
    private String addressReceiverListString = null;
    
    //params needed to move heavy jobs into a queue task
    private String groupReceiverListFileKey = null;
    private int groupReceiverListFileSize = 0;
    private String emailId = null;
    

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        
        
        adminEmailTaskQueueMode = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_TASK_QUEUE_MODE);
        Assumption.assertNotNull(adminEmailTaskQueueMode);
        
        if(adminEmailTaskQueueMode.contains(Const.ADMIN_EMAIL_TASK_QUEUE_ADDRESS_MODE)){
        
            log.info("Preparing admin email task queue in address mode...");
            
            emailId =  HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_ID);        
            Assumption.assertNotNull(emailId);
            
            addressReceiverListString = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_ADDRESS_RECEVIERS);
            Assumption.assertNotNull(addressReceiverListString);
            
            addAdminEmailToTaskQueue(emailId);
            
        } else if (adminEmailTaskQueueMode.contains(Const.ADMIN_EMAIL_TASK_QUEUE_GROUP_MODE)){
            
            log.info("Preparing admin email task queue in group mode...");
        
            emailId =  HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_ID);        
            Assumption.assertNotNull(emailId);
            
            groupReceiverListFileKey =  HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);        
            Assumption.assertNotNull(groupReceiverListFileKey);
            
            groupReceiverListFileSize = (int) getFileSize(groupReceiverListFileKey);
            
            String indexOfEmailListToResumeAsString = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_LIST_INDEX);
            String indexOfEmailToResumeAsString = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_INDEX);
            
            int indexOfEmailListToResume = indexOfEmailListToResumeAsString == null ? 0 : Integer.parseInt(indexOfEmailListToResumeAsString);
            int indexOfEmailToResume = indexOfEmailToResumeAsString == null ? 0 : Integer.parseInt(indexOfEmailToResumeAsString);
  
            try {
                processedReceiverEmails = getReceiverList(groupReceiverListFileKey, groupReceiverListFileSize);
                addAdminEmailToTaskQueue(emailId, indexOfEmailListToResume, indexOfEmailToResume);
    
            } catch (IOException e) {
                log.severe("Unexpected error while adding admin email tasks" + e.getMessage());
            }
        
        }
    }
    
    private long getFileSize(String blobkeyString){
        BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
        BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(new BlobKey(blobkeyString));
        long blobSize = blobInfo.getSize();
        return blobSize;
    }
    
    /**
     * This method: <br>
     * 1. retrieve the receiver txt file from the Google Cloud Storage
     * 2. goes through the list file by splitting the content of the txt file(email addresses separated by comma)
     * into separated email addresses. This is needed for creating admin email task for each receiver.
     * @param listFileKey
     * @param size
     * @throws IOException
     */
    private List<List<String>> getReceiverList(String listFileKey, int size) 
            throws IOException {
        
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
        
        //this is the list of list
        List<List<String>> listOfList = new LinkedList<List<String>>();
        
        //file size is needed to track the number of unread bytes 
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
        
        return listOfList;

    }
    
    private boolean isNearDeadline(){
        
        long timeLeftInMillis = ApiProxy.getCurrentEnvironment().getRemainingMillis();
        if (timeLeftInMillis/1000 < 100){
            return true;
        }
        
        return false;
    }
    
    private void pauseAndCreateAnNewTask(int indexOfEmailList, int indexOfEmail){
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();     
        
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
        paramMap.put(ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY, groupReceiverListFileKey);
        paramMap.put(ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_LIST_INDEX, "" + indexOfEmailList);
        paramMap.put(ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_INDEX, "" + indexOfEmail);
        paramMap.put(ParamsNames.ADMIN_EMAIL_TASK_QUEUE_MODE, Const.ADMIN_EMAIL_TASK_QUEUE_GROUP_MODE);
        
        taskQueueLogic.createAndAddTask(SystemParams.ADMIN_PREPARE_EMAIL_TASK_QUEUE,
                                        Const.ActionURIs.ADMIN_EMAIL_PREPARE_TASK_QUEUE_WORKER, paramMap); 
                
    }
    
    private void addAdminEmailToTaskQueue(String emailId){
        
        AdminEmailAttributes adminEmail = AdminEmailsLogic.inst().getAdminEmailById(emailId);      
        Assumption.assertNotNull(adminEmail); 
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();         
        List<String> addressList = new ArrayList<String>();
        
        if(!addressReceiverListString.contains(",")){
            addressList.add(addressReceiverListString);
        } else {
            addressList.addAll(Arrays.asList(addressReceiverListString.split(",")));
        }    
        
        for(String emailAddress : addressList){     
            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
            paramMap.put(ParamsNames.ADMIN_EMAIL_RECEVIER, emailAddress);
            paramMap.put(ParamsNames.ADMIN_EMAIL_SUBJECT, adminEmail.getSubject());
            paramMap.put(ParamsNames.ADMIN_EMAIL_CONTENT, adminEmail.getContent().getValue());
            
            try {  
                taskQueueLogic.createAndAddTask(SystemParams.ADMIN_EMAIL_TASK_QUEUE,
                                                Const.ActionURIs.ADMIN_EMAIL_WORKER, paramMap);
            } catch (IllegalArgumentException e){
                if(e.getMessage().toLowerCase().contains("task size too large")){
                    log.info("Email task size exceeds max limit. Switching to large email task mode.");
                    paramMap.remove(ParamsNames.ADMIN_EMAIL_SUBJECT);
                    paramMap.remove(ParamsNames.ADMIN_EMAIL_CONTENT);
                    taskQueueLogic.createAndAddTask(SystemParams.ADMIN_EMAIL_TASK_QUEUE,
                                                    Const.ActionURIs.ADMIN_EMAIL_WORKER, paramMap);
                }
            }
               
        }

    }
    
    private void addAdminEmailToTaskQueue(String emailId, int indexOfEmailListToResume, int indexOfEmailToResume){
        
        AdminEmailAttributes adminEmail = AdminEmailsLogic.inst().getAdminEmailById(emailId);      
        Assumption.assertNotNull(adminEmail);       
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        
        log.info("Resume Adding group mail tasks for mail with id " + emailId + "from list index: "+
                 indexOfEmailListToResume + " email index: " + indexOfEmailToResume);
        
        int indexOfLastEmailList = 0;
        int indexOfLastEmail = 0;
        
        for(int i = indexOfEmailListToResume; i < processedReceiverEmails.size() ; i ++ ){
            
            List<String> currentEmailList = processedReceiverEmails.get(i);
            
            for(int j = indexOfEmailToResume; j < currentEmailList.size(); j++){
                String receiverEmail = currentEmailList.get(j);
                
                HashMap<String, String> paramMap = new HashMap<String, String>();
                paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
                paramMap.put(ParamsNames.ADMIN_EMAIL_RECEVIER, receiverEmail);
                paramMap.put(ParamsNames.ADMIN_EMAIL_SUBJECT, adminEmail.getSubject());
                paramMap.put(ParamsNames.ADMIN_EMAIL_CONTENT, adminEmail.getContent().getValue());
                
                try {  
                    taskQueueLogic.createAndAddTask(SystemParams.ADMIN_EMAIL_TASK_QUEUE,
                                                    Const.ActionURIs.ADMIN_EMAIL_WORKER, paramMap);
                } catch (IllegalArgumentException e){
                    if(e.getMessage().toLowerCase().contains("task size too large")){
                        log.info("Email task size exceeds max limit. Switching to large email task mode.");
                        paramMap.remove(ParamsNames.ADMIN_EMAIL_SUBJECT);
                        paramMap.remove(ParamsNames.ADMIN_EMAIL_CONTENT);
                        taskQueueLogic.createAndAddTask(SystemParams.ADMIN_EMAIL_TASK_QUEUE,
                                                        Const.ActionURIs.ADMIN_EMAIL_WORKER, paramMap);
                    }
                }               
                
                if(isNearDeadline())
                {
                    pauseAndCreateAnNewTask(i, j);
                    log.info("Adding group mail tasks for mail with id " + emailId + "have been paused with list index: " + i + " email index: " + j);
                    return;
                }
                
                indexOfLastEmail = j;
            }          
            indexOfLastEmailList = i;   
        }
        
        log.info("Adding Group mail tasks for mail with id " + emailId + 
                 "was complete. List index : " + indexOfLastEmailList + 
                 " Email index: " + indexOfLastEmail);
    }
}
