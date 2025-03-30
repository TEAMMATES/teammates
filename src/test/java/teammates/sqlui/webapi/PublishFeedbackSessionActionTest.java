package teammates.sqlui.webapi;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Const.TaskQueue;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.PublishFeedbackSessionAction;

/**
 * SUT: {@link PublishFeedbackSessionAction}.
 */
public class PublishFeedbackSessionActionTest extends BaseActionTest<PublishFeedbackSessionAction> {

    private Course typicalCourse;
    private FeedbackSession typicalFeedbackSession;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_PUBLISH;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        typicalCourse = getTypicalCourse();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
        typicalFeedbackSession.setCreatedAt(Instant.now());
    }

    @AfterMethod
    void tearDown() {
        reset(mockLogic);
        mockTaskQueuer.clearTasks();
    }

    @Test
    void testExecute_publishedFeedbackSession_returnsEarly()
            throws EntityDoesNotExistException, InvalidParametersException {
        typicalFeedbackSession.setResultsVisibleFromTime(Instant.now());
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);

        PublishFeedbackSessionAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        FeedbackSessionData feedbackSessionData = (FeedbackSessionData) result.getOutput();

        verifyPublishedFeedbackSession(feedbackSessionData, typicalFeedbackSession);
        verify(mockLogic, never()).publishFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId());
        verifyNoTasksAdded();
    }

    @Test
    void testExecute_unpublishedFeedbackSessionWithEmailDisabled_succeedsWithNoTasksAdded()
            throws EntityDoesNotExistException, InvalidParametersException {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.publishFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);

        PublishFeedbackSessionAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        FeedbackSessionData feedbackSessionData = (FeedbackSessionData) result.getOutput();

        verifyPublishedFeedbackSession(feedbackSessionData, typicalFeedbackSession);
        verify(mockLogic).publishFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId());
        verifyNoTasksAdded();
    }

    @Test
    void testExecute_unpublishedFeedbackSessionWithEmailEnabled_succeedsWithTasksAdded()
            throws EntityDoesNotExistException, InvalidParametersException {
        typicalFeedbackSession.setPublishedEmailEnabled(true);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.publishFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);

        PublishFeedbackSessionAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        FeedbackSessionData feedbackSessionData = (FeedbackSessionData) result.getOutput();

        verifyPublishedFeedbackSession(feedbackSessionData, typicalFeedbackSession);
        verify(mockLogic).publishFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId());
        verifySpecifiedTasksAdded(TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 1);
    }

    private void verifyPublishedFeedbackSession(FeedbackSessionData output, FeedbackSession session) {
        assertEquals(output.getFeedbackSessionId(), session.getId());
        assertEquals(output.getCourseId(), session.getCourseId());
        assertEquals(output.getTimeZone(), session.getCourse().getTimeZone());
        assertEquals(output.getFeedbackSessionName(), session.getName());
        assertEquals(output.getInstructions(), session.getInstructions());
        assertEquals(output.getSubmissionStartTimestamp(), TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getStartTime(), session.getCourse().getTimeZone(), true).toEpochMilli());
        assertEquals(output.getSubmissionEndTimestamp(), TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getEndTime(), session.getCourse().getTimeZone(), true).toEpochMilli());
        assertEquals(output.getSubmissionEndWithExtensionTimestamp(), TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getEndTime(), session.getCourse().getTimeZone(), true).toEpochMilli());
        assertEquals((long) output.getGracePeriod(), session.getGracePeriod().toMinutes());
        // more
    }
}
