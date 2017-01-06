package teammates.test.cases.automated;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.ui.automated.FeedbackSessionRemindParticularUsersEmailWorkerAction;

/**
 * SUT: {@link FeedbackSessionRemindParticularUsersEmailWorkerAction}.
 */
public class FeedbackSessionRemindParticularUsersEmailWorkerActionTest extends BaseAutomatedActionTest {
    
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_WORKER_URL;
    }
    
    @Test
    public void allTests() {
        
        ______TS("Send feedback session reminder email");
        
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student1 = dataBundle.students.get("student1InCourse1");
        InstructorAttributes instructor1 = dataBundle.instructors.get("instructor1OfCourse1");
        
        String[] submissionParams = new String[] {
                ParamsNames.SUBMISSION_FEEDBACK, session1.getFeedbackSessionName(),
                ParamsNames.SUBMISSION_COURSE, session1.getCourseId(),
                ParamsNames.SUBMISSION_REMIND_USERLIST, student1.email,
                ParamsNames.SUBMISSION_REMIND_USERLIST, instructor1.email,
                ParamsNames.SUBMISSION_REMIND_USERLIST, "non-existent"
        };
        
        FeedbackSessionRemindParticularUsersEmailWorkerAction action = getAction(submissionParams);
        action.execute();
        
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 2);
        
    }
    
    private FeedbackSessionRemindParticularUsersEmailWorkerAction getAction(String... submissionParams) {
        return (FeedbackSessionRemindParticularUsersEmailWorkerAction)
                gaeSimulation.getAutomatedActionObject(getActionUri(), submissionParams);
    }
    
}
