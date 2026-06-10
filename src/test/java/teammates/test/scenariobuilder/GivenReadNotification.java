package teammates.test.scenariobuilder;

import java.util.UUID;

import teammates.storage.entity.Account;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;

/**
 * Builder for ReadNotification entities used in test scenarios.
 */
public final class GivenReadNotification extends GivenBase<ReadNotification> {
    public GivenReadNotification(GivenData given, UUID readNotificationId) {
        super(given);
        this.entity = defaultReadNotification(readNotificationId);
    }

    /**
     * Sets the account for the read notification.
     */
    public GivenReadNotification account(String accountAlias) {
        assert entity.getAccount() == null : "Account has already been set for this read notification";
        Account account = given.getOrCreate(accountAlias, given.dataBundle.accounts, given::account);
        entity.setAccount(account);
        return this;
    }

    /**
     * Sets the notification for the read notification.
     */
    public GivenReadNotification notification(String notificationAlias) {
        assert entity.getNotification() == null : "Notification has already been set for this read notification";
        Notification notification = given.getOrCreate(
                notificationAlias, given.dataBundle.notifications, given::notification);
        notification.addReadNotification(entity);
        return this;
    }

    @Override
    void ensureConsistent() {
        if (entity.getAccountId() == null) {
            this.account(GivenAccount.getDefaultAlias("read-notification-account:" + entity.getId()));
        }

        if (entity.getNotificationId() == null) {
            this.notification(GivenNotification.getDefaultAlias());
        }
    }

    private ReadNotification defaultReadNotification(UUID readNotificationId) {
        ReadNotification readNotification = new ReadNotification();
        readNotification.setId(readNotificationId);
        return readNotification;
    }
}
