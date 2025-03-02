package teammates.sqlui.webapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionClosingRemindersAction;

/**
 * SUT: {@link FeedbackSessionClosingRemindersAction}.
 */
public class FeedbackSessionClosingRemindersActionTest extends BaseActionTest<FeedbackSessionClosingRemindersAction> {

    private FeedbackSession mockSession;
    private FeedbackSession mockSession2;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    public void setUp() {
        Mockito.reset(mockLogic, mockSqlEmailGenerator);

        mockSession = mock(FeedbackSession.class);
        mockSession2 = mock(FeedbackSession.class);
        EmailWrapper mockEmail = mock(EmailWrapper.class);
        EmailWrapper mockEmail2 = mock(EmailWrapper.class);

        when(mockSession.isClosingEmailEnabled()).thenReturn(true);
        when(mockSession2.isClosingEmailEnabled()).thenReturn(true);
        when(mockSqlEmailGenerator.generateFeedbackSessionClosingEmails(mockSession)).thenReturn(List.of(mockEmail));
        when(mockSqlEmailGenerator.generateFeedbackSessionClosingEmails(mockSession2)).thenReturn(List.of(mockEmail2));
    }

    @Test
    void testExecute_allSessionsClosingWithNoDeadlineExtensions_emailsSent() {
        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of(mockSession, mockSession2));
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingEmail()).thenReturn(List.of());

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        // Verify emails are scheduled for sending
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 2);
        verify(mockSession, times(1)).setClosingSoonEmailSent(true);
        verify(mockSession2, times(1)).setClosingSoonEmailSent(true);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_oneSessionClosingWithNoDeadlineExtensions_emailsSent() {
        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of(mockSession));
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingEmail()).thenReturn(List.of());

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
        verify(mockSession, times(1)).setClosingSoonEmailSent(true);
        verify(mockSession2, never()).setClosingSoonEmailSent(true);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_oneSessionClosingWithDeadlineExtension_emailsSent() {
        DeadlineExtension deadlineExtension = mock(DeadlineExtension.class);
        when(deadlineExtension.getFeedbackSession()).thenReturn(mockSession);

        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of(mockSession));
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingEmail()).thenReturn(List.of(deadlineExtension));

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
        verify(mockSession, times(1)).setClosingSoonEmailSent(true);
        verify(mockSession2, never()).setClosingSoonEmailSent(true);
        verify(deadlineExtension, times(1)).setClosingSoonEmailSent(true);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_sessionClosingEmailDisabled_noEmailsSentForDeadlineExtension() {
        DeadlineExtension deadlineExtension = mock(DeadlineExtension.class);
        when(deadlineExtension.getFeedbackSession()).thenReturn(mockSession);
        when(mockSession.isClosingEmailEnabled()).thenReturn(false);

        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of(mockSession));
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingEmail()).thenReturn(List.of(deadlineExtension));

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
        verify(mockSession, times(1)).setClosingSoonEmailSent(true);
        verify(mockSession2, never()).setClosingSoonEmailSent(true);
        // Setting isClosingEmailEnabled to false would only disable the deadline extension email
        verify(deadlineExtension, never()).setClosingSoonEmailSent(true);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_allSessionsClosingWithDeadlineExtensions_emailsSent() {
        DeadlineExtension deadlineExtension = mock(DeadlineExtension.class);
        DeadlineExtension deadlineExtension2 = mock(DeadlineExtension.class);
        when(deadlineExtension.getFeedbackSession()).thenReturn(mockSession);
        when(deadlineExtension2.getFeedbackSession()).thenReturn(mockSession2);

        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of(mockSession, mockSession2));
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingEmail())
                .thenReturn(List.of(deadlineExtension, deadlineExtension2));

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 2);
        verify(mockSession, times(1)).setClosingSoonEmailSent(true);
        verify(mockSession2, times(1)).setClosingSoonEmailSent(true);
        verify(deadlineExtension, times(1)).setClosingSoonEmailSent(true);
        verify(deadlineExtension2, times(1)).setClosingSoonEmailSent(true);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_noSessionsClosing_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of());

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        verify(mockSession, never()).setClosingSoonEmailSent(true);
        verify(mockSession2, never()).setClosingSoonEmailSent(true);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_sessionNotClosingButWithDeadlineExtension_noEmailsSent() {
        DeadlineExtension deadlineExtension = mock(DeadlineExtension.class);
        when(deadlineExtension.getFeedbackSession()).thenReturn(mockSession);

        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of());
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingEmail()).thenReturn(List.of(deadlineExtension));

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        verify(mockSession, never()).setClosingSoonEmailSent(true);
        verify(mockSession2, never()).setClosingSoonEmailSent(true);
        verify(deadlineExtension, times(1)).setClosingSoonEmailSent(true);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    void testSpecificAccessControl_instructor_cannotAccess() {
        loginAsInstructor("instructor-googleId");
        verifyCannotAccess();
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        loginAsStudent("student-googleId");
        verifyCannotAccess();
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }
}
