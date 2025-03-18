package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.User;

/**
 * Handles CRUD operations for deadline extensions.
 *
 * @see DeadlineExtension
 */
public final class DeadlineExtensionsDb extends EntitiesDb {

    private static final DeadlineExtensionsDb instance = new DeadlineExtensionsDb();

    private DeadlineExtensionsDb() {
        // prevent initialization
    }

    public static DeadlineExtensionsDb inst() {
        return instance;
    }

    /**
     * Creates a deadline extension.
     */
    public DeadlineExtension createDeadlineExtension(DeadlineExtension de)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert de != null;

        if (!de.isValid()) {
            throw new InvalidParametersException(de.getInvalidityInfo());
        }

        if (getDeadlineExtension(de.getId()) != null) {
            throw new EntityAlreadyExistsException(
                    String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, de.toString()));
        }

        persist(de);
        return de;
    }

    /**
     * Gets a deadline extension by {@code id}.
     */
    public DeadlineExtension getDeadlineExtension(UUID id) {
        assert id != null;

        return HibernateUtil.get(DeadlineExtension.class, id);
    }

    /**
     * Get DeadlineExtension by {@code userId} and {@code feedbackSessionId}.
     */
    public DeadlineExtension getDeadlineExtension(UUID userId, UUID feedbackSessionId) {
        assert userId != null;
        assert feedbackSessionId != null;

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
     * Saves an updated {@code DeadlineExtension} to the db.
     *
     * @return updated deadline extension
     * @throws InvalidParametersException  if attributes to update are not valid
     * @throws EntityDoesNotExistException if the deadline extension cannot be found
     */
    public DeadlineExtension updateDeadlineExtension(DeadlineExtension deadlineExtension)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert deadlineExtension != null;

        if (!deadlineExtension.isValid()) {
            throw new InvalidParametersException(deadlineExtension.getInvalidityInfo());
        }

        if (getDeadlineExtension(deadlineExtension.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        return merge(deadlineExtension);
    }

    /**
     * Deletes a deadline extension.
     */
    public void deleteDeadlineExtension(DeadlineExtension de) {
        if (de != null) {
            delete(de);
        }
    }

    /**
     * Gets a list of deadline extensions with endTime coming up soon
     * and possibly need a closing soon email to be sent.
     */
    public List<DeadlineExtension> getDeadlineExtensionsPossiblyNeedingClosingSoonEmail() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<DeadlineExtension> cr = cb.createQuery(DeadlineExtension.class);
        Root<DeadlineExtension> root = cr.from(DeadlineExtension.class);

        cr.select(root).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("endTime"), Instant.now()),
                cb.lessThanOrEqualTo(root.get("endTime"), TimeHelper.getInstantDaysOffsetFromNow(1)),
                cb.equal(root.get("isClosingSoonEmailSent"), false)
                ));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets the DeadlineExtension with the specified {@code feedbackSessionId} and {@code userId} if it exists.
     * Otherwise, return null.
     */
    public DeadlineExtension getDeadlineExtensionForUser(UUID feedbackSessionId, UUID userId) {
        assert feedbackSessionId != null;
        assert userId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<DeadlineExtension> cr = cb.createQuery(DeadlineExtension.class);
        Root<DeadlineExtension> deadlineExtensionRoot = cr.from(DeadlineExtension.class);
        Join<DeadlineExtension, User> userJoin = deadlineExtensionRoot.join("user");
        Join<DeadlineExtension, FeedbackSession> sessionJoin = deadlineExtensionRoot.join("feedbackSession");

        cr.select(deadlineExtensionRoot).where(cb.and(
                cb.equal(sessionJoin.get("id"), feedbackSessionId),
                cb.equal(userJoin.get("id"), userId)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }
}
