package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * Handles CRUD operations for feedback sessions.
 *
 * @see FeedbackSession
 */
public final class FeedbackSessionsDb extends EntitiesDb {

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
        assert fsId != null;

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
     * Gets a feedback session reference.
     *
     * @return Returns a proxy for the feedback session.
     */
    public FeedbackSession getFeedbackSessionReference(UUID id) {
        assert id != null;

        return HibernateUtil.getReference(FeedbackSession.class, id);
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
     * Restores a specific soft deleted feedback session.
     */
    public void restoreDeletedFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
        assert courseId != null;
        assert feedbackSessionName != null;

        FeedbackSession sessionEntity = getFeedbackSession(feedbackSessionName, courseId);

        if (sessionEntity == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        sessionEntity.setDeletedAt(null);
        merge(sessionEntity);
    }

    /**
     * Creates a feedback session.
     */
    public FeedbackSession createFeedbackSession(FeedbackSession session)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert session != null;

        if (!session.isValid()) {
            throw new InvalidParametersException(session.getInvalidityInfo());
        }

        if (getFeedbackSession(session.getId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, session.toString()));
        }

        persist(session);
        return session;
    }

    /**
     * Saves an updated {@code FeedbackSession} to the db.
     *
     * @return updated feedback session
     * @throws InvalidParametersException  if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSession updateFeedbackSession(FeedbackSession feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert feedbackSession != null;

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        if (getFeedbackSession(feedbackSession.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        return merge(feedbackSession);
    }

    /**
     * Deletes a feedback session.
     */
    public void deleteFeedbackSession(FeedbackSession feedbackSession) {
        if (feedbackSession != null) {
            delete(feedbackSession);
        }
    }

    /**
     * Soft-deletes a specific feedback session by its name and course id.
     *
     * @return the feedback session.
     */
    public FeedbackSession softDeleteFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
        assert courseId != null;
        assert feedbackSessionName != null;

        FeedbackSession feedbackSessionEntity = getFeedbackSession(feedbackSessionName, courseId);

        if (feedbackSessionEntity == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        feedbackSessionEntity.setDeletedAt(Instant.now());
        merge(feedbackSessionEntity);

        return feedbackSessionEntity;
    }

    /**
     * Gets feedback sessions for a given {@code courseId}.
     */
    public List<FeedbackSession> getFeedbackSessionEntitiesForCourse(String courseId) {
        assert courseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cq = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cq.from(FeedbackSession.class);
        Join<FeedbackSession, Course> courseJoin = root.join("course");

        cq.select(root).where(cb.equal(courseJoin.get("id"), courseId));

        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Gets feedback sessions for a given {@code courseId} that start after {@code after}.
     */
    public List<FeedbackSession> getFeedbackSessionEntitiesForCourseStartingAfter(String courseId, Instant after) {
        assert courseId != null;
        assert after != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);
        Join<FeedbackSession, Course> courseJoin = root.join("course");
        cr.select(root)
                .where(cb.and(
                    cb.greaterThanOrEqualTo(root.get("startTime"), after),
                    cb.equal(courseJoin.get("id"), courseId)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of undeleted feedback sessions which open in the future
     * and possibly need a opening soon email to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsPossiblyNeedingOpeningSoonEmail() {
        return getFeedbackSessionEntitiesPossiblyNeedingOpeningSoonEmail().stream()
                .filter(session -> session.getDeletedAt() == null)
                .collect(Collectors.toList());
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingOpeningSoonEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);

        cr.select(root)
                .where(cb.and(
                    cb.greaterThan(root.get("startTime"), TimeHelper.getInstantDaysOffsetFromNow(-2)),
                    cb.equal(root.get("isOpeningSoonEmailSent"), false)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of undeleted feedback sessions which end in the future (2 hour ago onward)
     * and possibly need a closing soon email to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsPossiblyNeedingClosingSoonEmail() {
        return getFeedbackSessionEntitiesPossiblyNeedingClosingSoonEmail().stream()
                .filter(session -> session.getDeletedAt() == null)
                .collect(Collectors.toList());
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingClosingSoonEmail() {
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
                                cb.equal(root.get("isClosedEmailSent"), false))
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
        return getFeedbackSessionEntitiesPossiblyNeedingPublishedEmail().stream()
                .filter(session -> session.getDeletedAt() == null)
                .collect(Collectors.toList());
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingPublishedEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);

        cr.select(root)
                .where(cb.and(
                        cb.greaterThan(root.get("resultsVisibleFromTime"), TimeHelper.getInstantDaysOffsetFromNow(-2)),
                        cb.and(
                                cb.equal(root.get("isPublishedEmailSent"), false),
                                cb.equal(root.get("isPublishedEmailEnabled"), true))
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
