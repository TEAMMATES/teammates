package teammates.it.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlsearch.SearchManagerFactory;
import teammates.ui.webapi.UpdateFeedbackSessionLogsAction;

/**
 * SUT: {@link UpdateFeedbackSessionLogsAction}.
 */
public class UpdateFeedbackSessionLogsActionIT extends BaseActionIT<UpdateFeedbackSessionLogsAction> {

    static final int COLLECTION_TIME_PERIOD = 60; // represents one hour
    static final long SPAM_FILTER = 2000L; // in ms

    Student student1InCourse1;
    Student student2InCourse1;
    Student student1InCourse3;

    Course course1;
    Course course3;

    FeedbackSession session1InCourse1;
    FeedbackSession session2InCourse1;
    FeedbackSession session1InCourse3;

    Instant endTime;
    Instant startTime;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
        SearchManagerFactory.getStudentSearchManager().resetCollections();

        endTime = TimeHelper.getInstantNearestHourBefore(Instant.now());
        startTime = endTime.minus(COLLECTION_TIME_PERIOD, ChronoUnit.MINUTES);

        course1 = typicalBundle.courses.get("course1");
        course3 = typicalBundle.courses.get("course3");

        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        student1InCourse3 = typicalBundle.students.get("student1InCourse3");

        session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        session2InCourse1 = typicalBundle.feedbackSessions.get("session2InTypicalCourse");
        session1InCourse3 = typicalBundle.feedbackSessions.get("ongoingSession1InCourse3");

        mockLogsProcessor.getOrderedFeedbackSessionLogs("", "GET", 0, 0, "DELETE").clear();
    }

    @Override
    String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_SESSION_LOGS_PROCESSING;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() {
        ______TS("No spam all logs added");
        // Different Types
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(300).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.SUBMISSION.getLabel(),
                startTime.plusSeconds(300).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.VIEW_RESULT.getLabel(),
                startTime.plusSeconds(300).toEpochMilli());

        // Different feedback sessions
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(600).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session2InCourse1.getId(), session2InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(600).toEpochMilli());

        // Different Student
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(900).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student2InCourse1.getId(),
                student2InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(900).toEpochMilli());

        // Different course
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(1200).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course3.getId(), student1InCourse3.getId(),
                student1InCourse3.getEmail(),
                session1InCourse3.getId(), session1InCourse3.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(1200).toEpochMilli());

        // Gap is larger than spam filter
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusMillis(SPAM_FILTER + 1).toEpochMilli());

        UpdateFeedbackSessionLogsAction action = getAction();
        getJsonResult(action);

        // method returns all logs regardless of params
        List<FeedbackSessionLogEntry> expected = mockLogsProcessor.getOrderedFeedbackSessionLogs("", "", 0, 0, "");
        List<FeedbackSessionLog> actual = logic.getOrderedFeedbackSessionLogs(course1.getId(), null, null, startTime,
                endTime);
        List<FeedbackSessionLog> actualCourse3 = logic.getOrderedFeedbackSessionLogs(course3.getId(), null, null,
                startTime, endTime);
        actual.addAll(actualCourse3);
        assertTrue(isEqual(expected, actual));
    }

    @Test
    protected void testExecute_recentLogsWithSpam_someLogsCreated() {
        // Gap is smaller than spam filter
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusMillis(SPAM_FILTER - 2).toEpochMilli());

        // Filters multiple logs within one spam window
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusMillis(SPAM_FILTER - 1).toEpochMilli());

        // Correctly adds new log after filtering
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusMillis(SPAM_FILTER + 1).toEpochMilli());

        // Filters out spam in the new window
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusMillis(SPAM_FILTER + 2).toEpochMilli());

        UpdateFeedbackSessionLogsAction action = getAction();
        action.execute();

        List<FeedbackSessionLogEntry> expected = new ArrayList<>();
        expected.add(new FeedbackSessionLogEntry(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.toEpochMilli()));
        expected.add(new FeedbackSessionLogEntry(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusMillis(SPAM_FILTER + 1).toEpochMilli()));

        List<FeedbackSessionLog> actual = logic.getOrderedFeedbackSessionLogs(course1.getId(), null, null, startTime,
                endTime);
        assertTrue(isEqual(expected, actual));
    }

    @Test
    protected void testExecute_badLogs_otherLogsCreated() {
        UUID badUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(300).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(900).toEpochMilli());

        // bad student id
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), badUuid, student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(600).toEpochMilli());

        // bad session id
        mockLogsProcessor.insertFeedbackSessionLog(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                badUuid, session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(600).toEpochMilli());

        UpdateFeedbackSessionLogsAction action = getAction();
        action.execute();

        List<FeedbackSessionLogEntry> expected = new ArrayList<>();
        expected.add(new FeedbackSessionLogEntry(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(300).toEpochMilli()));
        expected.add(new FeedbackSessionLogEntry(course1.getId(), student1InCourse1.getId(),
                student1InCourse1.getEmail(),
                session1InCourse1.getId(), session1InCourse1.getName(), FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(900).toEpochMilli()));

        List<FeedbackSessionLog> actual = logic.getOrderedFeedbackSessionLogs(course1.getId(), null, null, startTime,
                endTime);
        assertTrue(isEqual(expected, actual));
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
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
