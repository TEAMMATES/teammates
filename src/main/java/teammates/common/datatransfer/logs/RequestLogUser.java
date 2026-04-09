package teammates.common.datatransfer.logs;

import java.util.UUID;

/**
 * Represents a user who invokes the HTTP request.
 */
public class RequestLogUser {

    private String regkey;
    private String email;
    private UUID accountId;

    public String getRegkey() {
        return regkey;
    }

    public void setRegkey(String regkey) {
        this.regkey = regkey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

}
