package teammates.ui.output;

import teammates.common.datatransfer.ReadNotifications;
import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * Output format of account data.
 */
public class AccountData extends ApiOutput {

    private final String googleId;
    private final String name;
    private final String email;
    private final ReadNotifications readNotifications;

    public AccountData(AccountAttributes accountInfo) {
        this.googleId = accountInfo.getGoogleId();
        this.name = accountInfo.getName();
        this.email = accountInfo.getEmail();
        this.readNotifications = accountInfo.getReadNotifications();
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

    public ReadNotifications getReadNotifications() {
        return this.readNotifications;
    }

}
