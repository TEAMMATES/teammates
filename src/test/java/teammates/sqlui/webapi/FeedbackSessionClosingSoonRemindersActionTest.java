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
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionClosingSoonRemindersAction;

/**
 * SUT: {@link FeedbackSessionClosingSoonRemindersAction}.
 */
public class FeedbackSessionClosingSoonRemindersActionTest
        extends BaseActionTest<FeedbackSessionClosingSoonRemindersAction> {

    private FeedbackSession session1;
    private FeedbackSession session2;
    private DeadlineExtension deadlineExtension1;
    private DeadlineExtension deadlineExtension2;
    private DeadlineExtension deadlineExtension3;
    private List<DeadlineExtension> deadlineExtensionsForSession1;
    private List<DeadlineExtension> deadlineExtensionsForSession2;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSING_SOON_REMINDERS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    public void setUp() {
        Mockito.reset(mockLogic, mockSqlEmailGenerator);

        session1 = mock(FeedbackSession.class);
        session2 = mock(FeedbackSession.class);
        deadlineExtension1 = mock(DeadlineExtension.class);
        deadlineExtension2 = mock(DeadlineExtension.class);
        deadlineExtension3 = mock(DeadlineExtension.class);

        EmailWrapper mockEmail = mock(EmailWrapper.class);
        EmailWrapper mockEmail2 = mock(EmailWrapper.class);
        EmailWrapper mockDeadlineEmail = mock(EmailWrapper.class);
        EmailWrapper mockDeadlineEmail2 = mock(EmailWrapper.class);

        when(mockSqlEmailGenerator.generateFeedbackSessionClosingSoonEmails(session1)).thenReturn(List.of(mockEmail));
        when(mockSqlEmailGenerator.generateFeedbackSessionClosingSoonEmails(session2)).thenReturn(List.of(mockEmail2));

        when(deadlineExtension1.getFeedbackSession()).thenReturn(session1);
        when(deadlineExtension2.getFeedbackSession()).thenReturn(session1);
        when(deadlineExtension3.getFeedbackSession()).thenReturn(session2);

        when(session1.isClosingSoonEmailEnabled()).thenReturn(true);
        when(session2.isClosingSoonEmailEnabled()).thenReturn(false);

        deadlineExtensionsForSession1 = List.of(deadlineExtension1, deadlineExtension2);
        deadlineExtensionsForSession2 = List.of(deadlineExtension3);
        when(mockSqlEmailGenerator.generateFeedbackSessionClosingWithExtensionEmails(
                session1, deadlineExtensionsForSession1)).thenReturn(List.of(mockDeadlineEmail));
        when(mockSqlEmailGenerator.generateFeedbackSessionClosingWithExtensionEmails(
                session2, deadlineExtensionsForSession2)).thenReturn(List.of(mockDeadlineEmail2));
    }

    @Test
    void testExecute_allSessionsClosingSoonAndNoDeadlineExtensionsClosingSoon_emailsSent() {
        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of(session1, session2));
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail()).thenReturn(List.of());

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionClosingSoonRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsClosingWithinTimeLimit();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(2));

            // Verify regular closing soon emails
            verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionClosingSoonEmails(session1);
            verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionClosingSoonEmails(session2);
            verify(session1, times(1)).setClosingSoonEmailSent(true);
            verify(session2, times(1)).setClosingSoonEmailSent(true);

            // Verify deadline extensions grouping
            verify(mockLogic, times(1)).getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();

            verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 2);
            verifyNoMoreInteractions(mockSqlEmailGenerator, session1, session2,
                    deadlineExtension1, deadlineExtension2, deadlineExtension3);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_oneSessionClosingSoonAndNoDeadlineExtensionsClosingSoon_emailsSent() {
        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of(session1));
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail()).thenReturn(List.of());

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionClosingSoonRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsClosingWithinTimeLimit();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(1));

            // Verify regular closing soon emails
            verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionClosingSoonEmails(session1);
            verify(session1, times(1)).setClosingSoonEmailSent(true);

            // Verify deadline extensions grouping
            verify(mockLogic, times(1)).getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();

            verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
            verifyNoMoreInteractions(mockSqlEmailGenerator, session1, session2,
                    deadlineExtension1, deadlineExtension2, deadlineExtension3);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_noSessionsClosingSoonAndNoDeadlineExtensionsClosingSoon_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of());
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail()).thenReturn(List.of());

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionClosingSoonRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsClosingWithinTimeLimit();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, never());
            verify(mockLogic, times(1)).getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();

            verifyNoTasksAdded();
            verifyNoMoreInteractions(mockSqlEmailGenerator, session1, session2,
                    deadlineExtension1, deadlineExtension2, deadlineExtension3);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_noSessionsClosingSoonAndAllDeadlineExtensionsWithEmailEnabledClosingSoon_emailsSent() {
        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of());
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail()).thenReturn(deadlineExtensionsForSession1);

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionClosingSoonRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsClosingWithinTimeLimit();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(1));

            // Verify deadline extensions grouping
            verify(mockLogic, times(1)).getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();
            verify(deadlineExtension1, times(2)).getFeedbackSession();
            verify(deadlineExtension2, times(1)).getFeedbackSession();

            // Verify deadline extension emails for session1 (isClosingSoonEmailEnabled() = true)
            verify(session1, times(1)).isClosingSoonEmailEnabled();
            verify(mockSqlEmailGenerator, times(1))
                    .generateFeedbackSessionClosingWithExtensionEmails(session1, deadlineExtensionsForSession1);
            verify(deadlineExtension1, times(1)).setClosingSoonEmailSent(true);
            verify(deadlineExtension2, times(1)).setClosingSoonEmailSent(true);

            verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
            verifyNoMoreInteractions(mockSqlEmailGenerator, session1, session2,
                    deadlineExtension1, deadlineExtension2, deadlineExtension3);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_noSessionsClosingSoonAndAllDeadlineExtensionsWithEmailDisabledClosingSoon_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of());
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail()).thenReturn(deadlineExtensionsForSession2);

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionClosingSoonRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsClosingWithinTimeLimit();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(1));

            // Verify deadline extensions grouping
            verify(mockLogic, times(1)).getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();
            verify(deadlineExtension3, times(2)).getFeedbackSession();

            // Verify no deadline extension emails for session2 (isClosingSoonEmailEnabled() = false)
            verify(session2, times(1)).isClosingSoonEmailEnabled();
            verify(mockSqlEmailGenerator, never())
                    .generateFeedbackSessionClosingWithExtensionEmails(session2, deadlineExtensionsForSession2);
            verify(deadlineExtension3, never()).setClosingSoonEmailSent(true);

            verifyNoTasksAdded();
            verifyNoMoreInteractions(mockSqlEmailGenerator, session1, session2,
                    deadlineExtension1, deadlineExtension2, deadlineExtension3);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_noSessionsClosingSoonAndAllDeadlineExtensionsClosingSoon_onlyEnabledEmailsSent() {
        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of());
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail())
                .thenReturn(List.of(deadlineExtension1, deadlineExtension2, deadlineExtension3));

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionClosingSoonRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsClosingWithinTimeLimit();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(2));

            // Verify deadline extensions grouping
            verify(mockLogic, times(1)).getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();
            verify(deadlineExtension1, times(2)).getFeedbackSession();
            verify(deadlineExtension2, times(1)).getFeedbackSession();
            verify(deadlineExtension3, times(2)).getFeedbackSession();

            // Verify deadline extension emails for session1 (isClosingSoonEmailEnabled() = true)
            verify(session1, times(1)).isClosingSoonEmailEnabled();
            verify(mockSqlEmailGenerator, times(1))
                    .generateFeedbackSessionClosingWithExtensionEmails(session1, deadlineExtensionsForSession1);
            verify(deadlineExtension1, times(1)).setClosingSoonEmailSent(true);
            verify(deadlineExtension2, times(1)).setClosingSoonEmailSent(true);

            // Verify no deadline extension emails for session2 (isClosingSoonEmailEnabled() = false)
            verify(session2, times(1)).isClosingSoonEmailEnabled();
            verify(mockSqlEmailGenerator, never())
                    .generateFeedbackSessionClosingWithExtensionEmails(session2, deadlineExtensionsForSession2);
            verify(deadlineExtension3, never()).setClosingSoonEmailSent(true);

            verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
            verifyNoMoreInteractions(mockSqlEmailGenerator, session1, session2,
                    deadlineExtension1, deadlineExtension2, deadlineExtension3);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_allSessionsClosingSoonAndAllDeadlineExtensionsWithEmailEnabledClosingSoon_emailsSent() {
        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of(session1, session2));
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail()).thenReturn(deadlineExtensionsForSession1);

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionClosingSoonRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsClosingWithinTimeLimit();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(3));

            // Verify regular closing soon emails
            verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionClosingSoonEmails(session1);
            verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionClosingSoonEmails(session2);
            verify(session1, times(1)).setClosingSoonEmailSent(true);
            verify(session2, times(1)).setClosingSoonEmailSent(true);

            // Verify deadline extensions grouping
            verify(mockLogic, times(1)).getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();
            verify(deadlineExtension1, times(2)).getFeedbackSession();
            verify(deadlineExtension2, times(1)).getFeedbackSession();

            // Verify deadline extension emails for session1 (isClosingSoonEmailEnabled() = true)
            verify(session1, times(1)).isClosingSoonEmailEnabled();
            verify(mockSqlEmailGenerator, times(1))
                    .generateFeedbackSessionClosingWithExtensionEmails(session1, deadlineExtensionsForSession1);
            verify(deadlineExtension1, times(1)).setClosingSoonEmailSent(true);
            verify(deadlineExtension2, times(1)).setClosingSoonEmailSent(true);

            verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 3);
            verifyNoMoreInteractions(mockSqlEmailGenerator, session1, session2,
                    deadlineExtension1, deadlineExtension2, deadlineExtension3);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testExecute_oneSessionClosingSoonAndAllDeadlineExtensionsClosingSoon_onlyEnabledEmailsSent() {
        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of(session1));
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail())
                .thenReturn(List.of(deadlineExtension1, deadlineExtension2, deadlineExtension3));

        try (MockedStatic<RequestTracer> mockRequestTracer = mockStatic(RequestTracer.class)) {
            FeedbackSessionClosingSoonRemindersAction action = getAction();
            MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

            verify(mockLogic, times(1)).getFeedbackSessionsClosingWithinTimeLimit();
            mockRequestTracer.verify(RequestTracer::checkRemainingTime, times(3));

            // Verify regular closing soon emails (only session1)
            verify(mockSqlEmailGenerator, times(1)).generateFeedbackSessionClosingSoonEmails(session1);
            verify(session1, times(1)).setClosingSoonEmailSent(true);

            // Verify deadline extensions grouping
            verify(mockLogic, times(1)).getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();
            verify(deadlineExtension1, times(2)).getFeedbackSession();
            verify(deadlineExtension2, times(1)).getFeedbackSession();
            verify(deadlineExtension3, times(2)).getFeedbackSession();

            // Verify deadline extension emails for session1 (isClosingSoonEmailEnabled() = true)
            verify(session1, times(1)).isClosingSoonEmailEnabled();
            verify(mockSqlEmailGenerator, times(1))
                    .generateFeedbackSessionClosingWithExtensionEmails(session1, deadlineExtensionsForSession1);
            verify(deadlineExtension1, times(1)).setClosingSoonEmailSent(true);
            verify(deadlineExtension2, times(1)).setClosingSoonEmailSent(true);

            // Verify no deadline extension emails for session2 (isClosingSoonEmailEnabled() = false)
            verify(session2, times(1)).isClosingSoonEmailEnabled();
            verify(mockSqlEmailGenerator, never())
                    .generateFeedbackSessionClosingWithExtensionEmails(session2, deadlineExtensionsForSession2);
            verify(deadlineExtension3, never()).setClosingSoonEmailSent(true);

            verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 2);
            verifyNoMoreInteractions(mockSqlEmailGenerator, session1, session2,
                    deadlineExtension1, deadlineExtension2, deadlineExtension3);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
    }
}
