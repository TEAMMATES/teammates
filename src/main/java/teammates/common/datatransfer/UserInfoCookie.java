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
    private boolean admin;
    private String verificationCode;

    public UserInfoCookie(String userId, boolean admin) {
        this.userId = userId;
        this.admin = admin;
        this.verificationCode = StringHelper.generateSignature(userId + "_" + admin);
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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
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
        return StringHelper.isCorrectSignature(userId + "_" + admin, verificationCode);
    }

}
