package teammates.ui.output;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.UserInfo;

/**
 * Authentication request format.
 */
public class AuthInfo implements ApiOutput {
    @Nullable
    private final UserInfo user;
    private final boolean masquerade;

    public AuthInfo(@Nullable UserInfo user, boolean masquerade) {
        this.user = user;
        this.masquerade = masquerade;
    }

    public UserInfo getUser() {
        return user;
    }

    public boolean isMasquerade() {
        return masquerade;
    }

}
