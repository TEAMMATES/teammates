package teammates.test.cases.automated;

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
import teammates.ui.automated.FeedbackSessionClosingRemindersAction;

/**
 * SUT: {@link FeedbackSessionClosingRemindersAction}.
 */
public class FeedbackSessionClosingRemindersActionTest extends BaseAutomatedActionTest {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS;
    }

    @Test
    public void allTests() throws Exception {

        ______TS("default state of typical data bundle: 0 sessions closing soon");

        FeedbackSessionClosingRemindersAction action = getAction();
        action.execute();

        verifyNoTasksAdded(action);

        ______TS("1 session closing soon, 1 session closing soon with disabled closing reminder, "
                 + "1 session closing soon but not yet opened");

        // Modify session to close in 24 hours

        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        session1.setTimeZone(ZoneId.of("UTC"));
        session1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        fsLogic.updateFeedbackSession(session1);
        verifyPresentInDatastore(session1);

        // Ditto, but disable the closing reminder

        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions.get("session1InCourse2");
        session2.setTimeZone(ZoneId.of("UTC"));
        session2.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session2.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        session2.setClosingEmailEnabled(false);
        fsLogic.updateFeedbackSession(session2);
        verifyPresentInDatastore(session2);

        // 1 session not yet opened; do not send the closing reminder

        FeedbackSessionAttributes session3 = dataBundle.feedbackSessions.get("gracePeriodSession");
        session3.setTimeZone(ZoneId.of("UTC"));
        session3.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(1));
        session3.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        fsLogic.updateFeedbackSession(session3);
        verifyPresentInDatastore(session3);

        action = getAction();
        action.execute();

        // 5 students and 5 instructors in course1, 2 students have completed the feedback session
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 8);

        String courseName = coursesLogic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = action.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            Map<String, String[]> paramMap = task.getParamMap();
            assertEquals(String.format(EmailType.FEEDBACK_CLOSING.getSubject(), courseName,
                                       session1.getSessionName()),
                         paramMap.get(ParamsNames.EMAIL_SUBJECT)[0]);
        }

        ______TS("1 session closing soon with emails sent");

        session1.setSentClosingEmail(true);
        fsLogic.updateFeedbackSession(session1);

        action = getAction();
        action.execute();

        verifyNoTasksAdded(action);

    }

    @Override
    protected FeedbackSessionClosingRemindersAction getAction(String... params) {
        return (FeedbackSessionClosingRemindersAction) gaeSimulation.getAutomatedActionObject(getActionUri());
    }

}
