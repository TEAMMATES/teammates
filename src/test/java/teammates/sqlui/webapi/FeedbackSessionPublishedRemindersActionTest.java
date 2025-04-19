package teammates.sqlui.webapi;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.RequestTracer;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionPublishedRemindersAction;

/**
 * SUT: {@link FeedbackSessionPublishedRemindersAction}.
 */
public class FeedbackSessionPublishedRemindersActionTest extends BaseActionTest<FeedbackSessionPublishedRemindersAction> {

    private FeedbackSession session;
    private FeedbackSession session2;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    public void setUp() {
        Mockito.reset(mockLogic);

        Course course = getTypicalCourse();
        session = getTypicalFeedbackSessionForCourse(course);
        session.setPublishedEmailEnabled(true);
        session2 = getTypicalFeedbackSessionForCourse(course);
        session2.setName("test-feedbacksession2");
        session2.setPublishedEmailEnabled(true);
    }

    @Test
    void testExecute_allSessionsPublished_emailsSent() {
        when(mockLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent())
                .thenReturn(List.of(session, session2));

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionPublishedRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(2));
            verifySpecifiedTasksAdded(Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 2);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_oneSessionPublished_emailsSent() {
        when(mockLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent()).thenReturn(List.of(session));

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionPublishedRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(1));
            verifySpecifiedTasksAdded(Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 1);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_noSessionsPublished_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent()).thenReturn(List.of());

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionPublishedRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, never());
            verifyNoTasksAdded();
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
    }
}
