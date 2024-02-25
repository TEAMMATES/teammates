package teammates.it.storage.sqlapi;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
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

    @Test
    public void testGetOngoingSessions_typicalCase_shouldGetOnlyOngoingSessionsWithinRange()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Instant instantNow = Instant.now();
        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        coursesDb.createCourse(course1);
        FeedbackSession c1Fs1 = new FeedbackSession("name1-1", course1, "test1@test.com", "test-instruction",
                instantNow.minus(Duration.ofDays(7L)), instantNow.minus(Duration.ofDays(1L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        fsDb.createFeedbackSession(c1Fs1);
        FeedbackSession c1Fs2 = new FeedbackSession("name1-2", course1, "test2@test.com", "test-instruction",
                instantNow, instantNow.plus(Duration.ofDays(7L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        fsDb.createFeedbackSession(c1Fs2);
        Course course2 = new Course("test-id2", "test-name2", "UTC", "MIT");
        coursesDb.createCourse(course2);
        FeedbackSession c2Fs1 = new FeedbackSession("name2-1", course2, "test3@test.com", "test-instruction",
                instantNow.minus(Duration.ofHours(12L)), instantNow.plus(Duration.ofHours(12L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        fsDb.createFeedbackSession(c2Fs1);
        FeedbackSession c2Fs2 = new FeedbackSession("name2-2", course2, "test3@test.com", "test-instruction",
                instantNow.plus(Duration.ofDays(1L)), instantNow.plus(Duration.ofDays(7L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        fsDb.createFeedbackSession(c2Fs2);
        Course course3 = new Course("test-id3", "test-name3", "UTC", "UCL");
        coursesDb.createCourse(course3);
        FeedbackSession c3Fs1 = new FeedbackSession("name3-1", course3, "test4@test.com", "test-instruction",
                instantNow.minus(Duration.ofDays(7L)), instantNow,
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        fsDb.createFeedbackSession(c3Fs1);
        Set<FeedbackSession> expectedUniqueOngoingSessions = new HashSet<>();
        expectedUniqueOngoingSessions.add(c1Fs2);
        expectedUniqueOngoingSessions.add(c2Fs1);
        expectedUniqueOngoingSessions.add(c3Fs1);
        List<FeedbackSession> actualOngoingSessions =
                fsDb.getOngoingSessions(instantNow.minus(Duration.ofDays(1L)), instantNow.plus(Duration.ofDays(1L)));
        Set<FeedbackSession> actualUniqueOngoingSessions = new HashSet<>();
        actualUniqueOngoingSessions.addAll(actualOngoingSessions);
        assertEquals(expectedUniqueOngoingSessions, actualUniqueOngoingSessions);
    }

    @Test
    public void testSoftDeleteFeedbackSession()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        coursesDb.createCourse(course1);
        FeedbackSession fs1 = new FeedbackSession("name1", course1, "test1@test.com", "test-instruction",
                Instant.now().plus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(7)), Instant.now(),
                Instant.now().plus(Duration.ofDays(7)), Duration.ofMinutes(10), true, true, true);
        fsDb.createFeedbackSession(fs1);
        fsDb.softDeleteFeedbackSession(fs1.getName(), course1.getId());

        FeedbackSession softDeletedFs = fsDb.getSoftDeletedFeedbackSession(fs1.getName(), course1.getId());
        verifyEquals(fs1, softDeletedFs);
    }

    @Test
    public void testRestoreFeedbackSession()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        coursesDb.createCourse(course1);
        FeedbackSession fs1 = new FeedbackSession("name1", course1, "test1@test.com", "test-instruction",
                Instant.now().plus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(7)), Instant.now(),
                Instant.now().plus(Duration.ofDays(7)), Duration.ofMinutes(10), true, true, true);
        fs1.setDeletedAt(Instant.now());
        fsDb.createFeedbackSession(fs1);
        FeedbackSession softDeletedFs = fsDb.getSoftDeletedFeedbackSession(fs1.getName(), course1.getId());

        verifyEquals(fs1, softDeletedFs);

        fsDb.restoreDeletedFeedbackSession(fs1.getName(), course1.getId());
        FeedbackSession restoredFs = fsDb.getFeedbackSession(fs1.getName(), course1.getId());

        verifyEquals(fs1, restoredFs);
    }
}
