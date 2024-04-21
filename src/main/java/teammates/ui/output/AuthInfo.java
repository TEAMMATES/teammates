package teammates.ui.output;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.UserInfo;

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

    public AuthInfo(String studentLoginUrl, String instructorLoginUrl, String adminLoginUrl, String maintainerLoginUrl) {
        this.studentLoginUrl = studentLoginUrl;
        this.instructorLoginUrl = instructorLoginUrl;
        this.adminLoginUrl = adminLoginUrl;
        this.maintainerLoginUrl = maintainerLoginUrl;
        this.user = null;
        this.masquerade = false;
    }

    public AuthInfo(UserInfo user, boolean masquerade) {
        this.studentLoginUrl = null;
        this.instructorLoginUrl = null;
        this.adminLoginUrl = null;
        this.maintainerLoginUrl = null;
        this.user = user;
        this.masquerade = masquerade;
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

}
