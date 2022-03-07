package teammates.ui.output;

import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * Output format of account data.
 */
public class AccountData extends ApiOutput {

    private final String googleId;
    private final String name;
    private final String email;
    private final String notificationReadStatuses;

    public AccountData(AccountAttributes accountInfo) {
        this.googleId = accountInfo.getGoogleId();
        this.name = accountInfo.getName();
        this.email = accountInfo.getEmail();
        this.notificationReadStatuses = accountInfo.getNotificationReadStatuses();
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

    public String getNotificationReadStatuses() {
        return this.notificationReadStatuses;
    }

}
