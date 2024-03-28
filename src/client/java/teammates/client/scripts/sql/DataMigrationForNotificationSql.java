package teammates.client.scripts.sql;

import java.util.UUID;

import com.googlecode.objectify.cmd.Query;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Notification;

/**
 * Data migration class for notification entity.
 */
@SuppressWarnings("PMD")
public class DataMigrationForNotificationSql extends
        DataMigrationEntitiesBaseScriptSql<teammates.storage.entity.Notification, teammates.storage.sqlentity.Notification> {

    public static void main(String[] args) {
        new DataMigrationForNotificationSql().doOperationRemotely();
    }

    @Override
    protected Query<Notification> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.Notification.class);
    }

    @Override
    protected boolean isPreview() {
        return false;
    }

    /*
     * Sets the migration criteria used in isMigrationNeeded.
    */
    @Override
    protected void setMigrationCriteria() {
        // No migration criteria currently needed.
    }

    @Override
    protected boolean isMigrationNeeded(Notification entity) {
        HibernateUtil.beginTransaction();
        teammates.storage.sqlentity.Notification notification = HibernateUtil.get(
                teammates.storage.sqlentity.Notification.class, UUID.fromString(entity.getNotificationId()));
        HibernateUtil.commitTransaction();
        return notification == null;
    }

    @Override
    protected void migrateEntity(Notification oldNotification) throws Exception {
        teammates.storage.sqlentity.Notification newNotification = new teammates.storage.sqlentity.Notification(
                oldNotification.getStartTime(),
                oldNotification.getEndTime(),
                oldNotification.getStyle(),
                oldNotification.getTargetUser(),
                oldNotification.getTitle(),
                oldNotification.getMessage());

        try {
            UUID oldUuid = UUID.fromString(oldNotification.getNotificationId());
            newNotification.setId(oldUuid);
        } catch (Exception e) {
            // Auto-generated UUID from entity is created
        }

        if (oldNotification.isShown()) {
            newNotification.setShown();
        }
        saveEntityDeferred(newNotification);
    }
}
