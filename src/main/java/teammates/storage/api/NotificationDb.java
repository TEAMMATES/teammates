package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.Notification;

/**
 * Handles CRUD operations for notifications.
 *
 * @see Notification
 * @see NotificationAttributes
 */
public final class NotificationDb extends EntitiesDb<Notification, NotificationAttributes> {

    private static final NotificationDb instance = new NotificationDb();

    private NotificationDb() {
        // prevent initialization
    }

    public static NotificationDb inst() {
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
