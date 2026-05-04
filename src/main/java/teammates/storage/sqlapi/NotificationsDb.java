package teammates.storage.sqlapi;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;

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
     * Gets active notifications by {@code targetUser} that have not been read by the account
     * with the given {@code accountId}.
     *
     * @return a list of unread active notifications for the specified targetUser and account.
     */
    public List<Notification> getUnreadActiveNotificationsByTargetUser(
            List<NotificationTargetUser> targetUsers, UUID accountId, Instant now) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Notification> cq = cb.createQuery(Notification.class);
        Root<Notification> root = cq.from(Notification.class);

        Subquery<Integer> subquery = cq.subquery(Integer.class);
        Root<ReadNotification> readRoot = subquery.from(ReadNotification.class);
        subquery.select(cb.literal(1))
                .where(cb.and(
                        cb.equal(readRoot.get("notification").get("id"), root.get("id")),
                        cb.equal(readRoot.get("account").get("id"), accountId)));

        cq.select(root)
                .where(cb.and(
                        root.get("targetUser").in(targetUsers),
                        cb.lessThanOrEqualTo(root.get("startTime"), now),
                        cb.greaterThanOrEqualTo(root.get("endTime"), now),
                        cb.not(cb.exists(subquery))))
                .orderBy(cb.asc(root.get("startTime")));
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
