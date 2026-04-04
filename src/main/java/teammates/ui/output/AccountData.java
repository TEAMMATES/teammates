package teammates.ui.output;

import java.util.Map;
import java.util.stream.Collectors;

import teammates.storage.sqlentity.Account;

/**
 * Output format of account data.
 */
public class AccountData extends ApiOutput {

    private final String accountId;
    private final String loginIdentifier;
    private final String loginProvider;
    private final String name;
    private final String email;
    private final Map<String, Long> readNotifications;

    public AccountData(Account account, String loginIdentifier, String loginProvider) {
        this.accountId = account.getId().toString();
        this.loginIdentifier = loginIdentifier;
        this.loginProvider = loginProvider;
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

    public String getAccountId() {
        return accountId;
    }

    public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public String getLoginProvider() {
        return loginProvider;
    }

    public String getName() {
        return name;
    }

    public Map<String, Long> getReadNotifications() {
        return this.readNotifications;
    }

}
