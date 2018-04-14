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
public class FeedbackSessionPublishedRemindersActionTest extends BaseAutomatedActionTest {

    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS;
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
        fsLogic.updateFeedbackSession(session1);
        verifyPresentInDatastore(session1);

        // Publish session by moving automated publish time and disable publish reminder

        FeedbackSessionAttributes session2 = dataBundle.feedbackSessions.get("session2InCourse1");
        session2.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session2.setPublishedEmailEnabled(false);
        fsLogic.updateFeedbackSession(session2);
        verifyPresentInDatastore(session2);

        // Do a manual publish

        FeedbackSessionAttributes session3 = dataBundle.feedbackSessions.get("gracePeriodSession");
        session3.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        fsLogic.updateFeedbackSession(session3);
        fsLogic.publishFeedbackSession(session3);
        verifyPresentInDatastore(session3);

        action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(action, Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 4);

        ______TS("1 session unpublished manually");

        fsLogic.unpublishFeedbackSession(session3);

        action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(action, Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 3);

        ______TS("1 session published with emails sent");

        session1.setSentPublishedEmail(true);
        fsLogic.updateFeedbackSession(session1);

        action = getAction();
        action.execute();

        verifySpecifiedTasksAdded(action, Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 2);

    }

    @Override
    protected FeedbackSessionPublishedRemindersAction getAction(String... params) {
        return (FeedbackSessionPublishedRemindersAction) gaeSimulation.getAutomatedActionObject(getActionUri());
    }

}
