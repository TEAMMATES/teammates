package teammates.ui.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Const;
import teammates.common.util.LogHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.TimeHelper;
import teammates.common.util.Const.StatusMessageColor;
import teammates.common.util.Version;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;

import com.google.appengine.api.log.AppLogLine;

public class AdminActivityLogPageAction extends Action {
    
    private static final int RELEVANT_LOGS_PER_PAGE = 50;
    private static final int MAX_SEARCH_TIMES = 12; // search maximum 12 times with time increment
    
    private int totalLogsSearched;
    private boolean isFirstRow = true;
    private Long nextEndTimeToSearch;
    private LogHelper logHelper;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException{
        new GateKeeper().verifyAdminPrivileges(account);
        
        AdminActivityLogPageData data = new AdminActivityLogPageData(account);
        
        String searchTimeOffset = getRequestParamValue("searchTimeOffset");
        if (searchTimeOffset == null) {
            searchTimeOffset = "";
        }
        String filterQuery = getRequestParamValue("filterQuery");
        String courseIdFromSearchPage = getRequestParamValue("courseId");
        
        String logRoleFromAjax = getRequestParamValue("logRole");
        String logGoogleIdFromAjax = getRequestParamValue("logGoogleId");
        String logTimeInAdminTimeZoneFromAjax = getRequestParamValue("logTimeInAdminTimeZone");
        
        boolean isLoadingLocalTimeAjax = (logRoleFromAjax != null)
                                         && (logGoogleIdFromAjax != null)
                                         && (logTimeInAdminTimeZoneFromAjax != null);
        
        if (isLoadingLocalTimeAjax) {
            data.setLogLocalTime(getLocalTimeInfo(logGoogleIdFromAjax, 
                                                  logRoleFromAjax,
                                                  logTimeInAdminTimeZoneFromAjax));
            return createAjaxResult(data);
        }
        
//      This parameter determines whether the logs with requests contained in "excludedLogRequestURIs" in AdminActivityLogPageData
//      should be shown. Use "?all=true" in URL to show all logs. This will keep showing all
//      logs despite any action or change in the page unless the the page is reloaded with "?all=false" 
//      or simply reloaded with this parameter omitted.
        boolean ifShowAll = getRequestParamAsBoolean("all");
        
        
//      This determines whether the logs related to testing data should be shown. Use "testdata=true" in URL
//      to show all testing logs. This will keep showing all logs from testing data despite any action or change in the page
//      unless the the page is reloaded with "?testdata=false"  or simply reloaded with this parameter omitted.       
        boolean ifShowTestData = getRequestParamAsBoolean("testdata");
        
        if (filterQuery == null) {
            filterQuery = "";
        }
        //This is used to parse the filterQuery. If the query is not parsed, the filter function would ignore the query
        data.generateQueryParameters(filterQuery);
        
        logHelper = new LogHelper();
        
        List<ActivityLogEntry> logs = null;
        if (data.isFromDateSpecifiedInQuery()) {
            logs = searchLogsWithExactTimePeriod(data);
        } else {
            if (!searchTimeOffset.isEmpty()) {
                data.setToDate(Long.parseLong(searchTimeOffset));
            }
            logs = searchLogsWithTimeIncrement(data);
        }
        generateStatusMessage(data, logs, courseIdFromSearchPage);
        data.init(ifShowAll, ifShowTestData, logs);
        
        if (searchTimeOffset.isEmpty()) {
            return createShowPageResult(Const.ViewURIs.ADMIN_ACTIVITY_LOG, data);
        }
        
        return createAjaxResult(data);
    }
    
