package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class AdminHomePageData extends PageData {
    
    public String instructorShortName;
    public String instructorName;
    public String instructorEmail;
    public String instructorInstitution;

    public AdminHomePageData(AccountAttributes account) {
        super(account);
    }

    public String getInstructorShortName() {
        return sanitizeForHtml(instructorShortName);
    }
    
    public String getInstructorName() {
        return sanitizeForHtml(instructorName);
    }
    
    public String getInstructorEmail() {
        return sanitizeForHtml(instructorEmail);
    }
    
    public String getInstructorInstitution() {
        return sanitizeForHtml(instructorInstitution);
    }
}
