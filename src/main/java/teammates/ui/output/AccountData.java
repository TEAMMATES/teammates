package teammates.ui.output;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.storage.sqlentity.Account;

/**
 * Output format of account data.
 */
public class AccountData extends ApiOutput {

    private final UUID accountId;
    private final String googleId;
    private final String name;
    private final String email;
    private final Map<String, Long> readNotifications;

    public AccountData(Account account) {
        this.accountId = account.getId();
        this.googleId = account.getGoogleId();
        this.name = account.getName();
        this.email = account.getEmail();
        this.readNotifications = account.getReadNotifications()
                .stream()
                .collect(Collectors.toMap(
                        readNotification -> readNotification.getNotification().getId().toString(),
                        readNotification ->
                                readNotification.getNotification().getEndTime().toEpochMilli()));
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getName() {
        return name;
    }

    public Map<String, Long> getReadNotifications() {
        return this.readNotifications;
    }

}
