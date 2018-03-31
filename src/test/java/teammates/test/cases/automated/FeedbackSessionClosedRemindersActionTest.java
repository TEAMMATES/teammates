package teammates.test.cases.automated;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailType;
import teammates.common.util.TaskWrapper;
import teammates.common.util.TimeHelper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.driver.TimeHelperExtension;
import teammates.ui.automated.FeedbackSessionClosedRemindersAction;

/**
 * SUT: {@link FeedbackSessionClosedRemindersAction}.
 */
public class FeedbackSessionClosedRemindersActionTest extends BaseAutomatedActionTest {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

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
        session1.setTimeZone(ZoneId.of("UTC"));
        session1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-2));
        session1.setEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-1));
        fsLogic.updateFeedbackSession(session1);
        verifyPresentInDatastore(session1);

        // Ditto, but with disabled closed reminder

        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions.get("session2InCourse1");
        session2.setTimeZone(ZoneId.of("UTC"));
        session2.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-2));
        session2.setEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-1));
        session2.setClosingEmailEnabled(false);
        fsLogic.updateFeedbackSession(session2);
        verifyPresentInDatastore(session2);

        // Still in grace period; closed reminder should not be sent

        FeedbackSessionAttributes session3 = dataBundle.feedbackSessions.get("gracePeriodSession");
        session3.setTimeZone(ZoneId.of("UTC"));
        session3.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-2));
        session3.setEndTime(Instant.now());
        fsLogic.updateFeedbackSession(session3);
        verifyPresentInDatastore(session3);

        action = getAction();
        action.execute();

        // 5 students and 5 instructors in course1
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 10);

        String courseName = coursesLogic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = action.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            Map<String, String[]> paramMap = task.getParamMap();
            assertEquals(String.format(EmailType.FEEDBACK_CLOSED.getSubject(), courseName,
                                       session1.getSessionName()),
                         paramMap.get(ParamsNames.EMAIL_SUBJECT)[0]);
        }

        ______TS("1 session closed recently with closed emails sent");

        session1.setSentClosedEmail(true);
        fsLogic.updateFeedbackSession(session1);

        action = getAction();
        action.execute();

        verifyNoTasksAdded(action);

    }

    @Override
    protected FeedbackSessionClosedRemindersAction getAction(String... params) {
        return (FeedbackSessionClosedRemindersAction) gaeSimulation.getAutomatedActionObject(getActionUri());
    }

}
