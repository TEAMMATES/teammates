package teammates.ui.controller;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;

public class CourseStatsPageData extends PageData {
    public CourseDetailsBundle courseDetails;
    
    public CourseStatsPageData(AccountAttributes account) {
        super(account);
    }
}
