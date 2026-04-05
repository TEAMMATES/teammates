package teammates.storage.sqlentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLInsert;


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
@SQLInsert(
        sql = """
                INSERT INTO read_notifications (account_id, created_at, notification_id, id)
                VALUES (?, ?, ?, ?) ON CONFLICT (account_id, notification_id) DO NOTHING
              """,
        check = ResultCheckStyle.NONE)
public class ReadNotification extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

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

    public void setAccount(Account account) {
        this.account = account;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
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
            return Objects.equals(this.getAccount(), otherReadNotification.getAccount())
                    && Objects.equals(this.getNotification(), otherReadNotification.getNotification());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getAccount(), this.getNotification());
    }

    @Override
    public String toString() {
        return "ReadNotification [id=" + id + ", account=" + account.getId() + ", notification=" + notification.getId()
                + "]";
    }
}
