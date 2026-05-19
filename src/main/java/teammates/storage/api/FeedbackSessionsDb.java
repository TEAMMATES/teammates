package teammates.storage.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;

/**
 * Handles CRUD operations for feedback sessions.
 *
 * @see FeedbackSession
 */
public final class FeedbackSessionsDb {

    private static final Logger log = Logger.getLogger();
    private static final FeedbackSessionsDb instance = new FeedbackSessionsDb();

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
     * Gets a feedback session for {@code feedbackSessionName} and {@code courseId}.
     *
     * @return null if not found
     */
    public FeedbackSession getFeedbackSession(String feedbackSessionName, String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cq = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> fsRoot = cq.from(FeedbackSession.class);
        Join<FeedbackSession, Course> fsJoin = fsRoot.join("course");
        cq.select(fsRoot).where(cb.and(
                cb.equal(fsRoot.get("name"), feedbackSessionName),
                cb.equal(fsJoin.get("id"), courseId)));
        return HibernateUtil.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets a soft-deleted feedback session.
     *
     * @return null if not found or not soft-deleted.
     */
    public FeedbackSession getSoftDeletedFeedbackSession(String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        FeedbackSession feedbackSession = getFeedbackSession(feedbackSessionName, courseId);

        if (feedbackSession != null && feedbackSession.getDeletedAt() == null) {
            log.info(feedbackSessionName + "/" + courseId + " is not soft-deleted!");
            return null;
        }

        return feedbackSession;
    }

    /**
     * Gets soft-deleted feedback sessions for course.
     */
    public List<FeedbackSession> getSoftDeletedFeedbackSessionsForCourse(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cq = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> fsRoot = cq.from(FeedbackSession.class);
        Join<FeedbackSession, Course> fsJoin = fsRoot.join("course");
        cq.select(fsRoot).where(cb.and(
                cb.isNotNull(fsRoot.get("deletedAt")),
                cb.equal(fsJoin.get("id"), courseId)));
        return HibernateUtil.createQuery(cq).getResultList();
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
        assert rangeStart != null;
        assert rangeEnd != null;
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
     * Creates a feedback session.
     */
    public FeedbackSession createFeedbackSession(FeedbackSession session) {
        assert session != null;

        HibernateUtil.persist(session);
        return session;
    }

    /**
     * Deletes a feedback session.
     */
    public void deleteFeedbackSession(FeedbackSession feedbackSession) {
        HibernateUtil.remove(feedbackSession);
    }

    /**
     * Gets non-soft-deleted feedback sessions for a given {@code courseId}.
     * Includes sessions from soft-deleted courses.
     */
    public List<FeedbackSession> getFeedbackSessionsForCourse(String courseId) {
        assert courseId != null;

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
        assert courseId != null;
        assert after != null;

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
     * Gets a list of undeleted feedback sessions which open in the future
     * and possibly need a opening soon email to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsPossiblyNeedingOpeningSoonEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);

        cr.select(root)
                .where(cb.and(
                    cb.greaterThan(root.get("startTime"), TimeHelper.getInstantDaysOffsetFromNow(-2)),
                    cb.equal(root.get("isOpeningSoonEmailSent"), false),
                    cb.isNull(root.get("deletedAt"))
                ));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of undeleted feedback sessions which end in the future (2 hour ago onward)
     * and possibly need a closing soon email to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsPossiblyNeedingClosingSoonEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);

        cr.select(root)
                .where(cb.and(
                        // Retrieve sessions with endTime from 2 days ago onwards
                        // to prevent issues caused by time zone differences
                        cb.greaterThan(root.get("endTime"), TimeHelper.getInstantDaysOffsetFromNow(-2)),
                        cb.and(
                                cb.equal(root.get("isClosingSoonEmailSent"), false),
                                cb.equal(root.get("isClosingSoonEmailEnabled"), true),
                                cb.equal(root.get("isClosedEmailSent"), false),
                                cb.isNull(root.get("deletedAt")))
               ));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of undeleted feedback sessions which end in the future (2 hour ago onward)
     * and possibly need a closed email to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsPossiblyNeedingClosedEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);

        cr.select(root)
                .where(cb.and(
                        cb.greaterThan(root.get("endTime"), TimeHelper.getInstantDaysOffsetFromNow(-2)),
                        cb.isFalse(root.get("isClosedEmailSent")),
                        cb.isTrue(root.get("isClosingSoonEmailEnabled")),
                        cb.isNull(root.get("deletedAt"))
               ));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of undeleted published feedback sessions which possibly need a published email
     * to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsPossiblyNeedingPublishedEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);

        cr.select(root)
                .where(cb.and(
                        cb.greaterThan(root.get("resultsVisibleFromTime"), TimeHelper.getInstantDaysOffsetFromNow(-2)),
                        cb.and(
                                cb.equal(root.get("isPublishedEmailSent"), false),
                                cb.equal(root.get("isPublishedEmailEnabled"), true),
                                cb.isNull(root.get("deletedAt")))
               ));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of undeleted feedback sessions which start within the last 2 days
     * and possibly need an open email to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsPossiblyNeedingOpenedEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);

        cr.select(root)
                .where(cb.and(
                    cb.greaterThan(root.get("startTime"), TimeHelper.getInstantDaysOffsetFromNow(-2)),
                    cb.isFalse(root.get("isOpenedEmailSent")),
                    cb.isNull(root.get("deletedAt"))
                ));

        return HibernateUtil.createQuery(cr).getResultList();
    }
}
