package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

public class AdminHomePageData extends PageData {

    public String instructorName;
    public String instructorEmail;
    public String instructorInstitution;
    public boolean isInstructorAddingResultForAjax;
    public String statusForAjax;

    public AdminHomePageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
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
