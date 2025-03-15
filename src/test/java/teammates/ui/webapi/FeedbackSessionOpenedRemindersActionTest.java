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
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link FeedbackSessionOpenedRemindersAction}.
 */
public class FeedbackSessionOpenedRemindersActionTest
        extends BaseActionTest<FeedbackSessionOpenedRemindersAction> {

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENED_REMINDERS;
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

        ______TS("default state of typical data bundle: no sessions opened");

        FeedbackSessionOpenedRemindersAction action = getAction();
        action.execute();

        verifyNoTasksAdded();

        ______TS("2 session opened, emails not sent");

        // Close the session and re-open with the opening time 1 day before

        FeedbackSessionAttributes session1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        session1.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(2));
        session1.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(3));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withStartTime(session1.getStartTime())
                        .withEndTime(session1.getEndTime())
                        .build());
        session1.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-23));
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
        session2.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-23));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withStartTime(session2.getStartTime())
                        .build());

        action = getAction();
        action.execute();

        // 5 students, 5 instructors, and 3 co-owner instructors in course1
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 26);

        String courseName = logic.getCourse(session1.getCourseId()).getName();
        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            try {
                String expectedSubject = (email.getIsCopy() ? EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX : "")
                        + String.format(EmailType.FEEDBACK_OPENED.getSubject(),
                        courseName, session1.getFeedbackSessionName());
                assertEquals(expectedSubject, email.getSubject());
            } catch (AssertionError ae) {
                String expectedSubject = (email.getIsCopy() ? EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX : "")
                        + String.format(EmailType.FEEDBACK_OPENED.getSubject(),
                        courseName, session2.getFeedbackSessionName());
                assertEquals(expectedSubject, email.getSubject());
            }
        }

        ______TS("2 sessions opened with emails sent");

        session1.setSentOpenedEmail(true);
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withSentOpenedEmail(session1.isSentOpenedEmail())
                        .build());
        session2.setSentOpenedEmail(true);
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withSentOpenedEmail(session2.isSentOpenedEmail())
                        .build());

        action = getAction();
        action.execute();

        verifyNoTasksAdded();

    }

}
