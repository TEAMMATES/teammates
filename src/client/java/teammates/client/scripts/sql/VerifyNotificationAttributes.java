package teammates.client.scripts.sql;

import java.util.UUID;

import teammates.storage.entity.Notification;

/**
 * Class for verifying notification attributes.
 */
@SuppressWarnings("PMD")
public class VerifyNotificationAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<Notification, teammates.storage.sqlentity.Notification> {

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
        try {
            UUID otherUuid = UUID.fromString(datastoreEntity.getNotificationId());
            return sqlEntity.getId().equals(otherUuid)
                    && sqlEntity.getStartTime().equals(datastoreEntity.getStartTime())
                    && sqlEntity.getEndTime().equals(datastoreEntity.getEndTime())
                    && sqlEntity.getStyle().equals(datastoreEntity.getStyle())
                    && sqlEntity.getTargetUser().equals(datastoreEntity.getTargetUser())
                    && sqlEntity.getTitle().equals(datastoreEntity.getTitle())
                    && sqlEntity.getMessage().equals(datastoreEntity.getMessage())
                    && sqlEntity.isShown() == datastoreEntity.isShown();
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    public static void main(String[] args) {
        VerifyNotificationAttributes script = new VerifyNotificationAttributes();
        script.doOperationRemotely();
    }
}
