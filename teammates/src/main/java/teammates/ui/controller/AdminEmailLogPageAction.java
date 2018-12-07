package teammates.ui.controller;

import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.log.AppLogLine;

import teammates.common.util.AdminLogQuery;
import teammates.common.util.Const;
import teammates.common.util.EmailLogEntry;
import teammates.common.util.GaeLogApi;
import teammates.common.util.GaeVersionApi;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.AdminEmailLogPageData;

public class AdminEmailLogPageAction extends Action {
    private static final int LOGS_PER_PAGE = 50;
    /**
     * The maximum time period to retrieve logs with time increment.
     */
    private static final int MAX_SEARCH_PERIOD = 24 * 60 * 60 * 1000; // 24 hrs in milliseconds
    private static final int SEARCH_TIME_INCREMENT = 2 * 60 * 60 * 1000; // two hours in milliseconds
    /**
     * The maximum number of times to retrieve logs with time increment.
     */
    private static final int MAX_SEARCH_TIMES = MAX_SEARCH_PERIOD / SEARCH_TIME_INCREMENT;
    /**
     * Maximum number of versions to query.
     */
    private static final int MAX_VERSIONS_TO_QUERY = 1 + 5; //the current version and its 5 preceding versions

    @Override
    protected ActionResult execute() {
        gateKeeper.verifyAdminPrivileges(account);

        AdminEmailLogPageData data = new AdminEmailLogPageData(account, sessionToken, getRequestParamValue("filterQuery"),
                                                               getRequestParamAsBoolean("all"));

        if (data.getFilterQuery() == null) {
            data.setFilterQuery("");
        }

        //This is used to parse the filterQuery. If the query is not parsed, the filter function would ignore the query
        data.generateQueryParameters(data.getFilterQuery());

        String timeOffset = getRequestParamValue("offset");
        if (timeOffset != null && !timeOffset.isEmpty()) {
            data.setToDate(Long.parseLong(timeOffset));
        }

        if (data.isFromDateInQuery()) {
            searchEmailLogsWithExactTimePeriod(data);
        } else {
            searchEmailLogsWithTimeIncrement(data);
        }

        statusToAdmin = "adminEmailLogPage Page Load";

        if (timeOffset == null) {
            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL_LOG, data);
        }

        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL_LOG_AJAX, data);
    }

    /**
     * Selects versions for query. If versions are not specified, it will return
     * MAX_VERSIONS_TO_QUERY most recent versions used for query.
     */
    private List<String> getVersionsForQuery(List<String> versions) {
        boolean isVersionSpecifiedInRequest = versions != null && !versions.isEmpty();
        if (isVersionSpecifiedInRequest) {
            return versions;
        }
        GaeVersionApi versionApi = new GaeVersionApi();
        return versionApi.getMostRecentVersions(MAX_VERSIONS_TO_QUERY);
    }

    /**
     * Searches all logs in the time period specified in the query.
     */
    private void searchEmailLogsWithExactTimePeriod(AdminEmailLogPageData data) {
        List<String> versionToQuery = getVersionsForQuery(data.getVersions());
        AdminLogQuery query = new AdminLogQuery(versionToQuery, data.getFromDate(), data.getToDate());

        List<AppLogLine> searchResult = new GaeLogApi().fetchLogs(query);
        data.setLogs(filterLogsForEmailLogPage(searchResult, data));

        long nextEndTimeToSearch = data.getFromDate() - 1;
        int totalLogsSearched = searchResult.size();

        String status = "&nbsp;&nbsp;Total Logs gone through in last search: "
                + totalLogsSearched + "<br>"
                + "<button class=\"btn-link\" id=\"button_older\" data-next-end-time-to-search=\""
                + nextEndTimeToSearch + "\">Search More</button>";
        data.setStatusForAjax(status);
        statusToUser.add(new StatusMessage(status, StatusMessageColor.INFO));
    }

    /**
     * Searches enough email logs within MAX_SEARCH_PERIOD hours.
     */
    private void searchEmailLogsWithTimeIncrement(AdminEmailLogPageData data) {
        List<EmailLogEntry> emailLogs = new LinkedList<>();
        List<String> versionToQuery = getVersionsForQuery(data.getVersions());
        AdminLogQuery query = new AdminLogQuery(versionToQuery, null, data.getToDate());

        int totalLogsSearched = 0;

        GaeLogApi logApi = new GaeLogApi();

        long startTime = query.getEndTime() - SEARCH_TIME_INCREMENT;
        query.setTimePeriod(startTime, query.getEndTime());

        for (int i = 0; i < MAX_SEARCH_TIMES; i++) {
            if (emailLogs.size() >= LOGS_PER_PAGE) {
                break;
            }
            List<AppLogLine> searchResult = logApi.fetchLogs(query);
            List<EmailLogEntry> filteredLogs = filterLogsForEmailLogPage(searchResult, data);
            emailLogs.addAll(filteredLogs);
            totalLogsSearched += searchResult.size();
            query.moveTimePeriodBackward(SEARCH_TIME_INCREMENT);
        }

        data.setLogs(emailLogs);

        long nextEndTimeToSearch = query.getEndTime();
        String status = "&nbsp;&nbsp;Total Logs gone through in last search: "
                      + totalLogsSearched + "<br>"
                      + "<button class=\"btn-link\" id=\"button_older\" data-next-end-time-to-search=\""
                      + nextEndTimeToSearch + "\">Search More</button>";
        data.setStatusForAjax(status);
        statusToUser.add(new StatusMessage(status, StatusMessageColor.INFO));
    }

    private List<EmailLogEntry> filterLogsForEmailLogPage(List<AppLogLine> appLogLines,
                                                          AdminEmailLogPageData data) {
        List<EmailLogEntry> emailLogs = new LinkedList<>();

        for (AppLogLine appLog : appLogLines) {
            String logMsg = appLog.getLogMessage();
            boolean isNotEmailLog = !logMsg.contains("TEAMMATESEMAILLOG");
            if (isNotEmailLog) {
                continue;
            }

            EmailLogEntry emailLogEntry = new EmailLogEntry(appLog);
            if (data.shouldShowLog(emailLogEntry)) {
                emailLogs.add(emailLogEntry);
            }
        }

        return emailLogs;
    }
}
