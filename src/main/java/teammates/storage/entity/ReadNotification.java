package teammates.storage.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Represents an association class between Accounts and Notifications.
 * Keeps track of which Notifications have been read by an Account.
 */
@Entity
@Table(
        name = "ReadNotifications",
        uniqueConstraints = @UniqueConstraint(
                name = "Unique account_id and notification_id",
                columnNames = {"account_id", "notification_id"}))
public class ReadNotification extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "account_id", nullable = false, insertable = false, updatable = false)
    private UUID accountId;

    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Column(name = "notification_id", nullable = false, insertable = false, updatable = false)
    private UUID notificationId;

    protected ReadNotification() {
        // required by Hibernate
    }

    public ReadNotification(Account account, Notification notification) {
        this.setId(UUID.randomUUID());
        this.setAccount(account);
        this.setNotification(notification);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public UUID getAccountId() {
        return accountId;
    }

    /**
     * Sets the account of the read notification.
     */
    public void setAccount(Account account) {
        this.account = account;
        this.accountId = account == null ? null : account.getId();
    }

    public Notification getNotification() {
        return notification;
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the notification of the read notification.
     */
    public void setNotification(Notification notification) {
        this.notification = notification;
        this.notificationId = notification == null ? null : notification.getId();
    }

    @Override
    public List<String> getInvalidityInfo() {
        return new ArrayList<>();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            ReadNotification otherReadNotification = (ReadNotification) other;
            return Objects.equals(this.getId(), otherReadNotification.getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public String toString() {
        return "ReadNotification [id=" + id + ", account=" + account.getId() + ", notification=" + notification.getId()
                + "]";
    }
}
