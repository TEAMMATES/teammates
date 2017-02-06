package teammates.ui.template;

import teammates.common.util.SanitizationHelper;

public class InstructorStudentListSearchBox {

    private String instructorSearchLink;
    private String searchKey;
    private String googleId;

    public InstructorStudentListSearchBox(String instructorSearchLink, String searchKey, String googleId) {
        this.instructorSearchLink = instructorSearchLink;
        this.searchKey = searchKey == null ? "" : SanitizationHelper.sanitizeForHtml(searchKey);
        this.googleId = googleId;
    }

    public String getInstructorSearchLink() {
        return instructorSearchLink;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public String getGoogleId() {
        return googleId;
    }

}
