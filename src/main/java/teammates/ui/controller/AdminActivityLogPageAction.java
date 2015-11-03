package teammates.ui.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.TimeHelper;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;
import com.google.appengine.api.log.LogService.LogLevel;

public class AdminActivityLogPageAction extends Action {
    
    //We want to pull out the application logs
    private boolean includeAppLogs = true;
    private static final int LOGS_PER_PAGE = 50;
    private static final int MAX_LOGSEARCH_LIMIT = 15000;
    private static final int ONE_DAY_IN_MILLIS = 24*60*60*1000;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException{
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        AdminActivityLogPageData data = new AdminActivityLogPageData(account);
        
        String startSearchTime = getRequestParamValue("startSearchTime");
        if (startSearchTime == null) {
            startSearchTime = "";
        }
        String offset = getRequestParamValue("offset");
        String pageChange = getRequestParamValue("pageChange");
        String filterQuery = getRequestParamValue("filterQuery");
        
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
        
        
        if (pageChange != null && pageChange.equals("true")) {
            //Reset the offset because we are performing a new search, so we start from the beginning of the logs
            offset = null;
        }
        if (filterQuery == null) {
            filterQuery = "";
        }
        //This is used to parse the filterQuery. If the query is not parsed, the filter function would ignore the query
        data.generateQueryParameters(filterQuery);
        if (!startSearchTime.isEmpty()) {
            data.setFromDate(Long.parseLong(startSearchTime));
        }
        
        LogQuery query = buildQuery(offset, data);
        
        List<ActivityLogEntry> logs = getAppLogs(query, data);
        
        data.init(offset, ifShowAll, ifShowTestData, logs);
        
        if (offset == null) {
            return createShowPageResult(Const.ViewURIs.ADMIN_ACTIVITY_LOG, data);
        }
        
        return createAjaxResult(data);
    }
    
    private LogQuery buildQuery(String offset, AdminActivityLogPageData data) {
        LogQuery query = LogQuery.Builder.withDefaults();
        List<String> versions = data.getVersions();
        
        query.includeAppLogs(includeAppLogs);
        query.batchSize(1000);
        query.minLogLevel(LogLevel.INFO);
        query.startTimeMillis(data.getFromDate());
        query.endTimeMillis(data.getToDate());
        
        try {
            query.majorVersionIds(getVersionIdsForQuery(versions));
        } catch (Exception e) {
            isError = true;
            statusToUser.add(new StatusMessage(e.getMessage(), StatusMessageColor.DANGER));
        }
        
        if (offset != null && !offset.equals("null")) {
            query.offset(offset);
        }
        return query;
    }
    
    private List<String> getVersionIdsForQuery(List<String> versions) {
        
        boolean isVersionSpecifiedInRequest = (versions != null && !versions.isEmpty());
        if (isVersionSpecifiedInRequest) {   
            return versions;        
        }       
        return getDefaultVersionIdsForQuery();
    }
    
    private List<String> getDefaultVersionIdsForQuery() {
    
        String currentVersion = Config.inst().getAppVersion();
        List<String> defaultVersions = new ArrayList<String>();
        
        //Check whether version Id contains alphabet 
        //Eg. 5.05rc
        if (currentVersion.matches(".*[A-z.*]")) {
            //if current version contains alphatet,
            //by default just prepare current version as a single element for the query
            defaultVersions.add(currentVersion.replace(".", "-"));
            
        } else {
            //current version does not contain alphabet
            //by default prepare current version with preceding 3 versions
            defaultVersions = getRecentVersionIdsWithDigitOnly(currentVersion);
        }
        
        return defaultVersions;        
    }
    
    private List<String> getRecentVersionIdsWithDigitOnly(String currentVersion) {
        
        List<String> recentVersions = new ArrayList<String>();
        
        double curVersionAsDouble = Double.parseDouble(currentVersion);
        recentVersions.add(currentVersion.replace(".", "-"));
        
        //go back for three preceding versions
        //subtract from double form of current version id
        //Eg. current version is 4.01 --> 4.00, 3.99, 3.98  --> 4-00, 3-99, 3-98
        for (int i = 1; i < 4; i++) {

            double preVersionAsDouble = curVersionAsDouble - 0.01 * i;
            if (preVersionAsDouble > 0) {
                String preVersion = String.format("%.2f", preVersionAsDouble)
                                          .replace(".", "-");
                
                recentVersions.add(preVersion);
            }
        }
        
        return recentVersions;
    }
    
