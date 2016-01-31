package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;

public class CourseStatsPageData extends PageData {
    public CourseDetailsBundle courseDetails;
    
    public CourseStatsPageData(AccountAttributes account) {
        super(account);
    }
}
