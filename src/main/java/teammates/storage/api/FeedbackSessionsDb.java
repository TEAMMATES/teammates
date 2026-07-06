package teammates.storage.api;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;

/**
 * Handles CRUD operations for feedback sessions.
 *
 * @see FeedbackSession
 */
public final class FeedbackSessionsDb {

    private static final FeedbackSessionsDb instance = new FeedbackSessionsDb();

    private static final Duration REMINDER_LEAD_TIME = Duration.ofHours(24);

    private FeedbackSessionsDb() {
        // prevent initialization
    }

    public static FeedbackSessionsDb inst() {
        return instance;
    }

    /**
     * Gets a feedback session.
     *
     * @return null if not found
     */
    public FeedbackSession getFeedbackSession(UUID fsId) {
        return HibernateUtil.get(FeedbackSession.class, fsId);
    }

    /**
     * Gets all non-soft-deleted feedback sessions for the given course IDs, excluding sessions in deleted courses.
     */
    public List<FeedbackSession> getFeedbackSessionsForCourses(List<String> courseIds) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cq = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> fsRoot = cq.from(FeedbackSession.class);
        Join<FeedbackSession, Course> fsJoin = fsRoot.join("course");
        cq.select(fsRoot).where(cb.and(
                fsJoin.get("id").in(courseIds),
                cb.isNull(fsJoin.get("deletedAt")),
                cb.isNull(fsRoot.get("deletedAt"))
        ));
        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Gets soft-deleted feedback sessions for the given course IDs, excluding sessions in deleted courses.
     */
    public List<FeedbackSession> getSoftDeletedFeedbackSessionsForCourses(List<String> courseIds) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cq = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> fsRoot = cq.from(FeedbackSession.class);
        Join<FeedbackSession, Course> fsJoin = fsRoot.join("course");
        cq.select(fsRoot).where(cb.and(
                fsJoin.get("id").in(courseIds),
                cb.isNull(fsJoin.get("deletedAt")),
                cb.isNotNull(fsRoot.get("deletedAt"))
        ));
        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Gets all and only the feedback sessions ongoing within a range of time.
     */
    public List<FeedbackSession> getOngoingSessions(Instant rangeStart, Instant rangeEnd) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);
        cr.select(root)
                .where(cb.and(
                    cb.greaterThan(root.get("endTime"), rangeStart),
                    cb.lessThan(root.get("startTime"), rangeEnd)));
        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Persists a feedback session.
     */
    public FeedbackSession persistFeedbackSession(FeedbackSession session) {
        HibernateUtil.persist(session);
        return session;
    }

    /**
     * Removes a feedback session.
     */
    public void removeFeedbackSession(FeedbackSession feedbackSession) {
        HibernateUtil.remove(feedbackSession);
    }

    /**
     * Gets non-soft-deleted feedback sessions for a given {@code courseId}.
     * Includes sessions from soft-deleted courses.
     */
    public List<FeedbackSession> getFeedbackSessionsForCourse(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cq = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cq.from(FeedbackSession.class);
        Join<FeedbackSession, Course> courseJoin = root.join("course");
        cq.select(root).where(cb.and(
                cb.equal(courseJoin.get("id"), courseId),
                cb.isNull(root.get("deletedAt"))
        ));

        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Gets feedback sessions for a given {@code courseId} that start after {@code after}.
     */
    public List<FeedbackSession> getFeedbackSessionsForCourseStartingAfter(String courseId, Instant after) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);
        Join<FeedbackSession, Course> courseJoin = root.join("course");
        cr.select(root)
                .where(cb.and(
                    cb.greaterThanOrEqualTo(root.get("startTime"), after),
                    cb.equal(courseJoin.get("id"), courseId),
                    cb.isNull(root.get("deletedAt"))));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of undeleted feedback sessions which open soon
     * and need an opening soon email to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsPossiblyNeedingOpeningSoonEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);
        Join<FeedbackSession, Course> courseJoin = root.join("course");
        Instant now = Instant.now();
        Instant reminderStart = now.plus(REMINDER_LEAD_TIME)
                .minus(Const.FEEDBACK_SESSION_REMINDER_EMAIL_REDUNDANCY_WINDOW);
        Instant reminderEnd = now.plus(REMINDER_LEAD_TIME);

        cr.select(root)
                .where(cb.and(
                    cb.greaterThanOrEqualTo(root.get("startTime"), reminderStart),
                    cb.lessThanOrEqualTo(root.get("startTime"), reminderEnd),
                    cb.isFalse(root.get("isOpeningSoonEmailSent")),
                    cb.isNull(root.get("deletedAt")),
                    cb.isNull(courseJoin.get("deletedAt"))
                ));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of undeleted feedback sessions which close soon
     * and need a closing soon email to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsPossiblyNeedingClosingSoonEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);
        Join<FeedbackSession, Course> courseJoin = root.join("course");
        Instant now = Instant.now();
        Instant reminderStart = now.plus(REMINDER_LEAD_TIME)
                .minus(Const.FEEDBACK_SESSION_REMINDER_EMAIL_REDUNDANCY_WINDOW);
        Instant reminderEnd = now.plus(REMINDER_LEAD_TIME);

        cr.select(root)
                .where(cb.and(
                        cb.lessThan(root.get("startTime"), now),
                        cb.greaterThan(root.get("endTime"), now),
                        cb.greaterThanOrEqualTo(root.get("endTime"), reminderStart),
                        cb.lessThanOrEqualTo(root.get("endTime"), reminderEnd),
                        cb.isFalse(root.get("isClosingSoonEmailSent")),
                        cb.isTrue(root.get("isClosingSoonEmailEnabled")),
                        cb.isFalse(root.get("isClosedEmailSent")),
                        cb.isNull(root.get("deletedAt")),
                        cb.isNull(courseJoin.get("deletedAt"))
               ));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of undeleted feedback sessions which may have closed recently
     * and need a closed email to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsPossiblyNeedingClosedEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);
        Join<FeedbackSession, Course> courseJoin = root.join("course");
        Instant now = Instant.now();

        cr.select(root)
                .where(cb.and(
                        cb.lessThanOrEqualTo(root.get("endTime"), now),
                        cb.greaterThan(root.get("endTime"),
                                now.minus(Const.FEEDBACK_SESSION_EVENT_EMAIL_LOOKBACK_WINDOW)),
                        cb.isFalse(root.get("isClosedEmailSent")),
                        cb.isTrue(root.get("isClosingSoonEmailEnabled")),
                        cb.isNull(root.get("deletedAt")),
                        cb.isNull(courseJoin.get("deletedAt"))
               ));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of undeleted published feedback sessions which need a published email
     * to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsPossiblyNeedingPublishedEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);
        Join<FeedbackSession, Course> courseJoin = root.join("course");
        Instant now = Instant.now();
        Predicate nonSpecialResultsVisibleTime = root.get("resultsVisibleFromTime").in(
                Const.TIME_REPRESENTS_FOLLOW_VISIBLE,
                Const.TIME_REPRESENTS_LATER,
                Const.TIME_REPRESENTS_NOW).not();

        cr.select(root)
                .where(cb.and(
                        cb.greaterThanOrEqualTo(root.get("resultsVisibleFromTime"),
                                now.minus(Const.FEEDBACK_SESSION_EVENT_EMAIL_LOOKBACK_WINDOW)),
                        cb.lessThanOrEqualTo(root.get("resultsVisibleFromTime"), now),
                        nonSpecialResultsVisibleTime,
                        cb.isFalse(root.get("isPublishedEmailSent")),
                        cb.isTrue(root.get("isPublishedEmailEnabled")),
                        cb.isNull(root.get("deletedAt")),
                        cb.isNull(courseJoin.get("deletedAt"))
               ));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of undeleted feedback sessions which opened recently
     * and need an open email to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsPossiblyNeedingOpenedEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);
        Join<FeedbackSession, Course> courseJoin = root.join("course");
        Instant now = Instant.now();

        cr.select(root)
                .where(cb.and(
                    cb.greaterThanOrEqualTo(root.get("startTime"),
                            now.minus(Const.FEEDBACK_SESSION_EVENT_EMAIL_LOOKBACK_WINDOW)),
                    cb.lessThanOrEqualTo(root.get("startTime"), now),
                    cb.greaterThan(root.get("endTime"), now),
                    cb.isFalse(root.get("isOpenedEmailSent")),
                    cb.isNull(root.get("deletedAt")),
                    cb.isNull(courseJoin.get("deletedAt"))
                ));

        return HibernateUtil.createQuery(cr).getResultList();
    }
}
