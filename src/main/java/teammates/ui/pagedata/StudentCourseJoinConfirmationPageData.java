package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

public class StudentCourseJoinConfirmationPageData extends PageData {
    private String confirmUrl;
    private String logoutUrl;
    private boolean redirectResult;
    private boolean nextUrlAccessibleWithoutLogin;
    private String courseId;

    public StudentCourseJoinConfirmationPageData(AccountAttributes account, StudentAttributes student,
                                                 String confirmUrl, String logoutUrl, boolean redirectResult,
                                                 String courseId, boolean nextUrlAccessibleWithoutLogin) {
        super(account, student);
        this.confirmUrl = confirmUrl;
        this.logoutUrl = logoutUrl;
        this.redirectResult = redirectResult;
        this.courseId = courseId;
        this.nextUrlAccessibleWithoutLogin = nextUrlAccessibleWithoutLogin;
    }

    public String getConfirmUrl() {
        return confirmUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public boolean isRedirectResult() {
        return redirectResult;
    }

    public String getCourseId() {
        return courseId;
    }

    public boolean isNextUrlAccessibleWithoutLogin() {
        return nextUrlAccessibleWithoutLogin;
    }

}
