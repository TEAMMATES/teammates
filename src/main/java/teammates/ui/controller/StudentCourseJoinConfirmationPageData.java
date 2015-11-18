package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;

public class StudentCourseJoinConfirmationPageData extends PageData {
    private String confirmUrl;
    private String logoutUrl;
    private boolean redirectResult;
    private String courseId;
    
    public StudentCourseJoinConfirmationPageData(AccountAttributes account, StudentAttributes student,
                                                 String confirmUrl, String logoutUrl, boolean redirectResult,
                                                 String courseId) {
        super(account, student);
        this.confirmUrl = confirmUrl;
        this.logoutUrl = logoutUrl;
        this.redirectResult = redirectResult;
        this.courseId = courseId;
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
}
