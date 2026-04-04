package teammates.ui.output;

import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.storage.sqlentity.Account;

/**
 * Output format of account data.
 */
public class AccountData extends ApiOutput {

    private final String googleId;
    private final String name;
    private final String email;
    private final Map<String, Long> readNotifications;

    @JsonCreator
    private AccountData(String googleId, String name, String email, Map<String, Long> readNotifications) {
        this.googleId = googleId;
        this.name = name;
        this.email = email;
        this.readNotifications = readNotifications;
    }

    public AccountData(Account account) {
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
