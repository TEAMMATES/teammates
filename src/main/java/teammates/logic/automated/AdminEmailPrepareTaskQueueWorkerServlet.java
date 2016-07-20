package teammates.logic.automated;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.AdminEmailsLogic;
import teammates.logic.core.TaskQueuesLogic;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.apphosting.api.ApiProxy;

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
    
    //param needed for sending small number of emails
    private String addressReceiverListString;
    
    //params needed to move heavy jobs into a queue task
    private String groupReceiverListFileKey;
    private String emailId;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        
        String adminEmailTaskQueueMode =
                HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_TASK_QUEUE_MODE);

        Assumption.assertNotNull(adminEmailTaskQueueMode);
        
        if (adminEmailTaskQueueMode.contains(Const.ADMIN_EMAIL_TASK_QUEUE_ADDRESS_MODE)) {
        
            log.info("Preparing admin email task queue in address mode...");
            
            emailId = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_ID);
            Assumption.assertNotNull(emailId);
            
            addressReceiverListString =
                    HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS);
            Assumption.assertNotNull(addressReceiverListString);
            
            addAdminEmailToTaskQueue(emailId);
            
        } else if (adminEmailTaskQueueMode.contains(Const.ADMIN_EMAIL_TASK_QUEUE_GROUP_MODE)) {
            
            log.info("Preparing admin email task queue in group mode...");
        
            emailId = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_ID);
            Assumption.assertNotNull(emailId);
            
            groupReceiverListFileKey =
                    HttpRequestHelper.getValueFromRequestParameterMap(
                            req, ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);
            Assumption.assertNotNull(groupReceiverListFileKey);
            
            String indexOfEmailListToResumeAsString =
                    HttpRequestHelper.getValueFromRequestParameterMap(
                            req, ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_LIST_INDEX);
            String indexOfEmailToResumeAsString =
                    HttpRequestHelper.getValueFromRequestParameterMap(
                            req, ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_INDEX);
            
            int indexOfEmailListToResume = indexOfEmailListToResumeAsString == null
                                           ? 0
                                           : Integer.parseInt(indexOfEmailListToResumeAsString);
            int indexOfEmailToResume = indexOfEmailToResumeAsString == null
                                       ? 0
                                       : Integer.parseInt(indexOfEmailToResumeAsString);
  
            try {
                processedReceiverEmails =
                        GoogleCloudStorageHelper.getGroupReceiverList(new BlobKey(groupReceiverListFileKey));
                addAdminEmailToTaskQueue(emailId, indexOfEmailListToResume, indexOfEmailToResume);
    
            } catch (IOException e) {
                log.severe("Unexpected error while adding admin email tasks" + e.getMessage());
            }
        
        }
    }
    
    private boolean isNearDeadline() {
        
        long timeLeftInMillis = ApiProxy.getCurrentEnvironment().getRemainingMillis();
        return timeLeftInMillis / 1000 < 100;
    }
    
    private void pauseAndCreateAnNewTask(int indexOfEmailList, int indexOfEmail) {
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
        paramMap.put(ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY, groupReceiverListFileKey);
        paramMap.put(ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_LIST_INDEX, Integer.toString(indexOfEmailList));
        paramMap.put(ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_INDEX, Integer.toString(indexOfEmail));
        paramMap.put(ParamsNames.ADMIN_EMAIL_TASK_QUEUE_MODE, Const.ADMIN_EMAIL_TASK_QUEUE_GROUP_MODE);
        
        taskQueueLogic.createAndAddTask(SystemParams.ADMIN_PREPARE_EMAIL_TASK_QUEUE,
                                        Const.ActionURIs.ADMIN_EMAIL_PREPARE_TASK_QUEUE_WORKER, paramMap);
                
    }
    
    private void addAdminEmailToTaskQueue(String emailId) {
        
        AdminEmailAttributes adminEmail = AdminEmailsLogic.inst().getAdminEmailById(emailId);
        Assumption.assertNotNull(adminEmail);
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        List<String> addressList = new ArrayList<String>();
        
        if (addressReceiverListString.contains(",")) {
            addressList.addAll(Arrays.asList(addressReceiverListString.split(",")));
        } else {
            addressList.add(addressReceiverListString);
        }
        
        for (String emailAddress : addressList) {
            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
            paramMap.put(ParamsNames.ADMIN_EMAIL_RECEIVER, emailAddress);
            paramMap.put(ParamsNames.ADMIN_EMAIL_SUBJECT, adminEmail.getSubject());
            paramMap.put(ParamsNames.ADMIN_EMAIL_CONTENT, adminEmail.getContent().getValue());
            
            try {
                taskQueueLogic.createAndAddTask(SystemParams.ADMIN_EMAIL_TASK_QUEUE,
                                                Const.ActionURIs.ADMIN_EMAIL_WORKER, paramMap);
            } catch (IllegalArgumentException e) {
                if (e.getMessage().toLowerCase().contains("task size too large")) {
                    log.info("Email task size exceeds max limit. Switching to large email task mode.");
                    paramMap.remove(ParamsNames.ADMIN_EMAIL_SUBJECT);
                    paramMap.remove(ParamsNames.ADMIN_EMAIL_CONTENT);
                    taskQueueLogic.createAndAddTask(SystemParams.ADMIN_EMAIL_TASK_QUEUE,
                                                    Const.ActionURIs.ADMIN_EMAIL_WORKER, paramMap);
                }
            }
               
        }

    }
    
    private void addAdminEmailToTaskQueue(String emailId, int indexOfEmailListToResume, int indexOfEmailToResume) {
        
        AdminEmailAttributes adminEmail = AdminEmailsLogic.inst().getAdminEmailById(emailId);
        Assumption.assertNotNull(adminEmail);
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        
        log.info("Resume Adding group mail tasks for mail with id " + emailId + "from list index: "
                + indexOfEmailListToResume + " email index: " + indexOfEmailToResume);
        
        int indexOfLastEmailList = 0;
        int indexOfLastEmail = 0;
        
        for (int i = indexOfEmailListToResume; i < processedReceiverEmails.size(); i++) {
            
            List<String> currentEmailList = processedReceiverEmails.get(i);
            
            for (int j = indexOfEmailToResume; j < currentEmailList.size(); j++) {
                String receiverEmail = currentEmailList.get(j);
                
                HashMap<String, String> paramMap = new HashMap<String, String>();
                paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
                paramMap.put(ParamsNames.ADMIN_EMAIL_RECEIVER, receiverEmail);
                paramMap.put(ParamsNames.ADMIN_EMAIL_SUBJECT, adminEmail.getSubject());
                paramMap.put(ParamsNames.ADMIN_EMAIL_CONTENT, adminEmail.getContent().getValue());
                
                try {
                    taskQueueLogic.createAndAddTask(SystemParams.ADMIN_EMAIL_TASK_QUEUE,
                                                    Const.ActionURIs.ADMIN_EMAIL_WORKER, paramMap);
                } catch (IllegalArgumentException e) {
                    if (e.getMessage().toLowerCase().contains("task size too large")) {
                        log.info("Email task size exceeds max limit. Switching to large email task mode.");
                        paramMap.remove(ParamsNames.ADMIN_EMAIL_SUBJECT);
                        paramMap.remove(ParamsNames.ADMIN_EMAIL_CONTENT);
                        taskQueueLogic.createAndAddTask(SystemParams.ADMIN_EMAIL_TASK_QUEUE,
                                                        Const.ActionURIs.ADMIN_EMAIL_WORKER, paramMap);
                    }
                }
                
                if (isNearDeadline()) {
                    pauseAndCreateAnNewTask(i, j);
                    log.info("Adding group mail tasks for mail with id " + emailId
                             + " have been paused with list index: " + i + " email index: " + j);
                    return;
                }
                
                indexOfLastEmail = j;
            }
            indexOfLastEmailList = i;
        }
        
        log.info("Adding Group mail tasks for mail with id " + emailId
                + "was complete. List index : " + indexOfLastEmailList
                + " Email index: " + indexOfLastEmail);
    }
}
