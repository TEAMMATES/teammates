package teammates.ui.pagedata;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.ui.template.AdminFeedbackSessionRow;
import teammates.ui.template.AdminFilter;
import teammates.ui.template.InstitutionPanel;

public class AdminSessionsPageData extends PageData {
    private long totalOngoingSessions;
    private long totalOpenStatusSessions;
    private long totalClosedStatusSessions;
    private long totalWaitToOpenStatusSessions;
    private long totalInstitutes;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private ZoneId timeZone;
    private boolean isShowAll;
    private List<InstitutionPanel> institutionPanels;
    private AdminFilter filter;

    public AdminSessionsPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);

    }

    public void init(
            Map<String, List<FeedbackSessionAttributes>> map, Map<String, String> sessionToInstructorIdMap,
            long totalOngoingSessions, long totalOpenStatusSessions, long totalClosedStatusSessions,
            long totalWaitToOpenStatusSessions, long totalInstitutes, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            ZoneId timeZone, boolean isShowAll) {

        this.totalOngoingSessions = totalOngoingSessions;
        this.totalOpenStatusSessions = totalOpenStatusSessions;
        this.totalClosedStatusSessions = totalClosedStatusSessions;
        this.totalWaitToOpenStatusSessions = totalWaitToOpenStatusSessions;
        this.totalInstitutes = totalInstitutes;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.timeZone = timeZone;
        this.isShowAll = isShowAll;
        setFilter();
        setInstitutionPanels(map, sessionToInstructorIdMap);
    }

    public long getTotalOngoingSessions() {
        return totalOngoingSessions;
    }

    public long getTotalOpenStatusSessions() {
        return totalOpenStatusSessions;
    }

    public long getTotalClosedStatusSessions() {
        return totalClosedStatusSessions;
    }

    public long getTotalWaitToOpenStatusSessions() {
        return totalWaitToOpenStatusSessions;
    }

    public long getTotalInstitutes() {
        return totalInstitutes;
    }

    public boolean isShowAll() {
        return isShowAll;
    }

    public AdminFilter getFilter() {
        return filter;
    }

    public String getRangeStartString() {
        return TimeHelper.formatDateTimeForDisplay(rangeStart);
    }

    public String getRangeEndString() {
        return TimeHelper.formatDateTimeForDisplay(rangeEnd);
    }

    public List<InstitutionPanel> getInstitutionPanels() {
        return institutionPanels;
    }

    private String getInstructorHomePageViewLink(String googleId) {
        String link = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, googleId);
        return "href=\"" + link + "\"";
    }

    @SuppressWarnings("deprecation")
    public List<String> getHourOptionsAsHtml(LocalDateTime ldt) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            result.add("<option value=\"" + i + "\"" + " "
                    + (ldt.getHour() == i ? "selected" : "")
                    + ">" + String.format("%02dH", i) + "</option>");
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    public List<String> getMinuteOptionsAsHtml(LocalDateTime ldt) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i <= 59; i++) {
            result.add("<option value=\"" + i + "\"" + " "
                    + (ldt.getMinute() == i ? "selected" : "")
                    + ">" + String.format("%02d", i) + "</option>");
        }
        return result;
    }

    public List<String> getTimeZoneOptionsAsHtml() {
        return getTimeZoneOptionsAsHtml(timeZone);
    }

    public String getTimeZoneAsString() {
        return timeZone.toString();
    }

    public String getFeedbackSessionStatsLink(String courseId, String feedbackSessionName, String user) {
        if (user.isEmpty()) {
            return "";
        }

        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, user);
        return link;
    }

    public String getSessionStatusForShow(FeedbackSessionAttributes fs) {
        StringBuilder status = new StringBuilder(100);

        if (fs.isClosed()) {
            status.append("[Closed]");
        }
        if (fs.isOpened()) {
            status.append("[Opened]");
        }
        if (fs.isWaitingToOpen()) {
            status.append("[Waiting To Open]");
        }
        if (fs.isPublished()) {
            status.append("[Published]");
        }
        if (fs.isInGracePeriod()) {
            status.append("[Grace Period]");
        }

        return status.length() == 0 ? "No Status" : status.toString();
    }

    public List<AdminFeedbackSessionRow> getFeedbackSessionRows(
            List<FeedbackSessionAttributes> feedbackSessions, Map<String, String> sessionToInstructorIdMap) {
        List<AdminFeedbackSessionRow> feedbackSessionRows = new ArrayList<>();
        for (FeedbackSessionAttributes feedbackSession : feedbackSessions) {
            String googleId = sessionToInstructorIdMap.get(feedbackSession.getIdentificationString());
            feedbackSessionRows.add(new AdminFeedbackSessionRow(
                    getSessionStatusForShow(feedbackSession),
                    getFeedbackSessionStatsLink(
                            feedbackSession.getCourseId(),
                            feedbackSession.getFeedbackSessionName(),
                            googleId),
                    TimeHelper.formatDateTimeForDisplay(feedbackSession.getStartTimeLocal()),
                    feedbackSession.getStartTimeInIso8601UtcFormat(),
                    TimeHelper.formatDateTimeForDisplay(feedbackSession.getEndTimeLocal()),
                    feedbackSession.getEndTimeInIso8601UtcFormat(),
                    getInstructorHomePageViewLink(googleId),
                    feedbackSession.getCreatorEmail(),
                    feedbackSession.getCourseId(),
                    feedbackSession.getFeedbackSessionName()));
        }
        return feedbackSessionRows;
    }

    private void setFilter() {
        filter = new AdminFilter(TimeHelper.formatDateForSessionsForm(rangeStart), getHourOptionsAsHtml(rangeEnd),
                getMinuteOptionsAsHtml(rangeStart), TimeHelper.formatDateForSessionsForm(rangeEnd),
                getHourOptionsAsHtml(rangeEnd), getMinuteOptionsAsHtml(rangeEnd),
                getTimeZoneOptionsAsHtml());
    }

    public void setInstitutionPanels(
            Map<String, List<FeedbackSessionAttributes>> map, Map<String, String> sessionToInstructorIdMap) {
        institutionPanels = new ArrayList<>();
        map.forEach((key, feedbackSessionAttributesList) -> institutionPanels.add(
                new InstitutionPanel(
                        key, getFeedbackSessionRows(feedbackSessionAttributesList, sessionToInstructorIdMap))));
    }
}
