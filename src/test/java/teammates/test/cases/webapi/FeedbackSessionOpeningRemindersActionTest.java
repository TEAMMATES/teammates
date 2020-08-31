package teammates.test.cases.webapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.common.util.TimeHelper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.driver.TimeHelperExtension;
import teammates.ui.webapi.action.FeedbackSessionOpeningRemindersAction;
import teammates.ui.webapi.request.SendEmailRequest;

/**
 * SUT: {@link FeedbackSessionOpeningRemindersAction}.
 */
public class FeedbackSessionOpeningRemindersActionTest
        extends BaseActionTest<FeedbackSessionOpeningRemindersAction> {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        ______TS("default state of typical data bundle: no sessions opened");

        FeedbackSessionOpeningRemindersAction action = getAction();
        action.execute();

        verifyNoTasksAdded(action);

        ______TS("2 session opened, emails not sent");

        // Close the session and re-open with the opening time 1 day before

        FeedbackSessionAttributes session1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        session1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(2));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3));
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());
        session1.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-23));
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withStartTime(session1.getStartTime())
                        .build());

        FeedbackSessionAttributes session2 = typicalBundle.feedbackSessions.get("session2InCourse1");
        session2.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(2));
        session2.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3));
        session2.setOpeningEmailEnabled(false);
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withStartTime(session2.getStartTime())
                        .withEndTime(session2.getEndTime())
                        .build());
        session2.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-23));
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withStartTime(session2.getStartTime())
                        .build());

        action = getAction();
        action.execute();

        // 5 students and 5 instructors in course1
        verifySpecifiedTasksAdded(action, Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 20);

        String courseName = coursesLogic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = action.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            try {
                assertEquals(String.format(EmailType.FEEDBACK_OPENING.getSubject(), courseName,
                                           session1.getFeedbackSessionName()),
                             email.getSubject());
            } catch (AssertionError ae) {
                assertEquals(String.format(EmailType.FEEDBACK_OPENING.getSubject(), courseName,
                                           session2.getFeedbackSessionName()),
                             email.getSubject());
            }
        }

        ______TS("2 sessions opened with emails sent");

        session1.setSentOpenEmail(true);
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withSentOpenEmail(session1.isSentOpenEmail())
                        .build());
        session2.setSentOpenEmail(true);
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withSentOpenEmail(session2.isSentOpenEmail())
                        .build());

        action = getAction();
        action.execute();

        verifyNoTasksAdded(action);

    }

}
