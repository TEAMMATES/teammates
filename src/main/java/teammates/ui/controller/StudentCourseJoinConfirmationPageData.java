package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;

public class StudentCourseJoinConfirmationPageData extends PageData {
    private String confirmUrl;
    private String logoutUrl;
    private boolean redirectResult;
    private boolean nextUrlAccessibleWithoutLogin;
    private String courseId;
    
    public StudentCourseJoinConfirmationPageData(final AccountAttributes account, final StudentAttributes student,
                                                 final String confirmUrl, final String logoutUrl, final boolean redirectResult,
                                                 final String courseId, final boolean nextUrlAccessibleWithoutLogin) {
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
