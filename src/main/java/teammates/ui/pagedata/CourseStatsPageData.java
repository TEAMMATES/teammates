package teammates.ui.pagedata;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;

public class CourseStatsPageData extends PageData {
    public CourseDetailsBundle courseDetails;

    public CourseStatsPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }
}
