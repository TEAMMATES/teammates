package teammates.logic.api;

import java.util.HashMap;
import java.util.Map;

import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.TaskQueue;
import teammates.logic.core.TaskQueuesLogic;

/**
 * Allows for adding specific type of tasks to the task queue.
 */
public class TaskQueuer {
    
    // The following methods are facades to the actual logic for adding tasks to the queue.
    // Using this method, the actual logic can still be black-boxed
    // while at the same time allowing this API to be mocked during test.
    
    protected void addTask(String queueName, String workerUrl, Map<String, String> paramMap) {
        new TaskQueuesLogic().createAndAddTask(queueName, workerUrl, paramMap);
    }
    
    protected void addDeferredTask(String queueName, String workerUrl, Map<String, String> paramMap,
                                   long countdownTime) {
        new TaskQueuesLogic().createAndAddDeferredTask(queueName, workerUrl, paramMap, countdownTime);
    }
    
    protected void addTaskMultisetParam(String queueName, String workerUrl, Map<String, String[]> paramMap) {
        new TaskQueuesLogic().createAndAddTaskMultisetParam(queueName, workerUrl, paramMap);
    }
    
    // The following methods are the actual API methods to be used by the client classes
    
    /**
     * Schedules an admin email preparation in group mode, i.e. using the group receiver list
     * retrieved from the Google Cloud Storage (GCS).
     * <p>
     * This group receiver list is in the form of {@code List<List<String>>} accessed by two indices,
     * namely "email list index" for accessing the {@code List<String>} inside the {@code List<List<String>>}
     * and "email index" for accessing the email {@code String} inside the {@code List<String>}.
     * </p>
     * 
     * @param emailId the ID of admin email to be retrieved from the database
     * @param groupReceiverListFileKey the file key for the group receiver list in GCS
     * @param emailListIndex see method description
     * @param emailIndex see method description
     */
    public void scheduleAdminEmailPreparationInGroupMode(String emailId, String groupReceiverListFileKey,
                                                         int emailListIndex, int emailIndex) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
        paramMap.put(ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY, groupReceiverListFileKey);
        paramMap.put(ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_LIST_INDEX, Integer.toString(emailListIndex));
        paramMap.put(ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_INDEX, Integer.toString(emailIndex));
        paramMap.put(ParamsNames.ADMIN_EMAIL_TASK_QUEUE_MODE, Const.ADMIN_EMAIL_TASK_QUEUE_GROUP_MODE);
        
        addTask(TaskQueue.ADMIN_PREPARE_EMAIL_QUEUE_NAME, TaskQueue.ADMIN_PREPARE_EMAIL_WORKER_URL, paramMap);
    }
    
}
