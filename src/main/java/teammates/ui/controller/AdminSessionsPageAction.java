package teammates.ui.controller;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
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

        
        String rawStart = getRequestParamValue("start");
        String rawEnd = getRequestParamValue("end");
        String rawZone = getRequestParamValue("timezone");

        Date start;
        Date end;
        double zone;

        if (rawStart == null && rawEnd == null && rawZone == null) {
            zone = 0.0;
            start = TimeHelper.now(zone).getTime();
            end = TimeHelper.now(zone).getTime();
        } else if (rawStart != null && rawEnd != null && rawZone != null 
                   && !rawStart.trim().isEmpty() && !rawEnd.trim().isEmpty() && !rawZone.trim().isEmpty()) {
                              
            zone = Double.parseDouble(rawZone);
            start = TimeHelper.convertToDate(converTorequiredFormat(rawStart));
            end = TimeHelper.convertToDate(converTorequiredFormat(rawEnd));  
          
        } else {
            
            isError = true;
            statusToUser.add("Error: Missing Parameters");
            statusToAdmin = "Admin Sessions Page Load<br>" +
                            "<span class=\"bold\"> Error: Missing Parameters</span>";

            data.map = map;
            data.totalOngoingSessions = 0;
            data.hasUnknown = false;

            return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
            
        }

        
        @SuppressWarnings("deprecation")
        // This method is deprecated to prevent unintended usage. This is an
        // intended usage.
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

    private String converTorequiredFormat(String rawDate) {

        String date = rawDate.substring(0, 10);
        String time = rawDate.substring(10);

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

        String formated = date + time + " UTC";

        return formated;

    }

}
