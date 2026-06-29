package teammates.ui.request;

import java.util.UUID;

import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The request that links a logged-in account to the authenticated student.
 */
public class LinkAccountRequest extends BasicRequest {
    private UUID accountId;
    private UUID userId;

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(accountId != null, "Account ID cannot be null");
        validateTrue(userId != null, "User ID cannot be null");
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
