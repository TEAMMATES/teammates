package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

public class AdminHomePageData extends PageData {

    public String instructorShortName;
    public String instructorName;
    public String instructorEmail;
    public String instructorInstitution;
    public boolean instructorAddingResultForAjax;
    public String statusForAjax;
    // this field will contain the name, email address and institution of the instructor separated by \t or |
    // e.g: "Instructor1 \t instructor1@test.com \t NUS"
    public String instructorDetailsSingleLine;

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

    public String getInstructorDetailsSingleLine() {
        return instructorDetailsSingleLine;
    }
}
