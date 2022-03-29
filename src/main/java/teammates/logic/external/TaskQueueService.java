package teammates.logic.external;

import teammates.common.util.TaskWrapper;

/**
 * Handles operations related to task queues.
 */
public interface TaskQueueService {

    /**
     * Adds the given task, to be run after the specified time, to the specified queue.
     *
     * @param task the task object containing the details of task to be added
     * @param countdownTime the time delay for the task to be executed
     */
    void addDeferredTask(TaskWrapper task, long countdownTime);

}
