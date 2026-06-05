package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.test.GroupNames;

/**
 * Tests for {@link FeedbackSessionsDb}.
 */
public class FeedbackSessionsDbTest extends BaseDbTestcase {
    private final FeedbackSessionsDb feedbackSessionsDb = FeedbackSessionsDb.inst();

    @Test(groups = GroupNames.DB)
    public void getFeedbackSession_feedbackSessionExists_returnsFeedbackSession() {
        var feedbackSession = given.feedbackSession("feedback-session");
        persistGivenData(given);

        FeedbackSession actual = inTransaction(() -> feedbackSessionsDb.getFeedbackSession(feedbackSession.id()));

        assertNotNull(actual);
        assertEquals(feedbackSession.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSession_feedbackSessionDoesNotExist_returnsNull() {
        given.feedbackSession("different-feedback-session");
        persistGivenData(given);

        FeedbackSession actual = inTransaction(
                () -> feedbackSessionsDb.getFeedbackSession(given.uuid("non-existent-feedback-session")));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionByNameAndCourse_feedbackSessionExists_returnsFeedbackSession() {
        var course = given.course("course");
        var feedbackSession = given.feedbackSession("feedback-session",
                fs -> fs.course(course.alias()).name("Feedback Session Name"));
        given.feedbackSession("same-name-feedback-session-in-another-course",
                fs -> fs.course("another-course").name("Feedback Session Name"));
        persistGivenData(given);

        FeedbackSession actual = inTransaction(() -> feedbackSessionsDb.getFeedbackSession(
                "Feedback Session Name", course.id()));

        assertNotNull(actual);
        assertEquals(feedbackSession.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void persistFeedbackSession_feedbackSessionIsNew_feedbackSessionIsPersisted() {
        var courseRef = given.course("course");
        var creatorRef = given.instructor("creator", i -> i.course(courseRef.alias()));
        persistGivenData(given);
        var feedbackSessionId = given.uuid("feedback-session");

        FeedbackSession actual = inTransaction(() -> {
            Course course = getEntity(Course.class, courseRef.id());
            Instructor creator = getEntity(Instructor.class, creatorRef.id());
            FeedbackSession feedbackSession = buildDefaultFeedbackSession(course, creator, feedbackSessionId);
            return feedbackSessionsDb.persistFeedbackSession(feedbackSession);
        });

        assertEquals(feedbackSessionId, actual.getId());
        verifyPresentInDatabase(FeedbackSession.class, feedbackSessionId);
    }

    @Test(groups = GroupNames.DB)
    public void removeFeedbackSession_feedbackSessionExists_feedbackSessionIsRemoved() {
        var feedbackSession = given.feedbackSession("feedback-session");
        persistGivenData(given);

        inTransaction(() -> feedbackSessionsDb.removeFeedbackSession(
                feedbackSessionsDb.getFeedbackSession(feedbackSession.id())));

        verifyAbsentInDatabase(FeedbackSession.class, feedbackSession.id());
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsForCourses_sessionsExist_returnsNonSoftDeletedSessionsInActiveCourses() {
        var course = given.course("course");
        var anotherCourse = given.course("another-course");
        var softDeletedCourse = given.course("soft-deleted-course", c -> c.softDeleted());
        var feedbackSession = given.feedbackSession("feedback-session", fs -> fs.course(course.alias()));
        var anotherCourseFeedbackSession = given.feedbackSession("another-course-feedback-session",
                fs -> fs.course(anotherCourse.alias()));
        given.feedbackSession("soft-deleted-feedback-session", fs -> fs.course(course.alias()).softDeleted());
        given.feedbackSession("feedback-session-in-soft-deleted-course", fs -> fs.course(softDeletedCourse.alias()));
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(() -> feedbackSessionsDb.getFeedbackSessionsForCourses(
                List.of(course.id(), anotherCourse.id(), softDeletedCourse.id())));

        assertEquals(Set.of(feedbackSession.id(), anotherCourseFeedbackSession.id()),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getSoftDeletedFeedbackSessionsForCourses_sessionsExist_returnsSoftDeletedSessionsInActiveCourses() {
        var course = given.course("course");
        var softDeletedCourse = given.course("soft-deleted-course", c -> c.softDeleted());
        var softDeletedFeedbackSession = given.feedbackSession("soft-deleted-feedback-session",
                fs -> fs.course(course.alias()).softDeleted());
        given.feedbackSession("feedback-session", fs -> fs.course(course.alias()));
        given.feedbackSession("soft-deleted-feedback-session-in-soft-deleted-course",
                fs -> fs.course(softDeletedCourse.alias()).softDeleted());
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(() -> feedbackSessionsDb.getSoftDeletedFeedbackSessionsForCourses(
                List.of(course.id(), softDeletedCourse.id())));

        assertEquals(Set.of(softDeletedFeedbackSession.id()),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getOngoingSessions_sessionsExist_returnsSessionsOverlappingRange() {
        Instant now = Instant.now();
        var ongoingFeedbackSession = given.feedbackSession("ongoing-feedback-session", fs -> fs.opened());
        given.feedbackSession("ended-before-range-feedback-session",
                fs -> fs.closed());
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(() -> feedbackSessionsDb.getOngoingSessions(
                now.minus(30, ChronoUnit.MINUTES), now.plus(30, ChronoUnit.MINUTES)));

        assertEquals(Set.of(ongoingFeedbackSession.id()),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsForCourseStartingAfter_sessionsExist_returnsMatchingSessions() {
        Instant now = Instant.now();
        var course = given.course("course");
        var matchingFeedbackSession = given.feedbackSession("matching-feedback-session",
                fs -> fs.course(course.alias()).waitingToOpen());
        given.feedbackSession("earlier-feedback-session",
                fs -> fs.course(course.alias()).opened());
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(() -> feedbackSessionsDb.getFeedbackSessionsForCourseStartingAfter(
                course.id(), now));

        assertEquals(Set.of(matchingFeedbackSession.id()),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsPossiblyNeedingOpeningSoonEmail_sessionsExist_returnsEligibleSessions() {
        var openingSoonFeedbackSession = given.feedbackSession("opening-soon-feedback-session",
                fs -> fs.openingSoon().openingSoonEmailSent(false));
        given.feedbackSession("sent-opening-soon-feedback-session",
                fs -> fs.openingSoon().openingSoonEmailSent(true));
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(
                feedbackSessionsDb::getFeedbackSessionsPossiblyNeedingOpeningSoonEmail);

        assertEquals(Set.of(openingSoonFeedbackSession.id()),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsPossiblyNeedingClosingSoonEmail_sessionsExist_returnsEligibleSessions() {
        var closingSoonFeedbackSession = given.feedbackSession("closing-soon-feedback-session",
                fs -> fs.closingSoon()
                        .closingSoonEmailSent(false)
                        .closedEmailSent(false)
                        .closingSoonEmailEnabled(true));
        given.feedbackSession("sent-closing-soon-feedback-session",
                fs -> fs.closingSoon()
                        .closingSoonEmailSent(true)
                        .closedEmailSent(false)
                        .closingSoonEmailEnabled(true));
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(
                feedbackSessionsDb::getFeedbackSessionsPossiblyNeedingClosingSoonEmail);

        assertEquals(Set.of(closingSoonFeedbackSession.id()),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsPossiblyNeedingClosedEmail_sessionsExist_returnsEligibleSessions() {
        var closedFeedbackSession = given.feedbackSession("closed-feedback-session",
                fs -> fs.closed()
                        .closedEmailSent(false)
                        .closingSoonEmailEnabled(true));
        given.feedbackSession("sent-closed-feedback-session",
                fs -> fs.closed()
                        .closedEmailSent(true)
                        .closingSoonEmailEnabled(true));
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(feedbackSessionsDb::getFeedbackSessionsPossiblyNeedingClosedEmail);

        assertEquals(Set.of(closedFeedbackSession.id()),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsPossiblyNeedingPublishedEmail_sessionsExist_returnsEligibleSessions() {
        var publishedFeedbackSession = given.feedbackSession("published-feedback-session",
                fs -> fs.published().publishedEmailSent(false).publishedEmailEnabled(true));
        given.feedbackSession("sent-published-feedback-session",
                fs -> fs.published().publishedEmailSent(true).publishedEmailEnabled(true));
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(
                feedbackSessionsDb::getFeedbackSessionsPossiblyNeedingPublishedEmail);

        assertEquals(Set.of(publishedFeedbackSession.id()),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsPossiblyNeedingOpenedEmail_sessionsExist_returnsEligibleSessions() {
        var openedFeedbackSession = given.feedbackSession("opened-feedback-session",
                fs -> fs.opened().openedEmailSent(false));
        given.feedbackSession("sent-opened-feedback-session", fs -> fs.opened().openedEmailSent(true));
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(feedbackSessionsDb::getFeedbackSessionsPossiblyNeedingOpenedEmail);

        assertEquals(Set.of(openedFeedbackSession.id()),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    private static FeedbackSession buildDefaultFeedbackSession(
            Course course, Instructor creator, UUID feedbackSessionId) {
        assertNotNull(course);
        assertNotNull(creator);
        Instant now = Instant.now();
        FeedbackSession feedbackSession = new FeedbackSession(
                "Feedback Session Name",
                creator,
                "<p>Feedback Session Instructions</p>",
                now.minus(1, ChronoUnit.HOURS),
                now.plus(1, ChronoUnit.HOURS),
                now.minus(2, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS),
                Duration.ZERO,
                true,
                true);
        feedbackSession.setId(feedbackSessionId);
        course.addFeedbackSession(feedbackSession);
        return feedbackSession;
    }
}
