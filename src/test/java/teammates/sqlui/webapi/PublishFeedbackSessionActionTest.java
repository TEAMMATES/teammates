package teammates.sqlui.webapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidFeedbackSessionStateException;
import teammates.common.util.Const;
import teammates.common.util.Const.TaskQueue;
import teammates.common.util.EmailWrapper;
import teammates.logic.entity.Course;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionPublishStatus;
import teammates.ui.output.FeedbackSessionSubmissionStatus;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.PublishFeedbackSessionAction;

/**
 * SUT: {@link PublishFeedbackSessionAction}.
 */
public class PublishFeedbackSessionActionTest extends BaseActionTest<PublishFeedbackSessionAction> {

    private Instructor typicalInstructor;
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
        reset(mockLogic, mockEmailGenerator);

        typicalInstructor = getTypicalInstructor();
        typicalCourse = getTypicalCourse();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
        typicalFeedbackSession.setCreatedAt(Instant.now());
    }

    @AfterMethod
    void tearDown() {
        reset(mockLogic);
        mockTaskQueuer.clearTasks();
        logoutUser();
    }

    @Test
    void testExecute_missingParams_throwsInvalidParametersException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_unpublishedFeedbackSessionWithEmailDisabled_succeedsWithNoTasksAdded()
            throws EntityDoesNotExistException, InvalidFeedbackSessionStateException {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, typicalFeedbackSession.getId().toString(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.publishFeedbackSession(typicalFeedbackSession.getId()))
                .thenReturn(typicalFeedbackSession);

        PublishFeedbackSessionAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        FeedbackSessionData feedbackSessionData = (FeedbackSessionData) result.getOutput();

        verifyFeedbackSessionData(feedbackSessionData, typicalFeedbackSession,
                FeedbackSessionPublishStatus.NOT_PUBLISHED);
        verify(mockLogic).publishFeedbackSession(typicalFeedbackSession.getId());
        verifyNoTasksAdded();
    }

    @Test
    void testExecute_unpublishedFeedbackSessionWithEmailEnabled_succeedsWithTasksAdded()
            throws EntityDoesNotExistException, InvalidFeedbackSessionStateException {
        typicalFeedbackSession.setPublishedEmailEnabled(true);
        EmailWrapper mockEmail = mock(EmailWrapper.class);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, typicalFeedbackSession.getId().toString(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.publishFeedbackSession(typicalFeedbackSession.getId()))
                .thenReturn(typicalFeedbackSession);
        when(mockEmailGenerator.generateFeedbackSessionPublishedEmails(typicalFeedbackSession))
                .thenReturn(List.of(mockEmail));

        PublishFeedbackSessionAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        FeedbackSessionData feedbackSessionData = (FeedbackSessionData) result.getOutput();

        verifyFeedbackSessionData(feedbackSessionData, typicalFeedbackSession,
                FeedbackSessionPublishStatus.NOT_PUBLISHED);
        verify(mockLogic).publishFeedbackSession(typicalFeedbackSession.getId());
        verifySpecifiedTasksAdded(TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    void testCheckSpecificAccessControl_withoutLogin_throwsUnauthorizedAccessException() {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, typicalFeedbackSession.getId().toString(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_unregisteredUser_throwsUnauthorizedAccessException() {
        String googleId = "unregistered-user";
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, typicalFeedbackSession.getId().toString(),
        };

        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), googleId))
                .thenReturn(null);
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getId()))
                .thenReturn(typicalFeedbackSession);

        loginAsUnregistered(googleId);

        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_student_throwsUnauthorizedAccessException() {
        String googleId = "student";
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, typicalFeedbackSession.getId().toString(),
        };

        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), googleId))
                .thenReturn(null);
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getId()))
                .thenReturn(typicalFeedbackSession);

        loginAsStudent(googleId);

        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_instructorOfOtherCourse_throwsUnauthorizedAccessException() {
        String googleId = "instructor-of-other-course";
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, typicalFeedbackSession.getId().toString(),
        };

        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), googleId))
                .thenReturn(null);
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getId()))
                .thenReturn(typicalFeedbackSession);

        loginAsInstructor(googleId);

        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_instructorOfSameCourseWithoutPermission_throwsUnauthorizedAccessException() {
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        typicalInstructor.setPrivileges(instructorPrivileges);
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, typicalFeedbackSession.getId().toString(),
        };

        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getId()))
                .thenReturn(typicalFeedbackSession);

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_instructorOfSameCourseWithPermission_canAccess() {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, typicalFeedbackSession.getId().toString(),
        };

        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getId()))
                .thenReturn(typicalFeedbackSession);

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCanAccess(params);
    }

    private void verifyFeedbackSessionData(FeedbackSessionData output, FeedbackSession session,
            FeedbackSessionPublishStatus originalPublishStatus) {
        assertEquals(output.getFeedbackSessionId(), session.getId());
        assertEquals(output.getCourseId(), session.getCourseId());
        assertEquals(output.getTimeZone(), session.getCourse().getTimeZone());
        assertEquals(output.getFeedbackSessionName(), session.getName());
        assertEquals(output.getInstructions(), session.getInstructions());
        assertEquals(output.getSubmissionStartTimestamp(), session.getStartTime().toEpochMilli());
        assertEquals(output.getSubmissionEndTimestamp(), session.getEndTime().toEpochMilli());
        assertEquals(output.getSubmissionEndWithExtensionTimestamp(), session.getEndTime().toEpochMilli());
        assertEquals((long) output.getGracePeriod(), session.getGracePeriod().toMinutes());
        assertEquals((long) output.getSessionVisibleFromTimestamp(), session.getSessionVisibleFromTime().toEpochMilli());
        assertEquals(output.getSessionVisibleSetting(), SessionVisibleSetting.CUSTOM);
        assertEquals(output.getCustomSessionVisibleTimestamp(), output.getSessionVisibleFromTimestamp());
        assertEquals((long) output.getResultVisibleFromTimestamp(), session.getResultsVisibleFromTime().toEpochMilli());
        assertEquals(output.getResponseVisibleSetting(), ResponseVisibleSetting.CUSTOM);
        assertEquals(output.getCustomResponseVisibleTimestamp(), output.getResultVisibleFromTimestamp());
        assertEquals(output.getSubmissionStatus(), FeedbackSessionSubmissionStatus.NOT_VISIBLE);
        assertEquals(output.getPublishStatus(), originalPublishStatus);
        assertEquals(output.getIsClosingSoonEmailEnabled(), session.isClosingSoonEmailEnabled());
        assertEquals(output.getIsPublishedEmailEnabled(), session.isPublishedEmailEnabled());
        assertEquals(output.getCreatedAtTimestamp(), session.getCreatedAt().toEpochMilli());
        assertEquals(output.getDeletedAtTimestamp(), session.getDeletedAt() == null
                ? null
                : session.getDeletedAt().toEpochMilli());
    }
}
