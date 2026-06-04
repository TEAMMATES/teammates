package teammates.storage.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.User;

/**
 * Handles CRUD operations for deadline extensions.
 *
 * @see DeadlineExtension
 */
public final class DeadlineExtensionsDb {

    private static final DeadlineExtensionsDb instance = new DeadlineExtensionsDb();

    private DeadlineExtensionsDb() {
        // prevent initialization
    }

    public static DeadlineExtensionsDb inst() {
        return instance;
    }

    /**
     * Persists a deadline extension.
     */
    public DeadlineExtension persistDeadlineExtension(DeadlineExtension de) {
        HibernateUtil.persist(de);
        return de;
    }

    /**
     * Gets a deadline extension by {@code id}.
     */
    public DeadlineExtension getDeadlineExtension(UUID id) {
        return HibernateUtil.get(DeadlineExtension.class, id);
    }

    /**
     * Get DeadlineExtension by {@code userId} and {@code feedbackSessionId}.
     */
    public DeadlineExtension getDeadlineExtension(UUID userId, UUID feedbackSessionId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<DeadlineExtension> cr = cb.createQuery(DeadlineExtension.class);
        Root<DeadlineExtension> root = cr.from(DeadlineExtension.class);
        Join<DeadlineExtension, FeedbackSession> deFsJoin = root.join("feedbackSession");
        Join<DeadlineExtension, User> deUserJoin = root.join("user");

        cr.select(root).where(cb.and(
                cb.equal(deFsJoin.get("id"), feedbackSessionId),
                cb.equal(deUserJoin.get("id"), userId)));

        TypedQuery<DeadlineExtension> query = HibernateUtil.createQuery(cr);
        return query.getResultStream().findFirst().orElse(null);
    }

    /**
     * Removes a deadline extension.
     */
    public void removeDeadlineExtension(DeadlineExtension de) {
        HibernateUtil.remove(de);
    }

    /**
     * Gets a list of deadline extensions with endTime coming up soon
     * and possibly need a closing soon email to be sent.
     */
    public List<DeadlineExtension> getDeadlineExtensionsPossiblyNeedingClosingSoonEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<DeadlineExtension> cr = cb.createQuery(DeadlineExtension.class);
        Root<DeadlineExtension> root = cr.from(DeadlineExtension.class);
        Join<DeadlineExtension, FeedbackSession> feedbackSessionJoin = root.join("feedbackSession");
        Join<FeedbackSession, Course> courseJoin = feedbackSessionJoin.join("course");

        cr.select(root).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("endTime"), Instant.now()),
                cb.lessThanOrEqualTo(root.get("endTime"), TimeHelper.getInstantDaysOffsetFromNow(1)),
                cb.isFalse(root.get("isClosingSoonEmailSent")),
                cb.isTrue(feedbackSessionJoin.get("isClosingSoonEmailEnabled")),
                cb.isNull(feedbackSessionJoin.get("deletedAt")),
                cb.isNull(courseJoin.get("deletedAt"))
                ));

        return HibernateUtil.createQuery(cr).getResultList();
    }
}
