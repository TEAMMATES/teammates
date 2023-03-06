package teammates.storage.sqlapi;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Notification;

/**
 * Handles CRUD operations for notifications.
 *
 * @see Notification
 */
public final class NotificationsDb extends EntitiesDb<Notification> {

    private static final NotificationsDb instance = new NotificationsDb();

    private NotificationsDb() {
        // prevent initialization
    }

    public static NotificationsDb inst() {
        return instance;
    }

    /**
     * Creates a notification.
     */
    public Notification createNotification(Notification notification)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert notification != null;

        if (!notification.isValid()) {
            throw new InvalidParametersException(notification.getInvalidityInfo());
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
        cq.select(root).where(cb.equal(root.get("targetUser"), targetUser));
        TypedQuery<Notification> query = HibernateUtil.createQuery(cq);
        return query.getResultList();
    }
}
