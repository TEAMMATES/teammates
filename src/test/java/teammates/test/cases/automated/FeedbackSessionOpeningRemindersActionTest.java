package teammates.test.cases.automated;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.ui.automated.FeedbackSessionOpeningRemindersAction;

/**
 * SUT: {@link FeedbackSessionOpeningRemindersAction}.
 */
public class FeedbackSessionOpeningRemindersActionTest extends BaseAutomatedActionTest {
    
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @Override
    protected String getActionUri() {
        return Const.ActionURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS;
    }
    
    @Test
    public void allTests() throws Exception {
        
        ______TS("default state of typical data bundle: no sessions opened");
        
        FeedbackSessionOpeningRemindersAction action = getAction();
        action.execute();
        
        verifyNoTasksAdded(action);
        
        ______TS("1 session opened, 1 session opened with disabled opening reminder");
        
        // Close the session and re-open with the opening time 2 days before
        
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        session1.setStartTime(TimeHelper.getDateOffsetToCurrentTime(2));
        session1.setEndTime(TimeHelper.getDateOffsetToCurrentTime(3));
        fsLogic.updateFeedbackSession(session1);
        session1.setStartTime(TimeHelper.getHoursOffsetToCurrentTime(-47));
        fsLogic.updateFeedbackSession(session1);
        
        // Ditto, but disable the opening reminder, but currently open emails will still be sent regardless
        
        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions.get("session2InCourse1");
        session2.setStartTime(TimeHelper.getDateOffsetToCurrentTime(2));
        session2.setEndTime(TimeHelper.getDateOffsetToCurrentTime(3));
        session2.setOpeningEmailEnabled(false);
        fsLogic.updateFeedbackSession(session2);
        session2.setStartTime(TimeHelper.getHoursOffsetToCurrentTime(-47));
        fsLogic.updateFeedbackSession(session2);
        
        action = getAction();
        action.execute();
        
        // 5 students and 5 instructors in course1
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 20);
        
        ______TS("2 sessions opened with emails sent");
        
        session1.setSentOpenEmail(true);
        fsLogic.updateFeedbackSession(session1);
        session2.setSentOpenEmail(true);
        fsLogic.updateFeedbackSession(session2);
        
        action = getAction();
        action.execute();
        
        verifyNoTasksAdded(action);
        
    }
    
    private FeedbackSessionOpeningRemindersAction getAction() {
        return (FeedbackSessionOpeningRemindersAction) gaeSimulation.getAutomatedActionObject(getActionUri());
    }
    
}
