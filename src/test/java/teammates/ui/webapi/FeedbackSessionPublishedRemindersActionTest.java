package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

/**
 * SUT: {@link FeedbackSessionPublishedRemindersAction}.
 */
public class FeedbackSessionPublishedRemindersActionTest
        extends BaseActionTest<FeedbackSessionPublishedRemindersAction> {

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS;
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

        ______TS("default state of typical data bundle: 1 session published with email unsent");

        FeedbackSessionPublishedRemindersAction action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(action, Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 2);

        ______TS("1 session published by moving automated publish time, "
                 + "1 session published similarly with disabled published reminder, "
                 + "1 session published manually");

        // Publish session by moving automated publish time

        FeedbackSessionAttributes session1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        session1.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withResultsVisibleFromTime(session1.getResultsVisibleFromTime())
                        .build());
        verifyPresentInDatastore(session1);

        // Publish session by moving automated publish time and disable publish reminder

        FeedbackSessionAttributes session2 = typicalBundle.feedbackSessions.get("session2InCourse1");
        session2.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session2.setPublishedEmailEnabled(false);
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withResultsVisibleFromTime(session2.getResultsVisibleFromTime())
                        .withIsPublishedEmailEnabled(session2.isPublishedEmailEnabled())
                        .build());
        verifyPresentInDatastore(session2);

        // Do a manual publish

        FeedbackSessionAttributes session3 = typicalBundle.feedbackSessions.get("gracePeriodSession");
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session3.getFeedbackSessionName(), session3.getCourseId())
                        .withResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER)
                        .build());
        logic.publishFeedbackSession(session3.getFeedbackSessionName(), session3.getCourseId());
        session3.setResultsVisibleFromTime(
                logic.getFeedbackSession(session3.getFeedbackSessionName(), session3.getCourseId())
                        .getResultsVisibleFromTime());
        verifyPresentInDatastore(session3);

        action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(action, Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 4);

        ______TS("1 session unpublished manually");

        logic.unpublishFeedbackSession(session3.getFeedbackSessionName(), session3.getCourseId());

        action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(action, Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 3);

        ______TS("1 session published with emails sent");

        session1.setSentPublishedEmail(true);
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withSentPublishedEmail(session1.isSentPublishedEmail())
                        .build());

        action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(action, Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 2);

    }

}
