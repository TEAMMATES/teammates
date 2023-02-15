package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents an association class between Accounts and Notifications.
 * Keeps track of which Notifications have been read by an Account.
 */
@Entity
@Table(name = "ReadNotifications")
public class ReadNotification extends BaseEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Notification notification;

    @Column(nullable = false)
    private Instant readAt;

    protected ReadNotification() {
        // required by Hibernate
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
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
    public void sanitizeForSaving() {
        // No sanitization required
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            ReadNotification otherReadNotifiation = (ReadNotification) other;
            return Objects.equals(this.account, otherReadNotifiation.account)
                    && Objects.equals(this.notification, otherReadNotifiation.notification)
                    && Objects.equals(this.readAt, otherReadNotifiation.readAt)
                    && Objects.equals(this.id, otherReadNotifiation.id);
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
        return "ReadNotification [id=" + id + ", account=" + account + ", notification=" + notification + ", readAt="
                + readAt + "]";
    }
}
