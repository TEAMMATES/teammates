package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;

public class CourseStatsPageData extends PageData {
    public CourseDetailsBundle courseDetails;

    public CourseStatsPageData(AccountAttributes account) {
        super(account);
    }
}