    private void generateStatusMessage(AdminActivityLogPageData data, List<ActivityLogEntry> logs, String courseId) {
        String status = "Total Logs gone through in last search: " + totalLogsSearched + "<br>";
        status += "Total Relevant Logs found in last search: " + logs.size() + "<br>";
        
        long earliestSearchTime = data.getFromDate();
        ActivityLogEntry earliestLogChecked = null;
        if (!logs.isEmpty()) {
            earliestLogChecked = logs.get(logs.size() - 1);
        }
        //  if the search space is limited to a certain log
        if ((logs.size() >= RELEVANT_LOGS_PER_PAGE) && (earliestLogChecked != null)) {
            earliestSearchTime = earliestLogChecked.getTime();
        }
        
        double targetTimeZone = Const.DOUBLE_UNINITIALIZED;
        if (data.isPersonSpecified()) {
            String targetUserGoogleId = data.getPersonSpecified();
            targetTimeZone = getLocalTimeZoneForRequest(targetUserGoogleId, "");

            if (targetTimeZone == Const.DOUBLE_UNINITIALIZED) {
                // if the user is unregistered, try finding the timezone by course id passed from Search page
                if ((courseId != null) && (!courseId.isEmpty())) {
                    targetTimeZone = getLocalTimeZoneForUnregisteredUserRequest(courseId);
                }
            }
        } else {
            targetTimeZone = Const.SystemParams.ADMIN_TIMZE_ZONE_DOUBLE;
        }
        
        double adminTimeZone = Const.SystemParams.ADMIN_TIMZE_ZONE_DOUBLE;
        String timeInAdminTimeZone = computeLocalTime(adminTimeZone, String.valueOf(earliestSearchTime));
        String timeInUserTimeZone =  computeLocalTime(targetTimeZone, String.valueOf(earliestSearchTime));
        status += "The earliest log entry checked on <b>" + timeInAdminTimeZone + "</b> in Admin Time Zone (" 
                  + adminTimeZone + ") and ";
        if (targetTimeZone != Const.DOUBLE_UNINITIALIZED) {
            status += "on <b>" + timeInUserTimeZone + "</b> in Local Time Zone (" + targetTimeZone + ").<br>";
        } else {
            status += timeInUserTimeZone + ".<br>";
        }
        
        status += "Logs are from following version(s): ";
        List<String> versionListToQuery = logHelper.getVersionsToQuery();
        for (int i = 0; i < versionListToQuery.size(); i++) {
            String version = versionListToQuery.get(i).replace('-', '.');
            if (i < versionListToQuery.size() - 1) {
                status += version + ", ";
            } else {
                status += version + "<br>";
            }
        }
        
        status += "All available version(s): ";
        List<Version> versionList = Version.getAvailableVersions();
        for (int i = 0; i < versionList.size(); i++) {
            String version = versionList.get(i).toString();
            if (i < versionList.size() - 1) {
                status += version + ", ";
            } else {
                status += version + "<br>";
            }
        }
        
        // the "Search More" button to continue searching from the previous fromDate 
        status += "<button class=\"btn-link\" id=\"button_older\" onclick=\"submitFormAjax(" + nextEndTimeToSearch + ");\">Search More</button>";
        
        status += "<input id=\"ifShowAll\" type=\"hidden\" value=\""+ data.getIfShowAll() +"\"/>";
        status += "<input id=\"ifShowTestData\" type=\"hidden\" value=\""+ data.getIfShowTestData() +"\"/>";
        
        data.setStatusForAjax(status);
        statusToUser.add(new StatusMessage(status, StatusMessageColor.INFO));
    }

    /**
     * Retrieves enough logs within 24 hour.
     */
    private List<ActivityLogEntry> searchLogsWithTimeIncrement(AdminActivityLogPageData data) {
        List<ActivityLogEntry> appLogs = new LinkedList<ActivityLogEntry>();
        logHelper.setQuery(data.getVersions(), data.getFromDate(), data.getToDate());
        
        totalLogsSearched = 0;
        for(int i = 0; i < MAX_SEARCH_TIMES; i++) {
            if (appLogs.size() >= RELEVANT_LOGS_PER_PAGE) {
                break;
            }
            List<AppLogLine> searchResult = logHelper.fetchLogsInNextHours();
            List<ActivityLogEntry> filteredLogs = filterLogsForActivityLogPage(searchResult, data);
            appLogs.addAll(filteredLogs);
            totalLogsSearched += searchResult.size();
        }
        nextEndTimeToSearch = logHelper.getEndTime();
        return appLogs;
    }
    
    /**
     * Retrieves all logs in the time period specified in the query.
     */
    private List<ActivityLogEntry> searchLogsWithExactTimePeriod(AdminActivityLogPageData data) {
        logHelper.setQuery(data.getVersions(), data.getFromDate(), data.getToDate());
        
        List<AppLogLine> searchResult = logHelper.fetchLogs();
        List<ActivityLogEntry> filteredLogs = filterLogsForActivityLogPage(searchResult, data);
        
        nextEndTimeToSearch = data.getFromDate() - 1;
        totalLogsSearched = searchResult.size();
        return filteredLogs;
    }
    
