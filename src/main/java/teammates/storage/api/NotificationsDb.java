package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.Notification;

/**
 * Handles CRUD operations for notifications.
 *
 * @see Notification
 * @see NotificationAttributes
 */
public final class NotificationsDb extends EntitiesDb<Notification, NotificationAttributes> {

    private static final NotificationsDb instance = new NotificationsDb();

    private NotificationsDb() {
        // prevent initialization
    }

    public static NotificationsDb inst() {
        return instance;
    }

    /**
     * Gets a notification by its unique ID.
     */
    public NotificationAttributes getNotification(String notificationId) {
        assert notificationId != null;

        return notificationId.isEmpty() ? null : makeAttributesOrNull(getNotificationEntity(notificationId));
    }

    /**
     * Gets notifications by {@code targetUser}.
     *
     * @return a list of notifications for the specified targetUser.
     */
    public List<NotificationAttributes> getNotificationsByTargetUser(NotificationTargetUser targetUser) {
        assert targetUser != null;

        List<Notification> endEntities = load()
                .filter("targetUser=", Const.NotificationTargetUser.GENERAL)
                .filter("endTime >", Instant.now())
                .list();

        List<Notification> startEntities = load()
                .filter("targetUser=", Const.NotificationTargetUser.GENERAL)
                .filter("startTime <", Instant.now())
                .list();

        if (targetUser != NotificationTargetUser.GENERAL) {
            endEntities.addAll(load()
                    .filter("targetUser=", targetUser.toSingularFormString())
                    .filter("endTime >", Instant.now())
                    .list());
            startEntities.addAll(load()
                .filter("targetUser=", Const.NotificationTargetUser.GENERAL)
                .filter("startTime <", Instant.now())
                .list());
        }

        List<String> startEntitiesIds = startEntities.stream()
                .map(notif -> notif.getNotificationId())
                .collect(Collectors.toList());

        List<Notification> ongoingNotifications = endEntities.stream()
                .filter(notif -> {
                    String id = notif.getNotificationId();
                    return startEntitiesIds.contains(id);
                })
                .collect(Collectors.toList());

        List<NotificationAttributes> notificationAttributes = makeAttributes(ongoingNotifications);
        NotificationAttributes.sortByStartTime(notificationAttributes);
        return notificationAttributes;

    }

    /**
     * Updates a notification with {@link NotificationAttributes.UpdateOptions}.
     *
     * @return updated notification
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if notification cannot be found
     */
    public NotificationAttributes updateNotification(NotificationAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        Notification notification = getNotificationEntity(updateOptions.getNotificationId());
        if (notification == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        NotificationAttributes newAttributes = makeAttributes(notification);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        saveEntity(notification);

        return makeAttributes(notification);
    }

    /**
     * Deletes a notification by its unique ID.
     *
     * <p>Fails silently if there is no such notification.
     */
    public void deleteNotification(String notificationId) {
        assert notificationId != null;

        deleteEntity(Key.create(Notification.class, notificationId));
    }

    private Notification getNotificationEntity(String notificationId) {
        Notification notification = load().id(notificationId).now();
        if (notification == null) {
            return null;
        }

        return notification;
    }

    @Override
    LoadType<Notification> load() {
        return ofy().load().type(Notification.class);
    }

    @Override
    boolean hasExistingEntities(NotificationAttributes entityToCreate) {
        Key<Notification> keyToFind = Key.create(Notification.class, entityToCreate.getNotificationId());
        return !load().filterKey(keyToFind).keys().list().isEmpty();
    }

    @Override
    NotificationAttributes makeAttributes(Notification entity) {
        assert entity != null;

        return NotificationAttributes.valueOf(entity);
    }
}
