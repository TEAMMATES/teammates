package teammates.it.storage.sqlapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.FeedbackSessionLogsDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link FeedbackSessionLogsDb}.
 */
public class FeedbackSessionLogsDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final FeedbackSessionLogsDb fslDb = FeedbackSessionLogsDb.inst();

    private SqlDataBundle typicalDataBundle;

    @Override
    @BeforeClass
    public void setupClass() {
        super.setupClass();
        typicalDataBundle = getTypicalSqlDataBundle();
    }

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalDataBundle);
        HibernateUtil.flushSession();
    }

    @Test
    public void test_createFeedbackSessionLog_success() {
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackSession feedbackSession = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Student student = typicalDataBundle.students.get("student1InCourse1");

        Instant logTimestamp = Instant.parse("2011-01-01T00:00:00Z");
        FeedbackSessionLog expected = new FeedbackSessionLog(student, feedbackSession, FeedbackSessionLogType.ACCESS,
                logTimestamp);

        fslDb.createFeedbackSessionLog(expected);

        List<FeedbackSessionLog> actualLogs = fslDb.getOrderedFeedbackSessionLogs(course.getId(), student.getEmail(),
                feedbackSession.getName(), logTimestamp, logTimestamp.plusSeconds(1));

        assertEquals(actualLogs.size(), 1);
        assertEquals(expected, actualLogs.get(0));
    }

    @Test
    public void test_getOrderedFeedbackSessionLogs_success() {
        Instant startTime = Instant.parse("2011-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2011-01-01T01:00:00Z");

        Course course1 = typicalDataBundle.courses.get("course1");

        FeedbackSession session1 = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSession session2 = typicalDataBundle.feedbackSessions.get("session2InTypicalCourse");
        FeedbackSession sessionInAnotherCourse = typicalDataBundle.feedbackSessions.get("ongoingSession1InCourse3");

        Student student1 = typicalDataBundle.students.get("student1InCourse1");
        Student student2 = typicalDataBundle.students.get("student2InCourse1");

        FeedbackSessionLog student1Session1Log1 = new FeedbackSessionLog(student1, session1,
                FeedbackSessionLogType.ACCESS, startTime);
        FeedbackSessionLog student1Session1Log2 = new FeedbackSessionLog(student1, session1,
                FeedbackSessionLogType.SUBMISSION, startTime.plusSeconds(1));
        FeedbackSessionLog student1Session1Log3 = new FeedbackSessionLog(student1, session1,
                FeedbackSessionLogType.VIEW_RESULT, startTime.plusSeconds(2));
        FeedbackSessionLog student1Session2Log1 = new FeedbackSessionLog(student1, session2,
                FeedbackSessionLogType.ACCESS, startTime.plusSeconds(3));

        FeedbackSessionLog student2Session1Log1 = new FeedbackSessionLog(student2, session1,
                FeedbackSessionLogType.ACCESS, startTime.plusSeconds(4));
        FeedbackSessionLog student2Session2Log1 = new FeedbackSessionLog(student2, session2,
                FeedbackSessionLogType.ACCESS, startTime.plusSeconds(5));

        FeedbackSessionLog student1AnotherCourseLog1 = new FeedbackSessionLog(student1, sessionInAnotherCourse,
                FeedbackSessionLogType.ACCESS, startTime.plusSeconds(6));

        FeedbackSessionLog outOfRangeLog1 = new FeedbackSessionLog(student1, session1, FeedbackSessionLogType.ACCESS,
                startTime.minusSeconds(1));
        FeedbackSessionLog outOfRangeLog2 = new FeedbackSessionLog(student1, session1, FeedbackSessionLogType.ACCESS,
                endTime);

        List<FeedbackSessionLog> newLogs = new ArrayList<>();
        newLogs.add(student1Session1Log1);
        newLogs.add(student1Session1Log2);
        newLogs.add(student1Session1Log3);
        newLogs.add(student1Session2Log1);

        newLogs.add(student2Session1Log1);
        newLogs.add(student2Session2Log1);

        newLogs.add(student1AnotherCourseLog1);

        newLogs.add(outOfRangeLog1);
        newLogs.add(outOfRangeLog2);

        for (FeedbackSessionLog log : newLogs) {
            fslDb.createFeedbackSessionLog(log);
        }

        ______TS("Return all logs in time range");
        List<FeedbackSessionLog> expectedLogs = new ArrayList<>();
        expectedLogs.add(student1Session1Log1);
        expectedLogs.add(student1Session1Log2);
        expectedLogs.add(student1Session1Log3);
        expectedLogs.add(student1Session2Log1);

        expectedLogs.add(student2Session1Log1);
        expectedLogs.add(student2Session2Log1);

        expectedLogs.add(student1AnotherCourseLog1);

        List<FeedbackSessionLog> actualLogs = fslDb.getOrderedFeedbackSessionLogs(null, null, null, startTime, endTime);
        assertEquals(expectedLogs, actualLogs);

        ______TS("Return logs belonging to a course in time range");
        expectedLogs = new ArrayList<>();
        expectedLogs.add(student1Session1Log1);
        expectedLogs.add(student1Session1Log2);
        expectedLogs.add(student1Session1Log3);
        expectedLogs.add(student1Session2Log1);

        expectedLogs.add(student2Session1Log1);
        expectedLogs.add(student2Session2Log1);

        actualLogs = fslDb.getOrderedFeedbackSessionLogs(course1.getId(), null, null, startTime, endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("Return logs belonging to a student in time range");
        expectedLogs = new ArrayList<>();
        expectedLogs.add(student1Session1Log1);
        expectedLogs.add(student1Session1Log2);
        expectedLogs.add(student1Session1Log3);
        expectedLogs.add(student1Session2Log1);

        expectedLogs.add(student1AnotherCourseLog1);

        actualLogs = fslDb.getOrderedFeedbackSessionLogs(null, student1.getEmail(), null, startTime, endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("Return logs belonging to all feedback sessions with the specified name in time range");
        expectedLogs = new ArrayList<>();
        expectedLogs.add(student1Session1Log1);
        expectedLogs.add(student1Session1Log2);
        expectedLogs.add(student1Session1Log3);

        expectedLogs.add(student2Session1Log1);

        actualLogs = fslDb.getOrderedFeedbackSessionLogs(null, null, session1.getName(), startTime, endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("Return logs belonging to a student in feedback sessions with the specified name in time range");
        expectedLogs = new ArrayList<>();
        expectedLogs.add(student1Session1Log1);
        expectedLogs.add(student1Session1Log2);
        expectedLogs.add(student1Session1Log3);

        actualLogs = fslDb.getOrderedFeedbackSessionLogs(null, student1.getEmail(), session1.getName(), startTime, endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("Return logs belonging to a student in a course in time range");
        expectedLogs = new ArrayList<>();
        expectedLogs.add(student1Session1Log1);
        expectedLogs.add(student1Session1Log2);
        expectedLogs.add(student1Session1Log3);
        expectedLogs.add(student1Session2Log1);

        actualLogs = fslDb.getOrderedFeedbackSessionLogs(course1.getId(), student1.getEmail(), null, startTime, endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("Return logs belonging to a feedback session in time range");
        expectedLogs = new ArrayList<>();
        expectedLogs.add(student1Session2Log1);
        expectedLogs.add(student2Session2Log1);

        actualLogs = fslDb.getOrderedFeedbackSessionLogs(course1.getId(), null, session2.getName(), startTime, endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("Return logs belonging to a student in a feedback session in time range");
        expectedLogs = new ArrayList<>();
        expectedLogs.add(student2Session2Log1);

        actualLogs = fslDb.getOrderedFeedbackSessionLogs(course1.getId(), student2.getEmail(), session2.getName(), startTime,
                endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("No logs in time range, return empty list");
        expectedLogs = new ArrayList<>();

        actualLogs = fslDb.getOrderedFeedbackSessionLogs(null, null, null, endTime.plusSeconds(3600),
                endTime.plusSeconds(7200));

        assertEquals(expectedLogs, actualLogs);
    }
}
