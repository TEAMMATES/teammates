package teammates.ui.output;

import java.util.UUID;

import teammates.storage.sqlentity.Account;

/**
 * Output format of account data.
 */
public class AccountData extends ApiOutput {

    private final UUID accountId;
    private final String googleId;
    private final String name;
    private final String email;

    public AccountData(Account account) {
        this.accountId = account.getId();
        this.googleId = account.getGoogleId();
        this.name = account.getName();
        this.email = account.getEmail();
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

}
