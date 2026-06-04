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
        UUID feedbackSessionId = given.feedbackSession("feedback-session");
        persistGivenData(given);

        FeedbackSession actual = inTransaction(() -> feedbackSessionsDb.getFeedbackSession(feedbackSessionId));

        assertNotNull(actual);
        assertEquals(feedbackSessionId, actual.getId());
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
        String courseId = given.course("course");
        UUID feedbackSessionId = given.feedbackSession("feedback-session",
                fs -> fs.course("course").name("Feedback Session Name"));
        given.feedbackSession("same-name-feedback-session-in-another-course",
                fs -> fs.course("another-course").name("Feedback Session Name"));
        persistGivenData(given);

        FeedbackSession actual = inTransaction(() -> feedbackSessionsDb.getFeedbackSession(
                "Feedback Session Name", courseId));

        assertNotNull(actual);
        assertEquals(feedbackSessionId, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void persistFeedbackSession_feedbackSessionIsNew_feedbackSessionIsPersisted() {
        String courseId = given.course("course");
        UUID creatorId = given.instructor("creator", i -> i.course("course"));
        persistGivenData(given);
        UUID feedbackSessionId = given.uuid("feedback-session");

        FeedbackSession actual = inTransaction(() -> {
            Course course = getEntity(Course.class, courseId);
            Instructor creator = getEntity(Instructor.class, creatorId);
            FeedbackSession feedbackSession = buildDefaultFeedbackSession(course, creator, feedbackSessionId);
            return feedbackSessionsDb.persistFeedbackSession(feedbackSession);
        });

        assertEquals(feedbackSessionId, actual.getId());
        verifyPresentInDatabase(FeedbackSession.class, feedbackSessionId);
    }

    @Test(groups = GroupNames.DB)
    public void removeFeedbackSession_feedbackSessionExists_feedbackSessionIsRemoved() {
        UUID feedbackSessionId = given.feedbackSession("feedback-session");
        persistGivenData(given);

        inTransaction(() -> feedbackSessionsDb.removeFeedbackSession(
                feedbackSessionsDb.getFeedbackSession(feedbackSessionId)));

        verifyAbsentInDatabase(FeedbackSession.class, feedbackSessionId);
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsForCourses_sessionsExist_returnsNonSoftDeletedSessionsInActiveCourses() {
        String courseId = given.course("course");
        String anotherCourseId = given.course("another-course");
        given.course("soft-deleted-course", c -> c.softDeleted());
        UUID feedbackSessionId = given.feedbackSession("feedback-session", fs -> fs.course("course"));
        UUID anotherCourseFeedbackSessionId = given.feedbackSession("another-course-feedback-session",
                fs -> fs.course("another-course"));
        given.feedbackSession("soft-deleted-feedback-session", fs -> fs.course("course").softDeleted());
        given.feedbackSession("feedback-session-in-soft-deleted-course", fs -> fs.course("soft-deleted-course"));
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(() -> feedbackSessionsDb.getFeedbackSessionsForCourses(
                List.of(courseId, anotherCourseId, given.stringId("soft-deleted-course"))));

        assertEquals(Set.of(feedbackSessionId, anotherCourseFeedbackSessionId),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getSoftDeletedFeedbackSessionsForCourses_sessionsExist_returnsSoftDeletedSessionsInActiveCourses() {
        String courseId = given.course("course");
        given.course("soft-deleted-course", c -> c.softDeleted());
        UUID softDeletedFeedbackSessionId = given.feedbackSession("soft-deleted-feedback-session",
                fs -> fs.course("course").softDeleted());
        given.feedbackSession("feedback-session", fs -> fs.course("course"));
        given.feedbackSession("soft-deleted-feedback-session-in-soft-deleted-course",
                fs -> fs.course("soft-deleted-course").softDeleted());
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(() -> feedbackSessionsDb.getSoftDeletedFeedbackSessionsForCourses(
                List.of(courseId, given.stringId("soft-deleted-course"))));

        assertEquals(Set.of(softDeletedFeedbackSessionId),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getOngoingSessions_sessionsExist_returnsSessionsOverlappingRange() {
        Instant now = Instant.now();
        UUID ongoingFeedbackSessionId = given.feedbackSession("ongoing-feedback-session", fs -> fs.opened());
        given.feedbackSession("ended-before-range-feedback-session",
                fs -> fs.closed());
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(() -> feedbackSessionsDb.getOngoingSessions(
                now.minus(30, ChronoUnit.MINUTES), now.plus(30, ChronoUnit.MINUTES)));

        assertEquals(Set.of(ongoingFeedbackSessionId),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsForCourseStartingAfter_sessionsExist_returnsMatchingSessions() {
        Instant now = Instant.now();
        String courseId = given.course("course");
        UUID matchingFeedbackSessionId = given.feedbackSession("matching-feedback-session",
                fs -> fs.course("course").waitingToOpen());
        given.feedbackSession("earlier-feedback-session",
                fs -> fs.course("course").opened());
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(() -> feedbackSessionsDb.getFeedbackSessionsForCourseStartingAfter(
                courseId, now));

        assertEquals(Set.of(matchingFeedbackSessionId),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsPossiblyNeedingOpeningSoonEmail_sessionsExist_returnsEligibleSessions() {
        UUID openingSoonFeedbackSessionId = given.feedbackSession("opening-soon-feedback-session",
                fs -> fs.openingSoon().openingSoonEmailSent(false));
        given.feedbackSession("sent-opening-soon-feedback-session",
                fs -> fs.openingSoon().openingSoonEmailSent(true));
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(
                feedbackSessionsDb::getFeedbackSessionsPossiblyNeedingOpeningSoonEmail);

        assertEquals(Set.of(openingSoonFeedbackSessionId),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsPossiblyNeedingClosingSoonEmail_sessionsExist_returnsEligibleSessions() {
        UUID closingSoonFeedbackSessionId = given.feedbackSession("closing-soon-feedback-session",
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

        assertEquals(Set.of(closingSoonFeedbackSessionId),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsPossiblyNeedingClosedEmail_sessionsExist_returnsEligibleSessions() {
        UUID closedFeedbackSessionId = given.feedbackSession("closed-feedback-session",
                fs -> fs.closed()
                        .closedEmailSent(false)
                        .closingSoonEmailEnabled(true));
        given.feedbackSession("sent-closed-feedback-session",
                fs -> fs.closed()
                        .closedEmailSent(true)
                        .closingSoonEmailEnabled(true));
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(feedbackSessionsDb::getFeedbackSessionsPossiblyNeedingClosedEmail);

        assertEquals(Set.of(closedFeedbackSessionId),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsPossiblyNeedingPublishedEmail_sessionsExist_returnsEligibleSessions() {
        UUID publishedFeedbackSessionId = given.feedbackSession("published-feedback-session",
                fs -> fs.published().publishedEmailSent(false).publishedEmailEnabled(true));
        given.feedbackSession("sent-published-feedback-session",
                fs -> fs.published().publishedEmailSent(true).publishedEmailEnabled(true));
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(
                feedbackSessionsDb::getFeedbackSessionsPossiblyNeedingPublishedEmail);

        assertEquals(Set.of(publishedFeedbackSessionId),
                actual.stream().map(FeedbackSession::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackSessionsPossiblyNeedingOpenedEmail_sessionsExist_returnsEligibleSessions() {
        UUID openedFeedbackSessionId = given.feedbackSession("opened-feedback-session",
                fs -> fs.opened().openedEmailSent(false));
        given.feedbackSession("sent-opened-feedback-session", fs -> fs.opened().openedEmailSent(true));
        persistGivenData(given);

        List<FeedbackSession> actual = inTransaction(feedbackSessionsDb::getFeedbackSessionsPossiblyNeedingOpenedEmail);

        assertEquals(Set.of(openedFeedbackSessionId),
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
