package teammates.client.scripts.sql;

import java.util.UUID;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.Notification;

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
    return true;
  }

  @Override
  protected boolean isMigrationNeeded(Notification entity) {
    return true;
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