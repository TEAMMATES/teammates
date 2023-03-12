package teammates.storage.sqlentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Entity;
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
    private UUID id;

    @ManyToOne
    private Account account;

    @ManyToOne
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
