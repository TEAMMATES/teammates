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
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionOpeningRemindersAction;

/**
 * SUT: {@link FeedbackSessionOpeningRemindersAction}.
 */
public class FeedbackSessionOpeningRemindersActionTest extends BaseActionTest<FeedbackSessionOpeningRemindersAction> {

    private FeedbackSession mockSession;
    private FeedbackSession mockSession2;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS;
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

        when(mockSqlEmailGenerator.generateFeedbackSessionOpeningEmails(mockSession)).thenReturn(List.of(mockEmail));
        when(mockSqlEmailGenerator.generateFeedbackSessionOpeningEmails(mockSession2)).thenReturn(List.of(mockEmail2));
    }

    @Test
    void testExecute_allSessionsOpening_emailsSent() {
        when(mockLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent()).thenReturn(List.of(mockSession, mockSession2));

        FeedbackSessionOpeningRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 2);
        verify(mockSession, times(1)).setOpenEmailSent(true);
        verify(mockSession2, times(1)).setOpenEmailSent(true);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_oneSessionOpening_emailsSent() {
        when(mockLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent()).thenReturn(List.of(mockSession));

        FeedbackSessionOpeningRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
        verify(mockSession, times(1)).setOpenEmailSent(true);
        verify(mockSession2, never()).setOpenEmailSent(true);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_noSessionsOpening_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent()).thenReturn(List.of());

        FeedbackSessionOpeningRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        verify(mockSession, never()).setOpenEmailSent(true);
        verify(mockSession2, never()).setOpenEmailSent(true);
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