    private List<ActivityLogEntry> getAppLogs(LogQuery query, AdminActivityLogPageData data) {
        List<ActivityLogEntry> appLogs = new LinkedList<ActivityLogEntry>();
        int totalLogsSearched = 0;
        int currentLogsInPage = 0;
        
        String lastOffset = null;
        
        //fetch request log
        Iterable<RequestLogs> records = LogServiceFactory.getLogService().fetch(query);
        boolean isFirstRow = true;
        ActivityLogEntry earliestLogChecked = null;
        boolean stillHasLog = false;
        for (RequestLogs record : records) {
            
            totalLogsSearched ++;
            lastOffset = record.getOffset();
            
            //End the search if we hit limits
            if (totalLogsSearched >= MAX_LOGSEARCH_LIMIT) {
                stillHasLog = true; 
                break;
            }
            if (currentLogsInPage >= LOGS_PER_PAGE) {
                stillHasLog = true;
                break;
            }

            //fetch application log
            List<AppLogLine> appLogLines = record.getAppLogLines();
            for (AppLogLine appLog : appLogLines) {
                if (currentLogsInPage >= LOGS_PER_PAGE) {
                    break;
                }
                String logMsg = appLog.getLogMessage();
                if (logMsg.contains("TEAMMATESLOG") && !logMsg.contains("adminActivityLogPage")) {
                    ActivityLogEntry activityLogEntry = new ActivityLogEntry(appLog);                   
                    earliestLogChecked = activityLogEntry;
                    activityLogEntry = data.filterLogs(activityLogEntry);
                    
                    if (activityLogEntry.toShow() && ((!activityLogEntry.isTestingData()) || data.getIfShowTestData())) {
                        appLogs.add(activityLogEntry);
                        if (isFirstRow) {
                            activityLogEntry.setFirstRow();
                            isFirstRow = false;
                        }
                        currentLogsInPage ++;
                    }
                }
            }    
        }
        
        String status="&nbsp;&nbsp;Total Logs gone through in last search: " + totalLogsSearched + "<br>";
        
        long earliestSearchTime = query.getStartTimeMillis();
        //  if the search space is limited to a certain log
        if (((totalLogsSearched >= MAX_LOGSEARCH_LIMIT) || (currentLogsInPage >= LOGS_PER_PAGE)) && (earliestLogChecked != null)) {
            earliestSearchTime = earliestLogChecked.getTime();
        }
        
        double targetTimeZone = Const.DOUBLE_UNINITIALIZED;
        if (data.isPersonSpecified()) {
            String targetUserGoogleId = data.getPersonSpecified();
            targetTimeZone = getLocalTimeZoneForRequest(targetUserGoogleId, "");
        }
        
        if (targetTimeZone == Const.DOUBLE_UNINITIALIZED) { // if no person is specified or the person doesn't really exist
            if (((totalLogsSearched >= MAX_LOGSEARCH_LIMIT) || (currentLogsInPage >= LOGS_PER_PAGE)) && (earliestLogChecked != null)) {
                String userGoogleId = earliestLogChecked.getGoogleId();
                String userRole = earliestLogChecked.getRole();
                targetTimeZone = this.getLocalTimeZoneInfo(userGoogleId, userRole);
            } else {
                targetTimeZone = Const.SystemParams.ADMIN_TIMZE_ZONE_DOUBLE;
            }
        }
        
        if (earliestLogChecked != null) {
            double adminTimeZone = Const.SystemParams.ADMIN_TIMZE_ZONE_DOUBLE;
            String timeInAdminTimeZone = computeLocalTime(adminTimeZone, String.valueOf(earliestSearchTime));
            String timeInUserTimeZone =  computeLocalTime(targetTimeZone, String.valueOf(earliestSearchTime));
            status += "The earliest log entry checked on <b>" + timeInAdminTimeZone + "</b> in Admin Time Zone (" 
                      + adminTimeZone + ") and ";
            if (targetTimeZone != Const.DOUBLE_UNINITIALIZED) {
                status += "on <b>" + timeInUserTimeZone + "</b> in Local Time Zone (" + targetTimeZone + ").<br>";
            } else {
                status += timeInUserTimeZone;
            }
        }
        
        //link for Next button, will fetch older logs
        if ((totalLogsSearched >= MAX_LOGSEARCH_LIMIT) || (!stillHasLog)) {
            // extends the search space one more day
            long oneDayBefore = data.getFromDate() - ONE_DAY_IN_MILLIS;
            status += "<br><span class=\"red\">&nbsp;&nbsp;Maximum amount of logs per request have been searched.</span><br>";
            status += "<button class=\"btn-link\" id=\"button_older\" onclick=\"submitFormAjax('" + lastOffset + "', "+ oneDayBefore +");\">Search More</button>";           
        }
        
        if (currentLogsInPage >= LOGS_PER_PAGE) {   
            status += "<button class=\"btn-link\" id=\"button_older\" onclick=\"submitFormAjax('" + lastOffset + "');\">Older Logs </button>";              
        }
        
        status += "<input id=\"ifShowAll\" type=\"hidden\" value=\""+ data.getIfShowAll() +"\"/>";
        status += "<input id=\"ifShowTestData\" type=\"hidden\" value=\""+ data.getIfShowTestData() +"\"/>";
        
        data.setStatusForAjax(status);
        statusToUser.add(new StatusMessage(status, 
                            totalLogsSearched >= MAX_LOGSEARCH_LIMIT ? StatusMessageColor.WARNING : StatusMessageColor.INFO));
        
        return appLogs;
    }
    
    /*
     * Functions used to load local time for activity log using AJAX
     */
    
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
