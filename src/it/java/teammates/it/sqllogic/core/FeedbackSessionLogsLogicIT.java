package teammates.it.sqllogic.core;

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
import teammates.sqllogic.core.FeedbackSessionLogsLogic;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link FeedbackSessionLogsLogic}.
 */
public class FeedbackSessionLogsLogicIT extends BaseTestCaseWithSqlDatabaseAccess {

    private FeedbackSessionLogsLogic fslLogic = FeedbackSessionLogsLogic.inst();

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
        HibernateUtil.clearSession();
    }

    @Test
    public void test_createFeedbackSessionLog_success() {
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Student student = typicalDataBundle.students.get("student1InCourse1");
        Instant timestamp = Instant.now();
        FeedbackSessionLog newLog1 = new FeedbackSessionLog(student, fs, FeedbackSessionLogType.ACCESS, timestamp);
        FeedbackSessionLog newLog2 = new FeedbackSessionLog(student, fs, FeedbackSessionLogType.SUBMISSION, timestamp);
        FeedbackSessionLog newLog3 = new FeedbackSessionLog(student, fs, FeedbackSessionLogType.VIEW_RESULT, timestamp);
        List<FeedbackSessionLog> expected = List.of(newLog1, newLog2, newLog3);

        fslLogic.createFeedbackSessionLogs(expected);

        List<FeedbackSessionLog> actual = fslLogic.getOrderedFeedbackSessionLogs(course.getId(), student.getId(),
                fs.getId(), timestamp, timestamp.plusSeconds(1));

        assertEquals(expected, actual);
    }

    @Test
    public void test_getOrderedFeedbackSessionLogs_success() {
        Instant startTime = Instant.parse("2012-01-01T12:00:00Z");
        Instant endTime = Instant.parse("2012-01-01T23:59:59Z");
        Course course = typicalDataBundle.courses.get("course1");
        Student student1 = typicalDataBundle.students.get("student1InCourse1");
        FeedbackSession fs1 = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        FeedbackSessionLog student1Session1Log1 = typicalDataBundle.feedbackSessionLogs.get("student1Session1Log1");
        FeedbackSessionLog student1Session2Log1 = typicalDataBundle.feedbackSessionLogs.get("student1Session2Log1");
        FeedbackSessionLog student1Session2Log2 = typicalDataBundle.feedbackSessionLogs.get("student1Session2Log2");
        FeedbackSessionLog student2Session1Log1 = typicalDataBundle.feedbackSessionLogs.get("student2Session1Log1");
        FeedbackSessionLog student2Session1Log2 = typicalDataBundle.feedbackSessionLogs.get("student2Session1Log2");

        ______TS("Return logs belonging to a course in time range");
        List<FeedbackSessionLog> expectedLogs = List.of(
                student1Session1Log1,
                student1Session2Log1,
                student1Session2Log2,
                student2Session1Log1,
                student2Session1Log2);

        List<FeedbackSessionLog> actualLogs = fslLogic.getOrderedFeedbackSessionLogs(course.getId(), null, null,
                startTime, endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("Return logs belonging to a student in a course in time range");
        expectedLogs = List.of(
                student1Session1Log1,
                student1Session2Log1,
                student1Session2Log2);

        actualLogs = fslLogic.getOrderedFeedbackSessionLogs(course.getId(), student1.getId(), null, startTime,
                endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("Return logs belonging to a feedback session in time range");
        expectedLogs = List.of(
                student1Session1Log1,
                student2Session1Log1,
                student2Session1Log2);

        actualLogs = fslLogic.getOrderedFeedbackSessionLogs(course.getId(), null, fs1.getId(), startTime, endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("Return logs belonging to a student in a feedback session in time range");
        expectedLogs = List.of(student1Session1Log1);

        actualLogs = fslLogic.getOrderedFeedbackSessionLogs(course.getId(), student1.getId(), fs1.getId(),
                startTime,
                endTime);

        assertEquals(expectedLogs, actualLogs);

        ______TS("No logs in time range, return empty list");
        expectedLogs = new ArrayList<>();

        actualLogs = fslLogic.getOrderedFeedbackSessionLogs(course.getId(), null, null, endTime.plusSeconds(3600),
                endTime.plusSeconds(7200));

        assertEquals(expectedLogs, actualLogs);
    }

}
