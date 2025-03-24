package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.common.util.TimeHelper;
import teammates.common.util.TimeHelperExtension;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link FeedbackSessionClosedRemindersAction}.
 */
public class FeedbackSessionClosedRemindersActionTest
        extends BaseActionTest<FeedbackSessionClosedRemindersAction> {

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        ______TS("default state of typical data bundle: 0 sessions closed recently");

        FeedbackSessionClosedRemindersAction action = getAction();
        action.execute();

        verifyNoTasksAdded();

        ______TS("1 session closed recently, 1 session closed recently with disabled closed reminder, "
                 + "1 session closed recently but still in grace period");

        // Session is closed recently

        FeedbackSessionAttributes session1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        session1.setTimeZone("UTC");
        session1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-2));
        session1.setEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-1));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withTimeZone(session1.getTimeZone())
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());
        session1.setSentOpenedEmail(false); // fsLogic will set the flag to false
        session1.setSentOpeningSoonEmail(false); // fsLogic will set the flag to false
        verifyPresentInDatabase(session1);

        // Ditto, but with disabled closed reminder

        FeedbackSessionAttributes session2 = typicalBundle.feedbackSessions.get("session2InCourse1");
        session2.setTimeZone("UTC");
        session2.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-2));
        session2.setEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-1));
        session2.setClosingSoonEmailEnabled(false);
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withTimeZone(session2.getTimeZone())
                        .withStartTime(session2.getStartTime())
                        .withEndTime(session2.getEndTime())
                        .withIsClosingSoonEmailEnabled(session2.isClosingSoonEmailEnabled())
                        .build());
        session2.setSentOpenedEmail(false); // fsLogic will set the flag to false
        session2.setSentOpeningSoonEmail(false); // fsLogic will set the flag to false
        verifyPresentInDatabase(session2);

        // Still in grace period; closed reminder should not be sent

        FeedbackSessionAttributes session3 = typicalBundle.feedbackSessions.get("gracePeriodSession");
        session3.setTimeZone("UTC");
        session3.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-2));
        session3.setEndTime(Instant.now());
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session3.getFeedbackSessionName(), session3.getCourseId())
                        .withTimeZone(session3.getTimeZone())
                        .withStartTime(session3.getStartTime())
                        .withEndTime(session3.getEndTime())
                        .build());
        session3.setSentOpenedEmail(false); // fsLogic will set the flag to false
        session3.setSentOpeningSoonEmail(false); // fsLogic will set the flag to false
        verifyPresentInDatabase(session3);

        action = getAction();
        action.execute();

        // 3 co-owners in course1 x 1 session
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 3);

        String courseName = logic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            String expectedSubject = String.format(EmailType.FEEDBACK_CLOSED.getSubject(),
                    courseName, session1.getFeedbackSessionName());
            assertEquals(expectedSubject, email.getSubject());
        }

        ______TS("1 session closed recently with closed emails sent");

        session1.setSentClosedEmail(true);
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withSentClosedEmail(session1.isSentClosedEmail())
                        .build());

        action = getAction();
        action.execute();

        verifyNoTasksAdded();

    }

}
