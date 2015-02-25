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

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.logic.core.TaskQueuesLogic;

@SuppressWarnings("serial")
public class AdminEmailPrepareTaskQueueWorkerServlet extends WorkerServlet {
    
    private List<List<String>> processedReceiverEmails = new ArrayList<List<String>>();
    
    final int MAX_READING_LENGTH = 900000; 
    

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String emailId =  HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_ID);        
        Assumption.assertNotNull(emailId);
        
        String listFileKey =  HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);        
        Assumption.assertNotNull(listFileKey);
        
        String sizeAsString =  HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_SIZE);        
        Assumption.assertNotNull(sizeAsString);
        
        try {
            processedReceiverEmails = getReceiverList(listFileKey, sizeAsString);
            addAdminEmailToTaskQueue(emailId);
            log.info("Group mail tasks for mail with id " + emailId + "have been added.");
        } catch (IOException e) {
            log.severe("Unexpected error while adding admin email tasks" + e.getMessage());
        }
        
        
        
    }
    
    private List<List<String>> getReceiverList(String listFileKey, String sizeAsString) throws IOException {
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
       
        
        return listOfList;

    }
    
    
    private void addAdminEmailToTaskQueue(String emailId){
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        
        for(List<String> list : processedReceiverEmails){
            for(String receiverEmail : list){
                HashMap<String, String> paramMap = new HashMap<String, String>();
                paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
                paramMap.put(ParamsNames.ADMIN_EMAIL_RECEVIER, receiverEmail);
                
                taskQueueLogic.createAndAddTask(SystemParams.ADMIN_EMAIL_TASK_QUEUE,
                        Const.ActionURIs.ADMIN_EMAIL_WORKER, paramMap); 
                
            }
        }
        
        
    }
}
