package teammates.ui.output;

import java.util.List;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.UserInfo;
import teammates.common.util.Config;

/**
 * Authentication request format.
 */
public class AuthInfo extends ApiOutput {

    @Nullable
    private final String studentLoginUrl;
    @Nullable
    private final String instructorLoginUrl;
    @Nullable
    private final String adminLoginUrl;
    @Nullable
    private final String maintainerLoginUrl;
    @Nullable
    private final UserInfo user;
    private final boolean masquerade;
    private final List<String> authTypes;

    public AuthInfo(String studentLoginUrl, String instructorLoginUrl, String adminLoginUrl, String maintainerLoginUrl) {
        this.studentLoginUrl = studentLoginUrl;
        this.instructorLoginUrl = instructorLoginUrl;
        this.adminLoginUrl = adminLoginUrl;
        this.maintainerLoginUrl = maintainerLoginUrl;
        this.user = null;
        this.masquerade = false;
        this.authTypes = Config.AUTH_TYPES;
    }

    public AuthInfo(UserInfo user, boolean masquerade) {
        this.studentLoginUrl = null;
        this.instructorLoginUrl = null;
        this.adminLoginUrl = null;
        this.maintainerLoginUrl = null;
        this.user = user;
        this.masquerade = masquerade;
        this.authTypes = Config.AUTH_TYPES;
    }

    public String getStudentLoginUrl() {
        return studentLoginUrl;
    }

    public String getInstructorLoginUrl() {
        return instructorLoginUrl;
    }

    public String getAdminLoginUrl() {
        return adminLoginUrl;
    }

    public String getMaintainerLoginUrl() {
        return maintainerLoginUrl;
    }

    public UserInfo getUser() {
        return user;
    }

    public boolean isMasquerade() {
        return masquerade;
    }

    public List<String> getAuthTypes() {
        return authTypes;
    }
}
