package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionPublishedRemindersAction;

/**
 * SUT: {@link FeedbackSessionPublishedRemindersAction}.
 */
public class FeedbackSessionPublishedRemindersActionTest extends BaseActionTest<FeedbackSessionPublishedRemindersAction> {

    private Course course;
    private FeedbackSession session;

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
        course = new Course("course-id", "Course Name", Const.DEFAULT_TIME_ZONE, "institute");
        session = new FeedbackSession(
                "Session Name",
                course,
                "instructor1email@tm.tmt",
                "Instructions",
                Instant.now().minusSeconds(7200), // Start time 2 hours ago
                Instant.now().minusSeconds(3600), // End time 1 hour ago
                Instant.now().minusSeconds(7200), // Visible time 2 hours ago
                Instant.now().plusSeconds(1800), // Results visible time in 30 minutes
                Duration.ofMinutes(30),
                false, false, true
        );
    }

    @Test
    void testExecute_noPublishedSessions_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent()).thenReturn(List.of());

        FeedbackSessionPublishedRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_publishedSession_emailsSent() {
        when(mockLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent()).thenReturn(List.of(session));

        FeedbackSessionPublishedRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifySpecifiedTasksAdded(Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 1);
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_sessionPublishedWithEmailsAlreadySent_noEmailsSent() {
        session.setPublishedEmailSent(true);
        when(mockLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent()).thenReturn(List.of());

        FeedbackSessionPublishedRemindersAction action = getAction();
        MessageOutput output = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    void testExecute_publishedReminderDisabled_noEmailsSent() {
        session.setPublishedEmailEnabled(false);
        when(mockLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent()).thenReturn(List.of());

        FeedbackSessionPublishedRemindersAction action = getAction();
        MessageOutput output = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    void testExecute_unpublishedSession_noEmailsSent() throws InvalidParametersException, EntityDoesNotExistException {
        when(mockLogic.unpublishFeedbackSession(session.getName(), course.getId())).thenReturn(session);

        FeedbackSessionPublishedRemindersAction action = getAction();
        MessageOutput output = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", output.getMessage());
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
