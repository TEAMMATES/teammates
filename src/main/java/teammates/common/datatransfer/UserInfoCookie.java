package teammates.common.datatransfer;

import java.time.Instant;

import com.google.gson.JsonSyntaxException;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

/**
 * Represents user credential info to be persisted within cookies.
 */
public class UserInfoCookie {

    private String userId;

    private long expiryTime;

    public UserInfoCookie(String userId) {
        this.userId = userId;
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
        } catch (InvalidParametersException | JsonSyntaxException e) {
            return null;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        return !StringHelper.isEmpty(userId)
                && Instant.now().isBefore(Instant.ofEpochMilli(expiryTime));
    }

}
