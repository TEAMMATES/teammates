package teammates.client.scripts.sql;

import teammates.storage.entity.Notification;
import teammates.storage.sqlentity.BaseEntity;

public class VerifyNotificationAttributes extends VerifyNonCourseEntityAttributesBaseScript<Notification, 
    teammates.storage.sqlentity.Notification> {
    
    static String dataStoreIdFieldName = "notificationId";

    public VerifyNotificationAttributes() {
        super(VerifyNotificationAttributes.dataStoreIdFieldName,
            Notification.class,
            teammates.storage.sqlentity.Notification.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Notification sqlEntity) {
        return sqlEntity.getId().toString();
    }
}
