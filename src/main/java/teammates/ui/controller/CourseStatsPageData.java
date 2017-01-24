package teammates.ui.controller;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseDetailsBundle;

public class CourseStatsPageData extends PageData {
    public CourseDetailsBundle courseDetails;
    
    public CourseStatsPageData(AccountAttributes account) {
        super(account);
    }
}
