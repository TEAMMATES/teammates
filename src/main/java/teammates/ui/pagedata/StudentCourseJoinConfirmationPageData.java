package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

public class StudentCourseJoinConfirmationPageData extends PageData {
    private String confirmUrl;
    private String logoutUrl;
    private boolean isRedirectResult;
    private boolean isNextUrlAccessibleWithoutLogin;
    private String courseId;

    public StudentCourseJoinConfirmationPageData(AccountAttributes account, StudentAttributes student, String sessionToken,
                                                 String confirmUrl, String logoutUrl, boolean redirectResult,
                                                 String courseId, boolean nextUrlAccessibleWithoutLogin) {
        super(account, student, sessionToken);
        this.confirmUrl = confirmUrl;
        this.logoutUrl = logoutUrl;
        this.isRedirectResult = redirectResult;
        this.courseId = courseId;
        this.isNextUrlAccessibleWithoutLogin = nextUrlAccessibleWithoutLogin;
    }

    public String getConfirmUrl() {
        return confirmUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public boolean isRedirectResult() {
        return isRedirectResult;
    }

    public String getCourseId() {
        return courseId;
    }

    public boolean isNextUrlAccessibleWithoutLogin() {
        return isNextUrlAccessibleWithoutLogin;
    }

}
