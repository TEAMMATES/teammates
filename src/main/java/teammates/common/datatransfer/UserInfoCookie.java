package teammates.common.datatransfer;

import com.google.gson.JsonSyntaxException;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

/**
 * Represents user credential info to be persisted within cookies.
 */
public class UserInfoCookie {

    private String userId;
    private String verificationCode;

    public UserInfoCookie(String userId) {
        this.userId = userId;
        this.verificationCode = StringHelper.generateSignature(userId);
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

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    /**
     * Returns true if the object represents a valid user info.
     */
    public boolean isValid() {
        return StringHelper.isCorrectSignature(userId, verificationCode);
    }

}
