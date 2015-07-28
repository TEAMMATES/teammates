package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.logic.api.Logic;
import teammates.ui.template.AdminFeedbackSessionRow;
import teammates.ui.template.AdminFilter;
import teammates.ui.template.InstitutionPanel;

public class AdminSessionsPageData extends PageData {
    private static final String UNKNOWN_INSTITUTION = "Unknown";
    private int totalOngoingSessions;
    private Date rangeStart;
    private Date rangeEnd;
    private double zone;
    private boolean isShowAll = false;
    private List<InstitutionPanel> institutionPanels;
    private AdminFilter filter;
    
    public AdminSessionsPageData(AccountAttributes account) {
        super(account);

    }
    
    public void init(
            Map<String, List<FeedbackSessionAttributes>> map, Map<String, String> sessionToInstructorIdMap, 
            int totalOngoingSessions, Date rangeStart, Date rangeEnd, double zone, boolean isShowAll) {

        this.totalOngoingSessions = totalOngoingSessions;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.zone = zone;
        this.isShowAll = isShowAll;
        setFilter();
        setInstitutionPanels(map, sessionToInstructorIdMap);
    }

    public int getTotalOngoingSessions() {
        return totalOngoingSessions;
    }

    public int getTableCount() {
        return institutionPanels.size();
    }
    
    public boolean isShowAll() {
        return isShowAll;
    }
    
    public AdminFilter getFilter() {
        return filter;
    }
    
    public String getRangeStartString() {
        return TimeHelper.formatTime(rangeStart);
    }
    
    public String getRangeEndString() {
        return TimeHelper.formatTime(rangeEnd);
    }
    
    public List<InstitutionPanel> getInstitutionPanels() {
        return institutionPanels;
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

    @SuppressWarnings("deprecation")
    public ArrayList<String> getHourOptionsAsHtml(Date date) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i <= 23; i++) {
            result.add("<option value=\"" + i + "\"" + " "
                       + (date.getHours() == i ? "selected=\"selected\"" : "")
                       + ">" + String.format("%02dH", i) + "</option>");
        }
        return result;
    }

    @SuppressWarnings("deprecation")
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

    public String getTimeZoneAsString() {
        return StringHelper.toUtcFormat(zone);
    }
    
    public String getFeedbackSessionStatsLink(String courseID, String feedbackSessionName, String user) {
        String link;
        if (user.isEmpty()) {
            link = "";
        } else {
            link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE;
            link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID);
            link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName); 
            link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, user);
        }
        return link;
    }
    
    public String getSessionStatusForShow(FeedbackSessionAttributes fs) {
        
        String status = "";
        if (fs.isClosed()) {
            status += "[Closed]";   
        }
          if (fs.isOpened()) {
            status += "[Opened]";    
        } 
          if (fs.isWaitingToOpen()) {
            status +=  "[Waiting To Open]";   
        } 
          if (fs.isPublished()) {
            status +=  "[Published]";   
        }
          if (fs.isInGracePeriod()) {
            status +=  "[Grace Period]";   
        }
          
        status = status.isEmpty()? "No Status": status;
        
        return status;
        
    }
    
    public List<AdminFeedbackSessionRow> getFeedbackSessionRows(
            List<FeedbackSessionAttributes> feedbackSessions, Map<String, String> sessionToInstructorIdMap) {
        List<AdminFeedbackSessionRow> feedbackSessionRows = new ArrayList<AdminFeedbackSessionRow>();
        for (FeedbackSessionAttributes feedbackSession : feedbackSessions) {
            String googleId = sessionToInstructorIdMap.get(feedbackSession.getIdentificationString());
            feedbackSessionRows.add(new AdminFeedbackSessionRow(
                                            getSessionStatusForShow(feedbackSession), 
                                            getFeedbackSessionStatsLink(
                                                    feedbackSession.courseId, 
                                                    feedbackSession.feedbackSessionName, 
                                                    googleId),
                                            TimeHelper.formatTime(feedbackSession.getSessionStartTime()),
                                            TimeHelper.formatTime(feedbackSession.getSessionEndTime()),
                                            getInstructorHomePageViewLink(feedbackSession.creatorEmail),
                                            feedbackSession.creatorEmail,
                                            feedbackSession.courseId,
                                            feedbackSession.feedbackSessionName));
        }
        return feedbackSessionRows;
    }
    
    
    private void setFilter() {
        filter = new AdminFilter(TimeHelper.formatDate(rangeStart), getHourOptionsAsHtml(rangeStart), 
                                 getMinuteOptionsAsHtml(rangeStart), TimeHelper.formatDate(rangeEnd), 
                                 getHourOptionsAsHtml(rangeEnd), getMinuteOptionsAsHtml(rangeEnd),
                                 getTimeZoneOptionsAsHtml());
    }
    
    public void setInstitutionPanels(
            Map<String, List<FeedbackSessionAttributes>> map, Map<String, String> sessionToInstructorIdMap) {
        institutionPanels = new ArrayList<InstitutionPanel>();
        for (String key : map.keySet()) {
            if (!key.equals(UNKNOWN_INSTITUTION)) {
                institutionPanels.add(new InstitutionPanel(
                                              key, getFeedbackSessionRows(
                                                           map.get(key), 
                                                           sessionToInstructorIdMap)));
            }
        }
        String key = UNKNOWN_INSTITUTION;
        List<FeedbackSessionAttributes> feedbackSessions = map.get(key);
        if (feedbackSessions != null) {
            institutionPanels.add(new InstitutionPanel(
                                          key, getFeedbackSessionRows(
                                                       feedbackSessions, 
                                                       sessionToInstructorIdMap)));
        }
    }
}
