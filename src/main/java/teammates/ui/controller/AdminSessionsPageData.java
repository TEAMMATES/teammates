package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.logic.api.Logic;

public class AdminSessionsPageData extends PageData {

    public HashMap<String, List<FeedbackSessionAttributes>> map;
    public HashMap<String, String> sessionToInstructorIdMap = new HashMap<String, String>();
    public int totalOngoingSessions;
    public boolean hasUnknown;
    public Date rangeStart;
    public Date rangeEnd;
    public double zone;
    public int tableCount;
    public boolean isShowAll = false;

    public AdminSessionsPageData(AccountAttributes account) {
        super(account);

    }

    public String getInstructorHomePageViewLink(String email) {

        Logic logic = new Logic();
        List<InstructorAttributes> instructors = logic
                .getInstructorsForEmail(email);

        String link = "";

        if (instructors != null && !instructors.isEmpty()) {
            String googleId = logic.getInstructorsForEmail(email).get(0).googleId;
            link = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
            link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, googleId);
            link = "href=\"" + link + "\"";
        } else {
            return "";
        }
        return link;
    }

    public ArrayList<String> getHourOptionsAsHtml(Date date) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i <= 23; i++) {
            result.add("<option value=\"" + i + "\"" + " "
                       + (date.getHours() == i ? "selected=\"selected\"" : "")
                       + ">" + String.format("%02dH", i) + "</option>");
        }
        return result;
    }

    public ArrayList<String> getMinuteOptionsAsHtml(Date date) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i <= 59; i++) {
            result.add("<option value=\"" + i + "\"" + " "
                       + (date.getMinutes() == i ? "selected=\"selected\"" : "")
                       + ">" + String.format("%02d", i) + "</option>");
        }
        return result;
    }

    public ArrayList<String> getTimeZoneOptionsAsHtml() {
        return getTimeZoneOptionsAsHtml(zone);
    }

    public String getTimeZoneAsString(){
        return StringHelper.toUtcFormat(zone);
    }
    
    public String getFeedbackSessionStatsLink(String courseID, String feedbackSessionName, String user){
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName); 
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, user);
        return link;
    }
    
    public String getSessionStatusForShow(FeedbackSessionAttributes fs){
        
        String status = "";
        if(fs.isClosed()){
            status += "[Closed]";   
        }
          if(fs.isOpened()){
            status += "[Opened]";    
        } 
          if(fs.isWaitingToOpen()){
            status +=  "[Waiting To Open]";   
        } 
          if(fs.isPublished()){
            status +=  "[Published]";   
        }
          if(fs.isInGracePeriod()){
            status +=  "[Grace Period]";   
        }
          
        status = status.isEmpty()? "No Status": status;
        
        return status;
        
    }

}
