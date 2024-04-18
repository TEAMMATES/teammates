package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;
import teammates.ui.webapi.UpdateFeedbackSessionLogsAction;

/**
 * SUT: {@link UpdateFeedbackSessionLogsAction}.
 */
public class UpdateFeedbackSessionLogsActionTest
        extends BaseActionTest<UpdateFeedbackSessionLogsAction> {

    static final long COLLECTION_TIME_PERIOD = Const.STUDENT_ACTIVITY_LOGS_UPDATE_INTERVAL.toMinutes();
    static final long SPAM_FILTER = Const.STUDENT_ACTIVITY_LOGS_FILTER_WINDOW.toMillis();

    Student student1;
    Student student2;

    Course course1;
    Course course2;

    FeedbackSession session1InCourse1;
    FeedbackSession session2InCourse1;
    FeedbackSession session1InCourse2;

    Instant endTime;
    Instant startTime;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_SESSION_LOGS_PROCESSING;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        endTime = TimeHelper.getInstantNearestQuarterHourBefore(Instant.now());
        startTime = endTime.minus(COLLECTION_TIME_PERIOD, ChronoUnit.MINUTES);

        course1 = getTypicalCourse();
        course1.setId("course1");

        course2 = getTypicalCourse();
        course2.setId("course2");

        student1 = getTypicalStudent();
        student1.setEmail("student1@teammates.tmt");
        student1.setId(UUID.randomUUID());

        student2 = getTypicalStudent();
        student2.setEmail("student2@teammates.tmt");
        student2.setId(UUID.randomUUID());

        session1InCourse1 = getTypicalFeedbackSessionForCourse(course1);
        session1InCourse1.setName("session1");
        session1InCourse1.setId(UUID.randomUUID());

        session2InCourse1 = getTypicalFeedbackSessionForCourse(course1);
        session2InCourse1.setName("session2");
        session2InCourse1.setId(UUID.randomUUID());

        session1InCourse2 = getTypicalFeedbackSessionForCourse(course2);
        session1InCourse2.setName("session1");
        session1InCourse2.setId(UUID.randomUUID());

        reset(mockLogic);

        when(mockLogic.getStudentReference(student1.getId())).thenReturn(student1);
        when(mockLogic.getStudentReference(student2.getId())).thenReturn(student2);

        when(mockLogic.getFeedbackSessionReference(session1InCourse1.getId())).thenReturn(session1InCourse1);
        when(mockLogic.getFeedbackSessionReference(session2InCourse1.getId())).thenReturn(session2InCourse1);
        when(mockLogic.getFeedbackSessionReference(session1InCourse2.getId())).thenReturn(session1InCourse2);

        mockLogsProcessor.getOrderedFeedbackSessionLogs("", "", 0, 0, "").clear();
    }

    @Test
    public void testExecute_noRecentLogs_noLogsCreated()
            throws EntityAlreadyExistsException, InvalidParametersException {
        UpdateFeedbackSessionLogsAction action = getAction();
        action.execute();

        verify(mockLogic).createFeedbackSessionLogs(argThat(filteredLogs -> filteredLogs.isEmpty()));
    }

    @Test
    public void testExecute_recentLogsNoSpam_allLogsCreated()
            throws EntityAlreadyExistsException, InvalidParametersException {
        // Different Types
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusSeconds(100).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.SUBMISSION.getLabel(), startTime.plusSeconds(100).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.VIEW_RESULT.getLabel(), startTime.plusSeconds(100).toEpochMilli());

        // Different feedback sessions
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusSeconds(200).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session2InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusSeconds(200).toEpochMilli());

        // Different Student
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusSeconds(300).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student2.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusSeconds(300).toEpochMilli());

        // Different course
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusSeconds(400).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course2.getId(), student1.getId(), session1InCourse2.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusSeconds(400).toEpochMilli());

        // Gap is larger than spam filter
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusMillis(SPAM_FILTER + 1).toEpochMilli());

        UpdateFeedbackSessionLogsAction action = getAction();
        action.execute();

        // method returns all logs regardless of params
        List<FeedbackSessionLogEntry> expected = mockLogsProcessor.getOrderedFeedbackSessionLogs("", "", 0, 0, "");

        verify(mockLogic).createFeedbackSessionLogs(argThat(filteredLogs -> isEqual(expected, filteredLogs)));
    }

    @Test
    public void testExecute_recentLogsWithSpam_someLogsCreated()
            throws EntityAlreadyExistsException, InvalidParametersException {
        // Gap is smaller than spam filter
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusMillis(SPAM_FILTER - 2).toEpochMilli());

        // Filters multiple logs within one spam window
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusMillis(SPAM_FILTER - 1).toEpochMilli());

        // Correctly adds new log after filtering
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusMillis(SPAM_FILTER + 1).toEpochMilli());

        // Filters out spam in the new window
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusMillis(SPAM_FILTER + 2).toEpochMilli());

        UpdateFeedbackSessionLogsAction action = getAction();
        action.execute();

        List<FeedbackSessionLogEntry> expected = new ArrayList<>();
        expected.add(new FeedbackSessionLogEntry(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.toEpochMilli()));
        expected.add(new FeedbackSessionLogEntry(course1.getId(), student1.getId(), session1InCourse1.getId(),
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.plusMillis(SPAM_FILTER + 1).toEpochMilli()));

        verify(mockLogic).createFeedbackSessionLogs(argThat(filteredLogs -> isEqual(expected, filteredLogs)));
    }

    @Test
    public void testSpecificAccessControl_isAdmin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    public void testSpecificAccessControl_isInstructor_cannotAccess() {
        loginAsInstructor("user-id");
        verifyCannotAccess();
    }

    @Test
    public void testSpecificAccessControl_isStudent_cannotAccess() {
        loginAsStudent("user-id");
        verifyCannotAccess();
    }

    @Test
    public void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }

    private Boolean isEqual(List<FeedbackSessionLogEntry> expected, List<FeedbackSessionLog> actual) {

        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            FeedbackSessionLogEntry expectedEntry = expected.get(i);
            FeedbackSessionLog actualLog = actual.get(i);

            assertEquals(expectedEntry.getStudentId(), actualLog.getStudent().getId());

            assertEquals(expectedEntry.getFeedbackSessionId(), actualLog.getFeedbackSession().getId());

            assertEquals(expectedEntry.getFeedbackSessionLogType(), actualLog.getFeedbackSessionLogType().getLabel());

            assertEquals(expectedEntry.getTimestamp(), actualLog.getTimestamp().toEpochMilli());
        }

        return true;
    }
}
