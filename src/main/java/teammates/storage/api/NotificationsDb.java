package teammates.storage.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;

/**
 * Handles CRUD operations for notifications.
 *
 * @see Notification
 */
public final class NotificationsDb {

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
    public Notification createNotification(Notification notification) {
        assert notification != null;

        HibernateUtil.persist(notification);
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
     */
    public void deleteNotification(Notification notification) {
        HibernateUtil.remove(notification);
    }

    /**
     * Gets all notifications by {@code targetUsers}.
     *
     * @return a list of notifications for the specified targetUsers.
     */
    public List<Notification> getNotificationsByTargetUsers(
            List<NotificationTargetUser> targetUsers, boolean isActive) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Notification> cq = cb.createQuery(Notification.class);
        Root<Notification> root = cq.from(Notification.class);

        if (isActive) {
            Instant now = Instant.now();
            cq.select(root)
                    .where(cb.and(
                            root.get("targetUser").in(targetUsers),
                            cb.lessThanOrEqualTo(root.get("startTime"), now),
                            cb.greaterThanOrEqualTo(root.get("endTime"), now)));
        } else {
            cq.select(root)
                    .where(root.get("targetUser").in(targetUsers));
        }

        cq.orderBy(cb.asc(root.get("startTime")));
        TypedQuery<Notification> query = HibernateUtil.createQuery(cq);
        return query.getResultList();
    }

    /**
     * Creates a read notification.
     */
    public ReadNotification createReadNotification(ReadNotification readNotification) {
        HibernateUtil.persist(readNotification);
        return readNotification;
    }

    /**
     * Gets a read notification by its unique ID.
     */
    public ReadNotification getReadNotification(UUID readNotificationId) {
        return HibernateUtil.get(ReadNotification.class, readNotificationId);
    }

    /**
     * Gets read notifications by account ID.
     *
     * @return a list of read notifications for the specified account ID.
     */
    public List<ReadNotification> getReadNotificationsByAccountId(UUID accountId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<ReadNotification> cq = cb.createQuery(ReadNotification.class);
        Root<ReadNotification> root = cq.from(ReadNotification.class);
        cq.select(root).where(cb.equal(root.get("account").get("id"), accountId));
        TypedQuery<ReadNotification> query = HibernateUtil.createQuery(cq);
        return query.getResultList();
    }

    /**
     * Deletes a read notification.
     */
    public void deleteReadNotification(ReadNotification readNotification) {
        HibernateUtil.remove(readNotification);
    }
}
