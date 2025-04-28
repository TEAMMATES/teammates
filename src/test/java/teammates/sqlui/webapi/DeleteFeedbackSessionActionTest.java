package teammates.sqlui.webapi;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

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
                false,
                false);
        loginAsInstructor(googleId);
    }

    @Test
    public void testExecute_sessionDoesNotExist_failSilently() {
        String sessionName = "incorrect-name";
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, sessionName,
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        DeleteFeedbackSessionAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();
        assertEquals("The feedback session is deleted.", actionOutput.getMessage());
        verify(mockLogic, times(1))
                .deleteFeedbackSessionCascade(sessionName, course.getId());
    }

    @Test
    public void testExecute_sessionExists_success() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
        };

        DeleteFeedbackSessionAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();
        assertEquals("The feedback session is deleted.", actionOutput.getMessage());
        verify(mockLogic, times(1))
                .deleteFeedbackSessionCascade(session.getName(), session.getCourseId());
    }

    @Test
    public void testExecute_missingParameters_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "session-name",
        };

        verifyHttpParameterFailure(params);

        String[] params2 = {
                Const.ParamsNames.COURSE_ID, "course-id",
        };

        verifyHttpParameterFailure(params2);

        String[] params3 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, null,
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyHttpParameterFailure(params3);

        String[] params4 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, null,
        };

        verifyHttpParameterFailure(params4);

        String[] params5 = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, null,
                Const.ParamsNames.COURSE_ID, null,
        };

        verifyHttpParameterFailure(params5);
    }

    @Test
    void testAccessControl() {
        when(mockLogic.getFeedbackSessionFromRecycleBin(session.getName(), course.getId())).thenReturn(session);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
        };

        verifyAccessibleWithModifySessionPrivilege(course, params);
        verifyInaccessibleWithoutModifySessionPrivilege(course, params);
    }
}
