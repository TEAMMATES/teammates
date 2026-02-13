package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.common.util.TimeHelper;
import teammates.common.util.TimeHelperExtension;
import teammates.test.ThreadHelper;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link FeedbackSessionOpeningSoonRemindersAction}.
 */
public class FeedbackSessionOpeningSoonRemindersActionTest
        extends BaseActionTest<FeedbackSessionOpeningSoonRemindersAction> {

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_SOON_REMINDERS;
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
        ______TS("default state of typical data bundle: no sessions opening soon");

        FeedbackSessionOpeningSoonRemindersAction action = getAction();
        action.execute();

        verifyNoTasksAdded();

        ______TS("2 sessions that are opening soon should send opening soon emails");
        // Close the session and re-open with the opening time within 24 hours from now

        FeedbackSessionAttributes session1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        session1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(2));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());
        session1.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(24));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withStartTime(session1.getStartTime())
                        .build());

        FeedbackSessionAttributes session2 = typicalBundle.feedbackSessions.get("session2InCourse1");
        session2.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(2));
        session2.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3));
        session2.setOpenedEmailEnabled(false);
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withStartTime(session2.getStartTime())
                        .withEndTime(session2.getEndTime())
                        .build());
        session2.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(24));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withStartTime(session2.getStartTime())
                        .build());

        action = getAction();
        action.execute();

        // 3 co-owners in course1 x 2 sessions
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 6);

        // check that the subject matches either session 1 or session 2's details
        String courseName = logic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            try {
                assertEquals(String.format(EmailType.FEEDBACK_OPENING_SOON.getSubject(), courseName,
                        session1.getFeedbackSessionName()),
                        email.getSubject());
            } catch (AssertionError ae) {
                assertEquals(String.format(EmailType.FEEDBACK_OPENING_SOON.getSubject(), courseName,
                        session2.getFeedbackSessionName()),
                        email.getSubject());
            }
        }

        ______TS("session opening soon with emails already sent should not send more emails");

        session1.setSentOpeningSoonEmail(true);
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withSentOpeningSoonEmail(session1.isSentOpeningSoonEmail())
                        .build());
        session2.setSentOpeningSoonEmail(true);
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withSentOpeningSoonEmail(session2.isSentOpeningSoonEmail())
                        .build());

        action = getAction();
        action.execute();

        verifyNoTasksAdded();

        ______TS("Modifying a session's opening time from >24h in the future to "
                + "<24 hours from now should not send an opening soon email ");

        // close session
        session1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(2));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());

        // reopen session to miss the opening soon email time limit
        session1.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(12));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withStartTime(session1.getStartTime())
                        .build());

        action = getAction();
        action.execute();

        verifyNoTasksAdded();

        ______TS("Modifying an opened session, for which an opening soon email has already been sent, "
                + "to open in 24 hours should resend an opening soon email");

        session1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-2));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withSentOpeningSoonEmail(true)
                        .withSentOpenedEmail(true)
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());

        // allow session to be off the time limit to ensure that sentOpenedEmail is marked false
        session1.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(24).plusSeconds(10));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3)); // random date in future
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());

        assertFalse(logic.getFeedbackSession(session1.getFeedbackSessionName(),
                session1.getCourseId()).isSentOpeningSoonEmail());

        ThreadHelper.waitFor(10000); // wait for the session to be inside time limit so email is sent

        action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 3);

        tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();

            assertEquals(String.format(EmailType.FEEDBACK_OPENING_SOON.getSubject(), courseName,
                    session1.getFeedbackSessionName()),
                    email.getSubject());
        }

        ______TS("Modifying a session which has started and ended, "
                + "to open in 24 hours should resend an opening soon email");
        // similar to previous but the session has already ended in the past and is being reopened
        // this could happen if an instructor reuses a session for example

        session1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-2));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withSentOpeningSoonEmail(true)
                        .withSentOpenedEmail(true)
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());

        session1.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(24).plusSeconds(10));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3)); // random date in future
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());

        ThreadHelper.waitFor(10000); // wait for the session to be in the time limit so that email is sent

        action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 3);

        tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();

            assertEquals(String.format(EmailType.FEEDBACK_OPENING_SOON.getSubject(), courseName,
                    session1.getFeedbackSessionName()),
                    email.getSubject());
        }

        ______TS("Modifying an opened session with opening soon email already sent, to open in < 24 hours "
                + "should not send another opening soon email");

        // set start and end time to be sometime in the past
        session1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-2));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));

        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withSentOpeningSoonEmail(true)
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());

        session1.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(23));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());

        action = getAction();
        action.execute();

        verifyNoTasksAdded();

    }

}
