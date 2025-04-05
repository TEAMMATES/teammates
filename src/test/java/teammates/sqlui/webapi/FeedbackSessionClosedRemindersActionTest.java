package teammates.sqlui.webapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.RequestTracer;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionClosedRemindersAction;

/**
 * SUT: {@link FeedbackSessionClosedRemindersAction}.
 */
public class FeedbackSessionClosedRemindersActionTest extends BaseActionTest<FeedbackSessionClosedRemindersAction> {

    private FeedbackSession session;
    private FeedbackSession session2;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        Mockito.reset(mockLogic, mockSqlEmailGenerator);

        session = mock(FeedbackSession.class);
        session2 = mock(FeedbackSession.class);
        EmailWrapper mockEmail = mock(EmailWrapper.class);
        EmailWrapper mockEmail2 = mock(EmailWrapper.class);

        when(mockSqlEmailGenerator.generateFeedbackSessionClosedEmails(session)).thenReturn(List.of(mockEmail));
        when(mockSqlEmailGenerator.generateFeedbackSessionClosedEmails(session2)).thenReturn(List.of(mockEmail2));
    }

    @Test
    void testExecute_allSessionsClosed_emailsSent() {
        when(mockLogic.getFeedbackSessionsClosedWithinThePastHour()).thenReturn(List.of(session, session2));

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionClosedRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsClosedWithinThePastHour();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(2));
            verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionClosedEmails(session);
            verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionClosedEmails(session2);
            verify(session, times(1)).setClosedEmailSent(true);
            verify(session2, times(1)).setClosedEmailSent(true);

            verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 2);
            verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator, session, session2);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_oneSessionClosed_emailsSent() {
        when(mockLogic.getFeedbackSessionsClosedWithinThePastHour()).thenReturn(List.of(session));

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionClosedRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsClosedWithinThePastHour();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(1));
            verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionClosedEmails(session);
            verify(session, times(1)).setClosedEmailSent(true);

            verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
            verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator, session, session2);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_noSessionsClosed_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsClosedWithinThePastHour()).thenReturn(List.of());

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionClosedRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsClosedWithinThePastHour();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, never());

            verifyNoTasksAdded();
            verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator, session, session2);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
    }
}