    /**
     * Filters logs that should be shown on Admin Activity Log Page.
     */
    private List<ActivityLogEntry> filterLogsForActivityLogPage(List<AppLogLine> appLogLines,
                                                             AdminActivityLogPageData data) {
        List<ActivityLogEntry> appLogs = new LinkedList<ActivityLogEntry>();
        for (AppLogLine appLog : appLogLines) {
            String logMsg = appLog.getLogMessage();
            boolean isNotTeammatesLog = (!logMsg.contains("TEAMMATESLOG"));
            boolean isLogFromAdminActivityLogPage = logMsg.contains("adminActivityLogPage");
            if (isNotTeammatesLog || isLogFromAdminActivityLogPage) {
                continue;
            }
            
            ActivityLogEntry activityLogEntry = new ActivityLogEntry(appLog);
            activityLogEntry = data.filterLogs(activityLogEntry);
            
            boolean isToShow = activityLogEntry.toShow() && ((!activityLogEntry.isTestingData()) || data.getIfShowTestData());
            if (!isToShow) {
                continue;
            }
            if (isFirstRow ) {
                activityLogEntry.setFirstRow();
                isFirstRow = false;
            }
            appLogs.add(activityLogEntry);
        }
        return appLogs;
    }

    private double getLocalTimeZoneForRequest(String userGoogleId, String userRole) {
        double localTimeZone = Const.DOUBLE_UNINITIALIZED;
        
        if ((userRole != null) && (userRole.contentEquals("Admin") || userRole.contains("(M)"))) {
            return Const.SystemParams.ADMIN_TIMZE_ZONE_DOUBLE;
        }
        
        Logic logic = new Logic();
        if (userGoogleId != null && !userGoogleId.isEmpty()) {     
            try {
                localTimeZone = findAvailableTimeZoneFromCourses(logic.getCoursesForInstructor(userGoogleId));
            } catch (EntityDoesNotExistException e) {
                localTimeZone = Const.DOUBLE_UNINITIALIZED;
            }
            
            if (localTimeZone != Const.DOUBLE_UNINITIALIZED) {
                return localTimeZone;
            }
             
            try {
                localTimeZone = findAvailableTimeZoneFromCourses(logic.getCoursesForStudentAccount(userGoogleId));
            } catch (EntityDoesNotExistException e) {
                localTimeZone = Const.DOUBLE_UNINITIALIZED;
            }
            
            if (localTimeZone != Const.DOUBLE_UNINITIALIZED) {
                return localTimeZone;
            }
        }
        
        return localTimeZone;
    }
    
    private double findAvailableTimeZoneFromCourses(List<CourseAttributes> courses) {
        double localTimeZone = Const.DOUBLE_UNINITIALIZED;
        
        if (courses == null) {
            return localTimeZone;
        }
        
        Logic logic = new Logic();
        
        for (CourseAttributes course : courses) {
            List<FeedbackSessionAttributes> fsl = logic.getFeedbackSessionsForCourse(course.id); 
            if (fsl != null && !fsl.isEmpty()) {
                return fsl.get(0).timeZone;
            }
        }
        
        return localTimeZone;
    }
    
    private double getLocalTimeZoneForUnregisteredUserRequest(String courseId) {
        double localTimeZone = Const.DOUBLE_UNINITIALIZED;
        
        if (courseId == null || courseId.isEmpty()) {
            return localTimeZone;
        }
        
        Logic logic = new Logic();
        
        List<FeedbackSessionAttributes> fsl = logic.getFeedbackSessionsForCourse(courseId); 
        if (fsl != null && !fsl.isEmpty()) {
            return fsl.get(0).timeZone;
        }
        
        return localTimeZone;
        
    }
    
    private double getLocalTimeZoneInfo(String logGoogleId, String logRole) {
        if (!logGoogleId.contentEquals("Unknown") && !logGoogleId.contentEquals("Unregistered")) {
            return getLocalTimeZoneForRequest(logGoogleId, logRole);
        } else if (logRole.contains("Unregistered") && !logRole.contentEquals("Unregistered")) {
            String coureseId = logRole.split(":")[1];
            return getLocalTimeZoneForUnregisteredUserRequest(coureseId);
        } else {
            return Const.DOUBLE_UNINITIALIZED;
        }
    }
    
    private String getLocalTimeInfo(String logGoogleId, String logRole, String logTimeInAdminTimeZone) {
        double timeZone = getLocalTimeZoneInfo(logGoogleId, logRole);
        if (timeZone != Const.DOUBLE_UNINITIALIZED) {
            return computeLocalTime(timeZone, logTimeInAdminTimeZone);
        } else {
            return "Local Time Unavailable";
        }
    }
    
    private String computeLocalTime(double timeZone, String logTimeInAdminTimeZone) {
        if (timeZone == Const.DOUBLE_UNINITIALIZED) {
            return "Local Time Unavailable";
        }
        
        Calendar appCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        appCal.setTimeInMillis(Long.parseLong(logTimeInAdminTimeZone));
        TimeHelper.convertToUserTimeZone(appCal, timeZone);
        return sdf.format(appCal.getTime());
    }
}
