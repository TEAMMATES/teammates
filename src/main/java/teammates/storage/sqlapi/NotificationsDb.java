package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Notification;

/**
 * Handles CRUD operations for notifications.
 *
 * @see Notification
 */
public final class NotificationsDb extends EntitiesDb {

    private static final NotificationsDb instance = new NotificationsDb();

    private NotificationsDb() {
        // prevent initialization
    }

    public static NotificationsDb inst() {
        return instance;
    }

    /**
     * Creates a notification.
     *
     * <p>Preconditions:</p>
     * * Notification fields are valid.
     */
    public Notification createNotification(Notification notification) throws EntityAlreadyExistsException {
        assert notification != null;

        if (getNotification(notification.getId()) != null) {
            throw new EntityAlreadyExistsException(
                    String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, notification.toString()));
        }

        persist(notification);
        return notification;
    }

    /**
     * Gets a notification by its unique ID.
     */
    public Notification getNotification(UUID notificationId) {
        assert notificationId != null;

        return HibernateUtil.get(Notification.class, notificationId);
    }

    /**
     * Deletes a notification.
     *
     * <p>Fails silently if notification is null.
     */
    public void deleteNotification(Notification notification) {
        if (notification != null) {
            delete(notification);
        }
    }

    /**
     * Gets all notifications.
     */
    public List<Notification> getAllNotifications() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Notification> cq = cb.createQuery(Notification.class);
        Root<Notification> root = cq.from(Notification.class);
        CriteriaQuery<Notification> all = cq.select(root);
        TypedQuery<Notification> allQuery = HibernateUtil.createQuery(all);
        return allQuery.getResultList();
    }

    /**
     * Gets notifications by {@code targetUser}.
     *
     * @return a list of notifications for the specified targetUser.
     */
    public List<Notification> getActiveNotificationsByTargetUser(NotificationTargetUser targetUser) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Notification> cq = cb.createQuery(Notification.class);
        Root<Notification> root = cq.from(Notification.class);
        cq.select(root)
                .where(cb.and(
                        cb.or(cb.equal(root.get("targetUser"), targetUser),
                                cb.equal(root.get("targetUser"), NotificationTargetUser.GENERAL)),
                        cb.lessThanOrEqualTo(root.get("startTime"), Instant.now()),
                        cb.greaterThanOrEqualTo(root.get("endTime"), Instant.now())))
                .orderBy(cb.asc(root.get("startTime")));
        TypedQuery<Notification> query = HibernateUtil.createQuery(cq);
        return query.getResultList();
    }

    /**
     * Updates a notification.
     *
     * <p>Preconditions:</p>
     * * Notification fields are valid.
     */
    public Notification updateNotification(Notification notification) throws EntityDoesNotExistException {
        assert notification != null;

        if (getNotification(notification.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + Notification.class);
        }

        return merge(notification);
    }

}
