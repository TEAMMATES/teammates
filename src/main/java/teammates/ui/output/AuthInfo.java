package teammates.ui.output;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.UserInfo;

/**
 * Authentication request format.
 */
public class AuthInfo extends ApiOutput {
    private final String loginUrl;
    @Nullable
    private final UserInfo user;
    private final boolean masquerade;

    public AuthInfo(String loginUrl, @Nullable UserInfo user, boolean masquerade) {
        this.loginUrl = loginUrl;
        this.user = user;
        this.masquerade = masquerade;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public UserInfo getUser() {
        return user;
    }

    public boolean isMasquerade() {
        return masquerade;
    }

}
