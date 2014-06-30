package teammates.ui.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.TimeHelper;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;

public class AdminSessionsPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        Logic logic = new Logic();

        new GateKeeper().verifyAdminPrivileges(account);
        AdminSessionsPageData data = new AdminSessionsPageData(account);       
        HashMap<String, List<FeedbackSessionAttributes>> map = new HashMap<String, List<FeedbackSessionAttributes>>();

        
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

        if (startDate == null && endDate == null && startHour == null
            && endHour == null && startMin == null && endMin == null && timeZone == null) {
               
            start = calStart.getTime();
            end = calEnd.getTime();
        } else if (startDate != null && endDate != null && startHour != null
                   && endHour != null && startMin != null && endMin != null && timeZone != null
                   && !startDate.trim().isEmpty() && !endDate.trim().isEmpty() && !startHour.trim().isEmpty()
                   && !endHour.trim().isEmpty() && !startMin.trim().isEmpty() && !endMin.trim().isEmpty() && !timeZone.trim().isEmpty()) {
            
            Sanitizer.sanitizeForHtml(startDate);
            Sanitizer.sanitizeForHtml(endDate);
            Sanitizer.sanitizeForHtml(startHour);
            Sanitizer.sanitizeForHtml(endHour);
            Sanitizer.sanitizeForHtml(startMin);
            Sanitizer.sanitizeForHtml(endMin); 
            Sanitizer.sanitizeForHtml(timeZone); 
            
            zone = Double.parseDouble(timeZone);
            
            start = TimeHelper.convertToDate(converTorequiredFormat(startDate, startHour, startMin));
            end = TimeHelper.convertToDate(converTorequiredFormat(endDate, endHour, endMin));  
            
            
            if(start.after(end)){
                isError = true;
                statusToUser.add("The filter range is not valid."
                                 + "End time should be after start time.");
                statusToAdmin = "Admin Sessions Page Load<br>" +
                                "<span class=\"bold\"> Error: invalid filter range</span>";
    
                prepareDefaultPageData(data, calStart, calEnd);
    
                return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
            }
          
        } else {
            
            isError = true;
            statusToUser.add("Error: Missing Parameters");
            statusToAdmin = "Admin Sessions Page Load<br>" +
                            "<span class=\"bold\"> Error: Missing Parameters</span>";

            prepareDefaultPageData(data, calStart, calEnd);

            return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
            
        }
        
        
        data.rangeStart = start;
        data.rangeEnd = end;
        data.zone = zone;
        
        
        List<FeedbackSessionAttributes> allOpenFeedbackSessionsList = logic.getAllOpenFeedbackSessions(start,end,zone);
               

        if (allOpenFeedbackSessionsList.isEmpty()) {

            isError = false;
            statusToUser.add("Currently No Ongoing Sessions");
            statusToAdmin = "Admin Sessions Page Load<br>" +
                            "<span class=\"bold\"> No Ongoing Sessions</span>";

            data.map = map;
            data.totalOngoingSessions = 0;
            data.hasUnknown = false;

            return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
        }

        data.totalOngoingSessions = allOpenFeedbackSessionsList.size();
        data.hasUnknown = false;

        for (FeedbackSessionAttributes fs : allOpenFeedbackSessionsList) {

            List<InstructorAttributes> instructors = logic.getInstructorsForCourse(fs.courseId);

            if (!instructors.isEmpty()) {

                InstructorAttributes instructor = instructors.get(0);

                AccountAttributes account = logic.getAccount(instructor.googleId);

                if (account == null) {
                    putIntoUnknownList(map, fs);
                    data.hasUnknown = true;
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
                data.hasUnknown = true;
            }
        }

        data.tableCount = map.keySet().size();
        data.map = map;
        statusToAdmin = "Admin Sessions Page Load<br>" +
                "<span class=\"bold\">Total Ongoing Sessions:</span> " +
                data.totalOngoingSessions;

        return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
    }

    private void putIntoUnknownList(HashMap<String, List<FeedbackSessionAttributes>> map,
                                    FeedbackSessionAttributes fs) {

        if (map.get("Unknown") == null) {
            List<FeedbackSessionAttributes> newList = new ArrayList<FeedbackSessionAttributes>();
            newList.add(fs);
            map.put("Unknown", newList);
        } else {
            map.get("Unknown").add(fs);
        }
    }

    private String converTorequiredFormat(String date, String hour, String min) {

        final String OLD_FORMAT = "dd/MM/yyyy";
        final String NEW_FORMAT = "yyyy-MM-dd";

        String oldDateString = date;
        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d;
        try {
            d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            date = sdf.format(d);
        } catch (ParseException e) {
            Assumption.fail("Date in String is in wrong format.");
            return null;
        }
        
        int intHour = Integer.parseInt(hour);
        
        String amOrPm = intHour >= 12 ? "PM" : "AM";
        intHour = intHour >= 13 ? intHour - 12 : intHour;
        
        String formated = date + " "+ intHour + ":" + min + " " + amOrPm + " UTC";

        return formated;

    }
    
    private void prepareDefaultPageData(AdminSessionsPageData data, Calendar calStart, Calendar calEnd){        
        data.map = new HashMap<String, List<FeedbackSessionAttributes>>();
        data.totalOngoingSessions = 0;
        data.hasUnknown = false;
        data.rangeStart = calStart.getTime();
        data.rangeEnd = calEnd.getTime();
    }

}
