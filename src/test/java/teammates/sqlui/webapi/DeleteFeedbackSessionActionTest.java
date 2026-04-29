package teammates.sqlui.webapi;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteFeedbackSessionAction;

/**
 * SUT: {@link DeleteFeedbackSessionAction}.
 */
public class DeleteFeedbackSessionActionTest extends BaseActionTest<DeleteFeedbackSessionAction> {
    private String googleId = "user-googleId";
    private Course course;
    private FeedbackSession session;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        session = new FeedbackSession(
                "session-name",
                course,
                "creater_email@tm.tmt",
                null,
                Instant.parse("2020-01-01T00:00:00.000Z"),
                Instant.parse("2020-10-01T00:00:00.000Z"),
                Instant.parse("2020-01-01T00:00:00.000Z"),
                Instant.parse("2020-11-01T00:00:00.000Z"),
                null,
                false,
                false);
        loginAsInstructor(googleId);
    }

    @Test
    public void testExecute_sessionDoesNotExist_failSilently() {
        UUID sessionId = UUID.fromString("eeaed43c-9111-42da-bcbc-83f1a963b41b");
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, sessionId.toString(),
        };
        DeleteFeedbackSessionAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();
        assertEquals("The feedback session is deleted.", actionOutput.getMessage());
        verify(mockLogic, times(1))
                .deleteFeedbackSessionCascade(sessionId);
    }

    @Test
    public void testExecute_sessionExists_success() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
        };

        DeleteFeedbackSessionAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();
        assertEquals("The feedback session is deleted.", actionOutput.getMessage());
        verify(mockLogic, times(1))
                .deleteFeedbackSessionCascade(session.getId());
    }

    @Test
    public void testExecute_missingParameters_throwsInvalidHttpParameterException() {
        String[] params = {};

        verifyHttpParameterFailure(params);
    }

    @Test
    void testAccessControl() {
        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
        };

        verifyAccessibleWithModifySessionPrivilege(course, params);
        verifyInaccessibleWithoutModifySessionPrivilege(course, params);
    }
}
