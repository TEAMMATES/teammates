package teammates.test.cases.automated;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.ui.automated.FeedbackSessionClosedRemindersAction;

/**
 * SUT: {@link FeedbackSessionClosedRemindersAction}.
 */
public class FeedbackSessionClosedRemindersActionTest extends BaseAutomatedActionTest {
    
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @Override
    protected String getActionUri() {
        return Const.ActionURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS;
    }
    
    @Test
    public void allTests() throws Exception {
        
        ______TS("default state of typical data bundle: 0 sessions closed recently");
        
        FeedbackSessionClosedRemindersAction action = getAction();
        action.execute();
        
        verifyNoTasksAdded(action);
        
        ______TS("1 session closed recently, 1 session closed recently with disabled closed reminder, "
                 + "1 session closed recently but still in grace period");
        
        // Session is closed recently
        
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        session1.setTimeZone(0);
        session1.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-2));
        session1.setEndTime(TimeHelper.getHoursOffsetToCurrentTime(-1));
        fsLogic.updateFeedbackSession(session1);
        verifyPresentInDatastore(session1);
        
        // Ditto, but with disabled closed reminder
        
        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions.get("session2InCourse1");
        session2.setTimeZone(0);
        session2.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-2));
        session2.setEndTime(TimeHelper.getHoursOffsetToCurrentTime(-1));
        session2.setClosingEmailEnabled(false);
        fsLogic.updateFeedbackSession(session2);
        verifyPresentInDatastore(session2);
        
        // Still in grace period; closed reminder should not be sent
        
        FeedbackSessionAttributes session3 = dataBundle.feedbackSessions.get("gracePeriodSession");
        session3.setTimeZone(0);
        session3.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-2));
        session3.setEndTime(TimeHelper.getDateOffsetToCurrentTime(0));
        fsLogic.updateFeedbackSession(session3);
        verifyPresentInDatastore(session3);
        
        action = getAction();
        action.execute();
        
        // 5 students and 5 instructors in course1
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 10);
        
        ______TS("1 session closed recently with closed emails sent");
        
        session1.setSentClosedEmail(true);
        fsLogic.updateFeedbackSession(session1);
        
        action = getAction();
        action.execute();
        
        verifyNoTasksAdded(action);
        
    }
    
    private FeedbackSessionClosedRemindersAction getAction() {
        return (FeedbackSessionClosedRemindersAction) gaeSimulation.getAutomatedActionObject(getActionUri());
    }
    
}
