package teammates.ui.pagedata;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.ui.template.AdminFeedbackSessionRow;
import teammates.ui.template.AdminFilter;
import teammates.ui.template.InstitutionPanel;

public class AdminSessionsPageData extends PageData {
    private int totalOngoingSessions;
    private int totalOpenStatusSessions;
    private int totalClosedStatusSessions;
    private int totalWaitToOpenStatusSessions;
    private int totalInstitutes;
    private Instant rangeStart;
    private Instant rangeEnd;
    private ZoneId timeZone;
    private boolean isShowAll;
    private List<InstitutionPanel> institutionPanels;
    private AdminFilter filter;

    public AdminSessionsPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);

    }

    public void init(
            Map<String, List<FeedbackSessionAttributes>> map, Map<String, String> sessionToInstructorIdMap,
            int totalOngoingSessions, int totalOpenStatusSessions, int totalClosedStatusSessions,
            int totalWaitToOpenStatusSessions, int totalInstitutes, Instant rangeStart, Instant rangeEnd,
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

    public int getTotalOngoingSessions() {
        return totalOngoingSessions;
    }

    public int getTotalOpenStatusSessions() {
        return totalOpenStatusSessions;
    }

    public int getTotalClosedStatusSessions() {
        return totalClosedStatusSessions;
    }

    public int getTotalWaitToOpenStatusSessions() {
        return totalWaitToOpenStatusSessions;
    }

    public int getTotalInstitutes() {
        return totalInstitutes;
    }

    public boolean isShowAll() {
        return isShowAll;
    }

    public AdminFilter getFilter() {
        return filter;
    }

    public String getRangeStartString() {
        return TimeHelper.formatTime12H(getRangeStartLocal());
    }

    public String getRangeEndString() {
        return TimeHelper.formatTime12H(getRangeEndLocal());
    }

    public LocalDateTime getRangeStartLocal() {
        return TimeHelper.convertInstantToLocalDateTime(rangeStart, timeZone);
    }

    public LocalDateTime getRangeEndLocal() {
        return TimeHelper.convertInstantToLocalDateTime(rangeEnd, timeZone);
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
        return getTimeZoneOptionsAsHtml(TimeHelper.convertToOffset(timeZone));
    }

    public String getTimeZoneAsString() {
        return StringHelper.toUtcFormat(TimeHelper.convertToOffset(timeZone));
    }

    public String getFeedbackSessionStatsLink(String courseId, String feedbackSessionName, String user) {
        String link;
        if (user.isEmpty()) {
            link = "";
        } else {
            link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE;
            link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
            link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
            link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, user);
        }
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
                    TimeHelper.formatTime12H(feedbackSession.getStartTimeLocal()),
                    feedbackSession.getStartTimeInIso8601UtcFormat(),
                    TimeHelper.formatTime12H(feedbackSession.getEndTimeLocal()),
                    feedbackSession.getEndTimeInIso8601UtcFormat(),
                    getInstructorHomePageViewLink(googleId),
                    feedbackSession.getCreatorEmail(),
                    feedbackSession.getCourseId(),
                    feedbackSession.getFeedbackSessionName()));
        }
        return feedbackSessionRows;
    }

    private void setFilter() {
        filter = new AdminFilter(TimeHelper.formatDate(getRangeStartLocal()), getHourOptionsAsHtml(getRangeEndLocal()),
                                 getMinuteOptionsAsHtml(getRangeStartLocal()), TimeHelper.formatDate(getRangeEndLocal()),
                                 getHourOptionsAsHtml(getRangeEndLocal()), getMinuteOptionsAsHtml(getRangeEndLocal()),
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
