package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
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
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionClosedRemindersAction;

/**
 * SUT: {@link FeedbackSessionClosedRemindersAction}.
 */
public class FeedbackSessionClosedRemindersActionTest extends BaseActionTest<FeedbackSessionClosedRemindersAction> {

    private FeedbackSession mockSession;
    private FeedbackSession mockSession2;
    private EmailWrapper mockEmail;
    private EmailWrapper mockEmail2;

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
        Mockito.reset(mockLogic, mockSqlEmailGenerator, mockTaskQueuer);

        mockSession = mock(FeedbackSession.class);
        mockSession2 = mock(FeedbackSession.class);
        mockEmail = mock(EmailWrapper.class);
        mockEmail2 = mock(EmailWrapper.class);

        when(mockSqlEmailGenerator.generateFeedbackSessionClosedEmails(mockSession)).thenReturn(List.of(mockEmail));
        when(mockSqlEmailGenerator.generateFeedbackSessionClosedEmails(mockSession2)).thenReturn(List.of(mockEmail2));
    }

    @Test
    void testExecute_allSessionsClosed_emailsSent() {
        when(mockLogic.getFeedbackSessionsClosedWithinThePastHour()).thenReturn(List.of(mockSession, mockSession2));

        FeedbackSessionClosedRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockTaskQueuer, times(1)).scheduleEmailsForSending(List.of(mockEmail));
        verify(mockTaskQueuer, times(1)).scheduleEmailsForSending(List.of(mockEmail2));
        verify(mockSession, times(1)).setClosedEmailSent(true);
        verify(mockSession2, times(1)).setClosedEmailSent(true);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_oneSessionClosed_emailsSent() {
        when(mockLogic.getFeedbackSessionsClosedWithinThePastHour()).thenReturn(List.of(mockSession));

        FeedbackSessionClosedRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockTaskQueuer, times(1)).scheduleEmailsForSending(List.of(mockEmail));
        verify(mockTaskQueuer, never()).scheduleEmailsForSending(List.of(mockEmail2));
        verify(mockSession, times(1)).setClosedEmailSent(true);
        verify(mockSession2, never()).setClosedEmailSent(true);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_noSessionsClosed_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsClosedWithinThePastHour()).thenReturn(List.of());

        FeedbackSessionClosedRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockTaskQueuer, never()).scheduleEmailsForSending(anyList());
        verify(mockSession, never()).setClosedEmailSent(true);
        verify(mockSession2, never()).setClosedEmailSent(true);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_emailSendingFails_logsError() {
        when(mockLogic.getFeedbackSessionsClosedWithinThePastHour()).thenReturn(List.of(mockSession, mockSession2));
        doThrow(new RuntimeException("Email sending failed")).when(mockTaskQueuer).scheduleEmailsForSending(anyList());

        FeedbackSessionClosedRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockTaskQueuer, times(1)).scheduleEmailsForSending(List.of(mockEmail));
        verify(mockTaskQueuer, times(1)).scheduleEmailsForSending(List.of(mockEmail2));
        verify(mockSession, never()).setClosedEmailSent(true);
        verify(mockSession2, never()).setClosedEmailSent(true);
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
