package teammates.it.storage.api;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;

/**
 * SUT: {@link FeedbackSessionsDb}.
 */
public class FeedbackSessionsDbIT extends BaseTestCaseWithDatabaseAccess {

    private final CoursesDb coursesDb = CoursesDb.inst();
    private final FeedbackSessionsDb fsDb = FeedbackSessionsDb.inst();

    @Test
    public void testGetFeedbackSessionByFeedbackSessionNameAndCourseId() {
        ______TS("success: get feedback session that exists");
        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        coursesDb.createCourse(course1);
        FeedbackSession fs1 = new FeedbackSession("name1", "test1@test.com", "test-instruction",
                Instant.now().plus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(7)), Instant.now(),
                Instant.now().plus(Duration.ofDays(7)), Duration.ofMinutes(10), true, true);
        course1.addFeedbackSession(fs1);
        FeedbackSession fs2 = new FeedbackSession("name2", "test1@test.com", "test-instruction",
                Instant.now().plus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(7)), Instant.now(),
                Instant.now().plus(Duration.ofDays(7)), Duration.ofMinutes(10), true, true);
        course1.addFeedbackSession(fs2);
        fsDb.createFeedbackSession(fs1);
        fsDb.createFeedbackSession(fs2);

        FeedbackSession actualFs = fsDb.getFeedbackSession(fs2.getName(), fs2.getCourseId());

        verifyEquals(fs2, actualFs);
    }

    @Test
    public void testGetOngoingSessions_typicalCase_shouldGetOnlyOngoingSessionsWithinRange()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Instant instantNow = Instant.now();
        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        coursesDb.createCourse(course1);
        FeedbackSession c1Fs1 = new FeedbackSession("name1-1", "test1@test.com", "test-instruction",
                instantNow.minus(Duration.ofDays(7L)), instantNow.minus(Duration.ofDays(1L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true);
        course1.addFeedbackSession(c1Fs1);
        fsDb.createFeedbackSession(c1Fs1);
        FeedbackSession c1Fs2 = new FeedbackSession("name1-2", "test2@test.com", "test-instruction",
                instantNow, instantNow.plus(Duration.ofDays(7L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true);
        course1.addFeedbackSession(c1Fs2);
        fsDb.createFeedbackSession(c1Fs2);
        Course course2 = new Course("test-id2", "test-name2", "UTC", "MIT");
        coursesDb.createCourse(course2);
        FeedbackSession c2Fs1 = new FeedbackSession("name2-1", "test3@test.com", "test-instruction",
                instantNow.minus(Duration.ofHours(12L)), instantNow.plus(Duration.ofHours(12L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true);
        course2.addFeedbackSession(c2Fs1);
        fsDb.createFeedbackSession(c2Fs1);
        FeedbackSession c2Fs2 = new FeedbackSession("name2-2", "test3@test.com", "test-instruction",
                instantNow.plus(Duration.ofDays(1L)), instantNow.plus(Duration.ofDays(7L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true);
        course2.addFeedbackSession(c2Fs2);
        fsDb.createFeedbackSession(c2Fs2);
        Course course3 = new Course("test-id3", "test-name3", "UTC", "UCL");
        coursesDb.createCourse(course3);
        FeedbackSession c3Fs1 = new FeedbackSession("name3-1", "test4@test.com", "test-instruction",
                instantNow.minus(Duration.ofDays(7L)), instantNow,
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true);
        course3.addFeedbackSession(c3Fs1);
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
}
