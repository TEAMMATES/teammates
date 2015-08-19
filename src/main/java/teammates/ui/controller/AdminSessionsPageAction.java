package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Const.StatusMessageColor;
import teammates.common.util.Sanitizer;
import teammates.common.util.StatusMessage;
import teammates.common.util.TimeHelper;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;

public class AdminSessionsPageAction extends Action {
    
    AdminSessionsPageData data;

    private Map<String, List<FeedbackSessionAttributes>> map;
    private Map<String, String> sessionToInstructorIdMap = new HashMap<String, String>();
    private int totalOngoingSessions;
    private Date rangeStart;
    private Date rangeEnd;
    private double zone;
    private boolean isShowAll = false;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        Logic logic = new Logic();
        
        new GateKeeper().verifyAdminPrivileges(account);
        data = new AdminSessionsPageData(account);       
        
        isShowAll = getRequestParamAsBoolean("all");
        
        ActionResult result = createShowPageResultIfParametersInvalid();
        if (result != null) {
            return result;
        }      
        
        List<FeedbackSessionAttributes> allOpenFeedbackSessionsList = 
                logic.getAllOpenFeedbackSessions(this.rangeStart, this.rangeEnd, this.zone);
        
        result = createShowPageResultIfNoOngoingSession(allOpenFeedbackSessionsList);
        if (result != null) {
            return result;
        }
        
        result = createAdminSessionPageResult(allOpenFeedbackSessionsList);
        
