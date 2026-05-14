package teammates.common.datatransfer;

import java.time.Instant;
import java.util.UUID;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

import tools.jackson.core.JacksonException;

/**
 * Represents user credential info to be persisted within cookies.
 */
public class UserInfoCookie {

    private String userId;

    private UUID accountId;

    private long expiryTime;

    private UserInfoCookie() {
        // for Jackson deserialization
    }

    public UserInfoCookie(String userId, UUID accountId) {
        assert accountId != null;

        this.userId = userId;
        this.accountId = accountId;
        this.expiryTime = Instant.now().plus(Const.COOKIE_VALIDITY_PERIOD).toEpochMilli();
    }

    /**
     * Gets a {@link UserInfoCookie} object from cookie string.
     */
    public static UserInfoCookie fromCookie(String cookie) {
        if (cookie == null) {
            return null;
        }
        try {
            String decryptedCookie = StringHelper.decrypt(cookie);
            return JsonUtils.fromJson(decryptedCookie, UserInfoCookie.class);
        } catch (InvalidParametersException | JacksonException e) {
            return null;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    /**
     * Sets the account ID.
     */
    public void setAccountId(UUID accountId) {
        assert accountId != null;
        this.accountId = accountId;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    /**
     * Returns true if the object represents a valid user info and the object has not expired.
     */
    public boolean isValid() {
        return userId != null
                && !userId.trim().isEmpty()
                && accountId != null
                && Instant.now().isBefore(Instant.ofEpochMilli(expiryTime));
    }

}
