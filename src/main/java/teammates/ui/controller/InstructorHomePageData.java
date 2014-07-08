package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;

public class InstructorHomePageData extends PageData {
    
    public InstructorHomePageData(AccountAttributes account) {
        super(account);
    }
    
    public HashMap<String, InstructorAttributes> instructors;
    public List<CourseSummaryBundle> courses;
    public String sortCriteria;
    public HashMap<String, Integer> numberOfPendingComments;
    public static final int MAX_CLOSED_SESSION_STATS = 3;
    
    public String getInstructorEvaluationLinkForCourse(String courseID) {
        String link = super.getInstructorEvaluationLink();
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID);
        return link;
    }

}
