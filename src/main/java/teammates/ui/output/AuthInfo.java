package teammates.ui.output;

import javax.annotation.Nullable;

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
    private final UserInfo user;
    @Nullable
    private final String institute;
    private final boolean masquerade;

    public AuthInfo(String studentLoginUrl, String instructorLoginUrl, String adminLoginUrl) {
        this.studentLoginUrl = studentLoginUrl;
        this.instructorLoginUrl = instructorLoginUrl;
        this.adminLoginUrl = adminLoginUrl;
        this.user = null;
        this.institute = null;
        this.masquerade = false;
    }

    public AuthInfo(UserInfo user, String institute, boolean masquerade) {
        this.studentLoginUrl = null;
        this.instructorLoginUrl = null;
        this.adminLoginUrl = null;
        this.user = user;
        this.institute = institute;
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

    public UserInfo getUser() {
        return user;
    }

    public String getInstitute() {
        return institute;
    }

    public boolean isMasquerade() {
        return masquerade;
    }

}
