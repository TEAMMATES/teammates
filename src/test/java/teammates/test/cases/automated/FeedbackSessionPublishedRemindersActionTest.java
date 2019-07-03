package teammates.test.cases.automated;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.ui.automated.FeedbackSessionPublishedRemindersAction;

/**
 * SUT: {@link FeedbackSessionPublishedRemindersAction}.
 */
public class FeedbackSessionPublishedRemindersActionTest
        extends BaseAutomatedActionTest<FeedbackSessionPublishedRemindersAction> {

    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS;
    }

    @Test
    public void allTests() throws Exception {

        ______TS("default state of typical data bundle: 1 session published with email unsent");

        FeedbackSessionPublishedRemindersAction action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(action, Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 2);

        ______TS("1 session published by moving automated publish time, "
                 + "1 session published similarly with disabled published reminder, "
                 + "1 session published manually");

        // Publish session by moving automated publish time

        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        session1.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withResultsVisibleFromTime(session1.getResultsVisibleFromTime())
                        .build());
        verifyPresentInDatastore(session1);

        // Publish session by moving automated publish time and disable publish reminder

        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions.get("session2InCourse1");
        session2.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session2.setPublishedEmailEnabled(false);
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session2.getFeedbackSessionName(), session2.getCourseId())
                        .withResultsVisibleFromTime(session2.getResultsVisibleFromTime())
                        .withIsPublishedEmailEnabled(session2.isPublishedEmailEnabled())
                        .build());
        verifyPresentInDatastore(session2);

        // Do a manual publish

        FeedbackSessionAttributes session3 = dataBundle.feedbackSessions.get("gracePeriodSession");
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session3.getFeedbackSessionName(), session3.getCourseId())
                        .withResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER)
                        .build());
        fsLogic.publishFeedbackSession(session3.getFeedbackSessionName(), session3.getCourseId());
        session3.setResultsVisibleFromTime(
                fsLogic.getFeedbackSession(session3.getFeedbackSessionName(), session3.getCourseId())
                        .getResultsVisibleFromTime());
        verifyPresentInDatastore(session3);

        action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(action, Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 4);

        ______TS("1 session unpublished manually");

        fsLogic.unpublishFeedbackSession(session3.getFeedbackSessionName(), session3.getCourseId());

        action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(action, Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 3);

        ______TS("1 session published with emails sent");

        session1.setSentPublishedEmail(true);
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session1.getFeedbackSessionName(), session1.getCourseId())
                        .withSentPublishedEmail(session1.isSentPublishedEmail())
                        .build());

        action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(action, Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 2);

    }

}
