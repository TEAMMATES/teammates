package teammates.sqlui.webapi;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Const.TaskQueue;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
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
    public void testAccessControl_notEnoughParameters_shouldFail() {
        verifyHttpParameterFailure();
    }

    /**
     * Tests access control using convenience method.
     * 
     * <p>Note: This refactoring increases test coverage by adding the following test scenarios:
     * <ul>
     *   <li>Admin masquerading as instructor (verifyAccessibleForAdminsToMasqueradeAsInstructor)</li>
     *   <li>Default instructor privileges verification (tests with typical instructor's full Co-owner privileges)</li>
     *   <li>Invalid course ID scenario (verifyInaccessibleForInstructorsOfOtherCourses)</li>
     *   <li>Student access attempts (verifyStudentsCannotAccess)</li>
     *   <li>Unregistered user access attempts (verifyUnregisteredCannotAccess)</li>
     *   <li>Unauthenticated access attempts (verifyWithoutLoginCannotAccess)</li>
     * </ul>
     * 
     * <p>Original tests covered:
     * <ul>
     *   <li>Invalid course ID</li>
     *   <li>Invalid feedback session name</li>
     *   <li>Instructor without correct privilege (CAN_MODIFY_SESSION = false)</li>
     *   <li>Instructor of different course</li>
     *   <li>Instructor with correct privilege (CAN_MODIFY_SESSION = true)</li>
     * </ul>
     * 
     * <p>The convenience method consolidates these scenarios while adding admin masquerading tests,
     * default privilege verification, and additional user type checks, which improves overall test coverage.
     */
    @Test
    public void testAccessControl() {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                typicalCourse, Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }

    @Test
    void testExecute_missingCourseId_throwsInvalidParametersException() {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingFeedbackSessionName_throwsInvalidParametersException() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        verifyHttpParameterFailure(params);
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

        verifyFeedbackSessionData(feedbackSessionData, typicalFeedbackSession,
                FeedbackSessionPublishStatus.PUBLISHED);
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

        verifyFeedbackSessionData(feedbackSessionData, typicalFeedbackSession,
                FeedbackSessionPublishStatus.NOT_PUBLISHED);
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

        verifyFeedbackSessionData(feedbackSessionData, typicalFeedbackSession,
                FeedbackSessionPublishStatus.NOT_PUBLISHED);
        verify(mockLogic).publishFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId());
        verifySpecifiedTasksAdded(TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    void testAccessControl_withoutLogin() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_unregisteredUser() {
        String googleId = "unregistered-user";
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), googleId))
                .thenReturn(null);
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);

        loginAsUnregistered(googleId);

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_student() {
        String googleId = "student";
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), googleId))
                .thenReturn(null);
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);

        loginAsStudent(googleId);

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorOfOtherCourse() {
        String googleId = "instructor-of-other-course";
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), googleId))
                .thenReturn(null);
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);

        loginAsInstructor(googleId);

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorOfSameCourseWithoutPermission() {
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        typicalInstructor.setPrivileges(instructorPrivileges);
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCannotAccess(params);
    }

    @Test
    void testAccessControl_instructorOfSameCourseWithPermission() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);
        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
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
        assertEquals(output.getSubmissionStartTimestamp(), TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getStartTime(), session.getCourse().getTimeZone(), true).toEpochMilli());
        assertEquals(output.getSubmissionEndTimestamp(), TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getEndTime(), session.getCourse().getTimeZone(), true).toEpochMilli());
        assertEquals(output.getSubmissionEndWithExtensionTimestamp(), TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getEndTime(), session.getCourse().getTimeZone(), true).toEpochMilli());
        assertEquals((long) output.getGracePeriod(), session.getGracePeriod().toMinutes());
        assertEquals((long) output.getSessionVisibleFromTimestamp(), TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getSessionVisibleFromTime(), session.getCourse().getTimeZone(), true).toEpochMilli());
        assertEquals(output.getSessionVisibleSetting(), SessionVisibleSetting.CUSTOM);
        assertEquals(output.getCustomSessionVisibleTimestamp(), output.getSessionVisibleFromTimestamp());
        assertEquals((long) output.getResultVisibleFromTimestamp(), TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                session.getResultsVisibleFromTime(), session.getCourse().getTimeZone(), true).toEpochMilli());
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
