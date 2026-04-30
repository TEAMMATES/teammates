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
    void setUp() {
        Mockito.reset(mockLogic, mockEmailGenerator);

        session = mock(FeedbackSession.class);
        session2 = mock(FeedbackSession.class);
        EmailWrapper mockEmail = mock(EmailWrapper.class);
        EmailWrapper mockEmail2 = mock(EmailWrapper.class);

        when(mockEmailGenerator.generateFeedbackSessionPublishedEmails(session)).thenReturn(List.of(mockEmail));
        when(mockEmailGenerator.generateFeedbackSessionPublishedEmails(session2)).thenReturn(List.of(mockEmail2));
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
            verify(mockEmailGenerator, times(1)).generateFeedbackSessionPublishedEmails(session);
            verify(mockEmailGenerator, times(1)).generateFeedbackSessionPublishedEmails(session2);
            verify(session, times(1)).setPublishedEmailSent(true);
            verify(session2, times(1)).setPublishedEmailSent(true);
            verify(mockLogic, times(1)).adjustFeedbackSessionEmailStatusAfterUpdate(session);
            verify(mockLogic, times(1)).adjustFeedbackSessionEmailStatusAfterUpdate(session2);
            verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 2);
            verifyNoMoreInteractions(mockLogic, mockEmailGenerator, session, session2);
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
            verify(mockEmailGenerator, times(1)).generateFeedbackSessionPublishedEmails(session);
            verify(session, times(1)).setPublishedEmailSent(true);
            verify(mockLogic, times(1)).adjustFeedbackSessionEmailStatusAfterUpdate(session);
            verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
            verifyNoMoreInteractions(mockLogic, mockEmailGenerator, session, session2);
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
            verifyNoMoreInteractions(mockLogic, mockEmailGenerator, session, session2);
            assertEquals("Successful", actionOutput.getMessage());
        }
    }

    @Test
    void testAccessControl() {
        verifyOnlyAdminsOrAutomatedServicesCanAccess();
    }
}
