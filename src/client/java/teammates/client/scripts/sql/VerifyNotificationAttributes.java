package teammates.client.scripts.sql;

import teammates.storage.entity.Notification;

public class VerifyNotificationAttributes extends VerifyNonCourseEntityAttributesBaseScript<Notification, 
    teammates.storage.sqlentity.Notification> {
    
    static String dataStoreIdFieldName = "notificationId";

    public VerifyNotificationAttributes() {
        super(Notification.class, 
            teammates.storage.sqlentity.Notification.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Notification sqlEntity) {
        return sqlEntity.getId().toString();
    }

    public static void main(String[] args) {
        VerifyNotificationAttributes script = new VerifyNotificationAttributes();
        script.doOperationRemotely();
    }
}
