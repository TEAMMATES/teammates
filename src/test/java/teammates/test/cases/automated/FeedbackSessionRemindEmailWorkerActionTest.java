package teammates.test.cases.automated;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.ui.automated.FeedbackSessionRemindEmailWorkerAction;

/**
 * SUT: {@link FeedbackSessionRemindEmailWorkerAction}.
 */
public class FeedbackSessionRemindEmailWorkerActionTest extends BaseAutomatedActionTest {
    
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_REMIND_EMAIL_WORKER_URL;
    }
    
    @Test
    public void allTests() {
        
        ______TS("Send feedback session reminder email");
        
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[] {
                ParamsNames.SUBMISSION_FEEDBACK, session1.getFeedbackSessionName(),
                ParamsNames.SUBMISSION_COURSE, session1.getCourseId()
        };
        
        FeedbackSessionRemindEmailWorkerAction action = getAction(submissionParams);
        action.execute();
        
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 11);
        
    }
    
    private FeedbackSessionRemindEmailWorkerAction getAction(String... submissionParams) {
        return (FeedbackSessionRemindEmailWorkerAction)
                gaeSimulation.getAutomatedActionObject(getActionUri(), submissionParams);
    }
    
}
