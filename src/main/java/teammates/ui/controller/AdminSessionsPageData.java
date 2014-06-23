package teammates.ui.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.logic.api.Logic;

public class AdminSessionsPageData extends PageData {
    
    public HashMap<String,List<FeedbackSessionAttributes>> map;
    public int totalOngoingSessions;
    public boolean hasUnknown;
    public Date rangeStart;
    public Date rangeEnd;
    public double zone;
    
    public AdminSessionsPageData(AccountAttributes account) {
        super(account);
       
    }
    
    public String getInstructorHomePageViewLink(String email){
        
        Logic logic = new Logic();
        
        String googleId = logic.getInstructorsForEmail(email).get(0).googleId;
        String link = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, googleId);
        return link;
    }
    
}
