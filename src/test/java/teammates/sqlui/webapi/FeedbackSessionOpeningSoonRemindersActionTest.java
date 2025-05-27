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
import teammates.ui.webapi.FeedbackSessionOpeningSoonRemindersAction;

/**
 * SUT: {@link FeedbackSessionOpeningSoonRemindersAction}.
 */
public class FeedbackSessionOpeningSoonRemindersActionTest
        extends BaseActionTest<FeedbackSessionOpeningSoonRemindersAction> {

    private FeedbackSession session;
    private FeedbackSession session2;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_SOON_REMINDERS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    public void setUp() {
        Mockito.reset(mockLogic, mockSqlEmailGenerator);

        session = mock(FeedbackSession.class);
        session2 = mock(FeedbackSession.class);
        EmailWrapper mockEmail = mock(EmailWrapper.class);
        EmailWrapper mockEmail2 = mock(EmailWrapper.class);

        when(mockSqlEmailGenerator.generateFeedbackSessionOpeningSoonEmails(session)).thenReturn(List.of(mockEmail));
        when(mockSqlEmailGenerator.generateFeedbackSessionOpeningSoonEmails(session2)).thenReturn(List.of(mockEmail2));
    }

    @Test
    void testExecute_allSessionsOpeningSoon_emailsSent() {
        when(mockLogic.getFeedbackSessionsOpeningWithinTimeLimit()).thenReturn(List.of(session, session2));

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionOpeningSoonRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsOpeningWithinTimeLimit();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(2));
            verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionOpeningSoonEmails(session);
            verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionOpeningSoonEmails(session2);
            verify(session, times(1)).setOpeningSoonEmailSent(true);
            verify(session2, times(1)).setOpeningSoonEmailSent(true);

            verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 2);
            verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator, session, session2);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_oneSessionOpeningSoon_emailsSent() {
        when(mockLogic.getFeedbackSessionsOpeningWithinTimeLimit()).thenReturn(List.of(session));

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionOpeningSoonRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsOpeningWithinTimeLimit();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(1));
            verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionOpeningSoonEmails(session);
            verify(session, times(1)).setOpeningSoonEmailSent(true);

            verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
            verifyNoMoreInteractions(mockLogic, mockSqlEmailGenerator, session, session2);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_noSessionsOpeningSoon_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsOpeningWithinTimeLimit()).thenReturn(List.of());

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionOpeningSoonRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsOpeningWithinTimeLimit();
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
