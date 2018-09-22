package teammates.test.cases.automated;

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
import teammates.ui.automated.FeedbackSessionOpeningRemindersAction;

/**
 * SUT: {@link FeedbackSessionOpeningRemindersAction}.
 */
public class FeedbackSessionOpeningRemindersActionTest extends BaseAutomatedActionTest {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

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

        // Close the session and re-open with the opening time 1 day before

        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        session1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(2));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3));
        fsLogic.updateFeedbackSession(session1);
        session1.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-23));
        fsLogic.updateFeedbackSession(session1);

        // Ditto, but disable the opening reminder, but currently open emails will still be sent regardless

        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions.get("session2InCourse1");
        session2.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(2));
        session2.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3));
        session2.setOpeningEmailEnabled(false);
        fsLogic.updateFeedbackSession(session2);
        session2.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-23));
        fsLogic.updateFeedbackSession(session2);

        action = getAction();
        action.execute();

        // 5 students and 5 instructors in course1
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 20);

        String courseName = coursesLogic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = action.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            Map<String, String[]> paramMap = task.getParamMap();
            try {
                assertEquals(String.format(EmailType.FEEDBACK_OPENING.getSubject(), courseName,
                                           session1.getSessionName()),
                             paramMap.get(ParamsNames.EMAIL_SUBJECT)[0]);
            } catch (AssertionError ae) {
                assertEquals(String.format(EmailType.FEEDBACK_OPENING.getSubject(), courseName,
                                           session2.getSessionName()),
                             paramMap.get(ParamsNames.EMAIL_SUBJECT)[0]);
            }
        }

        ______TS("2 sessions opened with emails sent");

        session1.setSentOpenEmail(true);
        fsLogic.updateFeedbackSession(session1);
        session2.setSentOpenEmail(true);
        fsLogic.updateFeedbackSession(session2);

        action = getAction();
        action.execute();

        verifyNoTasksAdded(action);

    }

    @Override
    protected FeedbackSessionOpeningRemindersAction getAction(String... params) {
        return (FeedbackSessionOpeningRemindersAction) gaeSimulation.getAutomatedActionObject(getActionUri());
    }

}
