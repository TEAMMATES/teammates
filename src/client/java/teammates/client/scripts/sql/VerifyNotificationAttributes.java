package teammates.client.scripts.sql;

import java.util.UUID;

import teammates.storage.entity.Notification;

/**
 * Class for verifying notification attributes.
 */
public class VerifyNotificationAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<Notification, teammates.storage.sqlentity.Notification> {

    static String dataStoreIdFieldName = "notificationId";

    public VerifyNotificationAttributes() {
        super(Notification.class,
                teammates.storage.sqlentity.Notification.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Notification sqlEntity) {
        return sqlEntity.getId().toString();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Notification sqlEntity, Notification datastoreEntity) {
        if (datastoreEntity instanceof teammates.storage.entity.Notification) {
            teammates.storage.entity.Notification notif = (teammates.storage.entity.Notification) datastoreEntity;
            try {
                UUID otherUuid = UUID.fromString(notif.getNotificationId());
                return sqlEntity.getId().equals(otherUuid)
                        && sqlEntity.getStartTime().equals(notif.getStartTime())
                        && sqlEntity.getEndTime().equals(notif.getEndTime())
                        && sqlEntity.getStyle().equals(notif.getStyle())
                        && sqlEntity.getTargetUser().equals(notif.getTargetUser())
                        && sqlEntity.getTitle().equals(notif.getTitle())
                        && sqlEntity.getMessage().equals(notif.getMessage())
                        && sqlEntity.isShown() == notif.isShown();
            } catch (IllegalArgumentException iae) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        VerifyNotificationAttributes script = new VerifyNotificationAttributes();
        script.doOperationRemotely();
    }
}
