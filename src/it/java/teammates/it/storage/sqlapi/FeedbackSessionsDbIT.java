package teammates.it.storage.sqlapi;

import java.time.Duration;
import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.FeedbackSessionsDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * SUT: {@link FeedbackSessionsDb}.
 */
public class FeedbackSessionsDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final CoursesDb coursesDb = CoursesDb.inst();
    private final FeedbackSessionsDb fsDb = FeedbackSessionsDb.inst();

    @Test
    public void testGetFeedbackSessionByFeedbackSessionNameAndCourseId()
            throws EntityAlreadyExistsException, InvalidParametersException {
        ______TS("success: get feedback session that exists");
        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        coursesDb.createCourse(course1);
        FeedbackSession fs1 = new FeedbackSession("name1", course1, "test1@test.com", "test-instruction",
                Instant.now().plus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(7)), Instant.now(),
                Instant.now().plus(Duration.ofDays(7)), Duration.ofMinutes(10), true, true, true);
        FeedbackSession fs2 = new FeedbackSession("name2", course1, "test1@test.com", "test-instruction",
                Instant.now().plus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(7)), Instant.now(),
                Instant.now().plus(Duration.ofDays(7)), Duration.ofMinutes(10), true, true, true);
        fsDb.createFeedbackSession(fs1);
        fsDb.createFeedbackSession(fs2);

        FeedbackSession actualFs = fsDb.getFeedbackSession(fs2.getName(), fs2.getCourse().getId());

        verifyEquals(fs2, actualFs);
    }
}
