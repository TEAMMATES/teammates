package teammates.ui.controller;


import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.StudentSearchResultBundle;

public class AdminSearchPageData extends PageData {
    
    public String searchKey = "";
    
    /*
     * Data related to searched students
     */
    public StudentSearchResultBundle studentResultBundle = new StudentSearchResultBundle();
    public HashMap<String, List<String>> studentOpenFeedbackSessionLinksMap = new HashMap<String, List<String>>();
    public HashMap<String, List<String>> studentUnOpenedFeedbackSessionLinksMap = new HashMap<String, List<String>>();
    public HashMap<String, String> feedbackSeesionLinkToNameMap = new HashMap<String, String>();
    public HashMap<String, String> studentIdToHomePageLinkMap = new HashMap<String, String>();
    public HashMap<String, String> studentDetailsPageLinkMap = new HashMap<String, String>();
    public HashMap<String, String> studentInstituteMap = new HashMap<String, String>();
    
    /*
     * Data related to searched instructors
     */   
    public InstructorSearchResultBundle instructorResultBundle = new InstructorSearchResultBundle();
    public HashMap<String, String> instructorInstituteMap = new HashMap<String, String>();
    public HashMap<String, String> instructorHomaPageLinkMap = new HashMap<String, String>();
    public HashMap<String, String> instructorCourseJoinLinkMap = new HashMap<String, String>();
    
    public AdminSearchPageData(AccountAttributes account) {
        super(account);
    }    
}