        return result;
        
    }

    private void putIntoUnknownList(
            HashMap<String, List<FeedbackSessionAttributes>> map, FeedbackSessionAttributes fs) {
        if (map.get("Unknown") == null) {
            List<FeedbackSessionAttributes> newList = new ArrayList<FeedbackSessionAttributes>();
            newList.add(fs);
            map.put("Unknown", newList);
        } else {
            map.get("Unknown").add(fs);
        }
    }


    
    private void prepareDefaultPageData(Calendar calStart, Calendar calEnd) {        
        this.map = new HashMap<String, List<FeedbackSessionAttributes>>();
        this.totalOngoingSessions = 0;
        this.rangeStart = calStart.getTime();
        this.rangeEnd = calEnd.getTime();
    }
    
    private ActionResult createShowPageResultIfParametersInvalid() {
        String startDate = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE);
        String endDate = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE);
        String startHour = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTHOUR);
        String endHour = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDHOUR);
        String startMin = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTMINUTE);
        String endMin = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDMINUTE);       
        String timeZone = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE);
        
        Date start;
        Date end;
        double zone = 0.0;
        
        Calendar calStart = TimeHelper.now(zone);
        Calendar calEnd = TimeHelper.now(zone);
        calStart.add(Calendar.DAY_OF_YEAR, -3);
        calEnd.add(Calendar.DAY_OF_YEAR, 4);

        if (checkAllParameters("null")) {              
            start = calStart.getTime();
            end = calEnd.getTime();
        } else if (checkAllParameters("notNull")) {
            
            Sanitizer.sanitizeForHtml(startDate);
            Sanitizer.sanitizeForHtml(endDate);
            Sanitizer.sanitizeForHtml(startHour);
            Sanitizer.sanitizeForHtml(endHour);
            Sanitizer.sanitizeForHtml(startMin);
            Sanitizer.sanitizeForHtml(endMin); 
            Sanitizer.sanitizeForHtml(timeZone); 
            
            zone = Double.parseDouble(timeZone);
            
            start = TimeHelper.convertToDate(TimeHelper.convertToRequiredFormat(startDate, startHour, startMin));
            end = TimeHelper.convertToDate(TimeHelper.convertToRequiredFormat(endDate, endHour, endMin));  
            
            
            if (start.after(end)) {
                isError = true;
                statusToUser.add(new StatusMessage("The filter range is not valid."
                                 + " End time should be after start time.", StatusMessageColor.DANGER));
                statusToAdmin = "Admin Sessions Page Load<br>" +
                                "<span class=\"bold\"> Error: invalid filter range</span>";
    
                prepareDefaultPageData(calStart, calEnd);
                data.init(this.map, this.sessionToInstructorIdMap, this.totalOngoingSessions,  
                     this.rangeStart, this.rangeEnd, this.zone, this.isShowAll);
                return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
            }
          
        } else {
            
            isError = true;
            statusToUser.add(new StatusMessage("Error: Missing Parameters", StatusMessageColor.DANGER));
            statusToAdmin = "Admin Sessions Page Load<br>" +
                            "<span class=\"bold\"> Error: Missing Parameters</span>";

            prepareDefaultPageData(calStart, calEnd);
            data.init(this.map, this.sessionToInstructorIdMap, this.totalOngoingSessions,  
                      this.rangeStart, this.rangeEnd, this.zone, this.isShowAll);
            return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
            
        }
        
        
        this.rangeStart = start;
        this.rangeEnd = end;
        this.zone = zone;
        
        return null;
    }
    
    private ActionResult createShowPageResultIfNoOngoingSession(List<FeedbackSessionAttributes> allOpenFeedbackSessionsList) {
        if (allOpenFeedbackSessionsList.isEmpty()) {

            isError = false;
            statusToUser.add(new StatusMessage("Currently No Ongoing Sessions", StatusMessageColor.WARNING));
            statusToAdmin = "Admin Sessions Page Load<br>" +
                            "<span class=\"bold\"> No Ongoing Sessions</span>";

            this.map = new HashMap<String, List<FeedbackSessionAttributes>>();;
            this.totalOngoingSessions = 0;
            data.init(this.map, this.sessionToInstructorIdMap, this.totalOngoingSessions,  
                      this.rangeStart, this.rangeEnd, this.zone, this.isShowAll);
            return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
        }
        
        return null;
        
    }
    
    private ActionResult createAdminSessionPageResult(List<FeedbackSessionAttributes> allOpenFeedbackSessionsList) {
        HashMap<String, List<FeedbackSessionAttributes>> map = new HashMap<String, List<FeedbackSessionAttributes>>();
        this.totalOngoingSessions = allOpenFeedbackSessionsList.size();

        for (FeedbackSessionAttributes fs : allOpenFeedbackSessionsList) {

            List<InstructorAttributes> instructors = logic.getInstructorsForCourse(fs.courseId);

            if (!instructors.isEmpty()) {
                
                AccountAttributes account = getRegisteredInstructorAccountFromInstructors(instructors);

                if (account == null) {
                    putIntoUnknownList(map, fs);
                    continue;
                }

                if (map.get(account.institute) == null) {
                    List<FeedbackSessionAttributes> newList = new ArrayList<FeedbackSessionAttributes>();
                    newList.add(fs);
                    map.put(account.institute, newList);
                } else {
                    map.get(account.institute).add(fs);
                }

            } else {
                putIntoUnknownList(map, fs);
            }
        }
        this.map = map;
        statusToAdmin = "Admin Sessions Page Load<br>" +
                        "<span class=\"bold\">Total Ongoing Sessions:</span> " +
                        this.totalOngoingSessions;
        
        constructSessionToInstructorIdMap();
        data.init(this.map, this.sessionToInstructorIdMap, this.totalOngoingSessions, 
                  this.rangeStart, this.rangeEnd, this.zone, this.isShowAll);
        return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
    }
    
    private void constructSessionToInstructorIdMap() {
        for (String institute : this.map.keySet()) {
            for (FeedbackSessionAttributes fs : this.map.get(institute)) {
                String googleId = findAvailableInstructorGoogleIdForCourse(fs.courseId);
                this.sessionToInstructorIdMap.put(fs.getIdentificationString(), googleId);
            }
        }
    }
    
    
    /**
     * This method loops through all instructors for the given course until a registered Instructor is found.
     * It returns the google id of the found instructor.
     * @param CourseId
     * @return empty string if no available instructor google id is found
     */
    private String findAvailableInstructorGoogleIdForCourse(String courseId) {
        
        String googleId = "";
        
        if (logic.getInstructorsForCourse(courseId) == null) {
            return googleId;
        }
        
        for(InstructorAttributes instructor : logic.getInstructorsForCourse(courseId)) {
          
            if (instructor.googleId != null) {
                googleId = instructor.googleId;
                break;
            }            
        }
        
        return googleId; 
    }
    
    
    private AccountAttributes getRegisteredInstructorAccountFromInstructors(List<InstructorAttributes> instructors) {
        
        for (InstructorAttributes instructor : instructors) {
            if (instructor.googleId != null) {
                return logic.getAccount(instructor.googleId);
            }
        }
        
        return null;
    }
    
    
    private boolean checkAllParameters(String condition) {
        
        String startDate = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE);
        String endDate = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE);
        String startHour = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTHOUR);
        String endHour = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDHOUR);
        String startMin = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTMINUTE);
        String endMin = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDMINUTE);       
        String timeZone = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE);
        
        if (condition.contentEquals("null")) {

            return (startDate == null && endDate == null && startHour == null &&
                    endHour == null && startMin == null && endMin == null && timeZone == null);

        } else if (condition.contentEquals("notNull")) {

            return (startDate != null && endDate != null && startHour != null
                    && endHour != null && startMin != null && endMin != null && timeZone != null
                    && !startDate.trim().isEmpty() && !endDate.trim().isEmpty() && !startHour.trim().isEmpty()
                    && !endHour.trim().isEmpty() && !startMin.trim().isEmpty()
                    && !endMin.trim().isEmpty() && !timeZone.trim().isEmpty());

        } else {
            return false;
        }
        
    }
    
    

}
