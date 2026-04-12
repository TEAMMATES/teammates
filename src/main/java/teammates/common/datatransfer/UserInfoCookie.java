package teammates.common.datatransfer;

import java.time.Instant;
import java.util.UUID;

import com.google.gson.JsonSyntaxException;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

/**
 * Represents user credential info to be persisted within cookies.
 */
public class UserInfoCookie {

    private UUID accountId;
    private String verificationCode;

    private long expiryTime;

    public UserInfoCookie(UUID accountId) {
        this.accountId = accountId;
        this.verificationCode = StringHelper.generateSignature(accountId.toString());
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

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
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
        if (accountId == null || StringHelper.isEmpty(verificationCode)) {
            return false;
        }
        return StringHelper.isCorrectSignature(accountId.toString(), verificationCode)
                && Instant.now().isBefore(Instant.ofEpochMilli(expiryTime));
    }

}
