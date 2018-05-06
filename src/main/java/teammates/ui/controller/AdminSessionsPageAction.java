package teammates.ui.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.TimeHelper;
import teammates.ui.pagedata.AdminSessionsPageData;

public class AdminSessionsPageAction extends Action {

    private static final String UNKNOWN_INSTITUTION = "Unknown";

    private AdminSessionsPageData data;

    private Map<String, List<FeedbackSessionAttributes>> map;
    private Map<String, String> sessionToInstructorIdMap = new HashMap<>();
    private long totalOngoingSessions;
    private long totalOpenStatusSessions;
    private long totalClosedStatusSessions;
    private long totalWaitToOpenStatusSessions;
    private long totalInstitutes;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private ZoneId timeZone;
    private boolean isShowAll;

    @Override
    protected ActionResult execute() {
        gateKeeper.verifyAdminPrivileges(account);
        data = new AdminSessionsPageData(account, sessionToken);
        isShowAll = getRequestParamAsBoolean("all");

        ActionResult result = validateParametersAndCreateShowPageResultIfInvalid();
        if (result != null) {
            return result;
        }

        List<FeedbackSessionAttributes> allOpenFeedbackSessionsList = logic.getAllOpenFeedbackSessions(
                TimeHelper.convertLocalDateTimeToInstant(rangeStart, timeZone),
                TimeHelper.convertLocalDateTimeToInstant(rangeEnd, timeZone)
        );

        result = createShowPageResultIfNoOngoingSession(allOpenFeedbackSessionsList);
        if (result != null) {
            return result;
        }

        return createAdminSessionPageResult(allOpenFeedbackSessionsList);
    }

    private void putIntoUnknownList(Map<String, List<FeedbackSessionAttributes>> map, FeedbackSessionAttributes fs) {
        if (map.get("Unknown") != null) {
            map.get("Unknown").add(fs);
            return;
        }

        List<FeedbackSessionAttributes> newList = new ArrayList<>();
        newList.add(fs);
        map.put("Unknown", newList);
    }

    private void prepareDefaultPageData() {
        this.map = new HashMap<>();
        this.totalOngoingSessions = 0;
        this.totalOpenStatusSessions = 0;
        this.totalClosedStatusSessions = 0;
        this.totalOpenStatusSessions = 0;
        this.totalInstitutes = 0;
        this.rangeStart = LocalDateTime.now(Const.DEFAULT_TIME_ZONE).minusDays(3);
        this.rangeEnd = LocalDateTime.now(Const.DEFAULT_TIME_ZONE).plusDays(4);
    }

    private ActionResult validateParametersAndCreateShowPageResultIfInvalid() {
        if (checkAllParameters("null")) {
            this.rangeStart = LocalDateTime.now(Const.DEFAULT_TIME_ZONE).minusDays(3);
            this.rangeEnd = LocalDateTime.now(Const.DEFAULT_TIME_ZONE).plusDays(4);
            this.timeZone = Const.DEFAULT_TIME_ZONE;
            return null;
        }

        if (checkAllParameters("notNull")) {
            String startDate = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE);
            String endDate = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE);
            String startHour = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTHOUR);
            String endHour = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDHOUR);
            String startMin = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTMINUTE);
            String endMin = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDMINUTE);
            String timeZone = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE);

            this.rangeStart = TimeHelper.parseDateTimeFromSessionsForm(startDate, startHour, startMin);
            this.rangeEnd = TimeHelper.parseDateTimeFromSessionsForm(endDate, endHour, endMin);
            this.timeZone = TimeHelper.parseZoneId(timeZone);
            // validate parsed filter fields
            if (this.rangeStart == null || this.rangeEnd == null || this.timeZone == null) {
                isError = true;
                statusToUser.add(new StatusMessage("Invalid date/timezone format.", StatusMessageColor.DANGER));
                statusToAdmin = "Admin Sessions Page Load<br>"
                        + "<span class=\"bold\"> Error: invalid date/timezone format in filter range</span>";

                prepareDefaultPageData();
                return initializeDataAndCreateShowPageResult();
            }

            if (this.rangeStart.isAfter(this.rangeEnd)) {
                isError = true;
                statusToUser.add(new StatusMessage("The filter range is not valid."
                        + " End time should be after start time.", StatusMessageColor.DANGER));
                statusToAdmin = "Admin Sessions Page Load<br>"
                        + "<span class=\"bold\"> Error: invalid filter range</span>";
                prepareDefaultPageData();
                return initializeDataAndCreateShowPageResult();
            }

            return null;
        }

        isError = true;
        statusToUser.add(new StatusMessage("Error: Missing Parameters", StatusMessageColor.DANGER));
        statusToAdmin = "Admin Sessions Page Load<br>"
                + "<span class=\"bold\"> Error: Missing Parameters</span>";

        prepareDefaultPageData();
        return initializeDataAndCreateShowPageResult();
    }

    private ActionResult initializeDataAndCreateShowPageResult() {
        data.init(this.map, this.sessionToInstructorIdMap, this.totalOngoingSessions,
                this.totalOpenStatusSessions, this.totalClosedStatusSessions, this.totalWaitToOpenStatusSessions,
                this.totalInstitutes, this.rangeStart, this.rangeEnd, this.timeZone, this.isShowAll);
        return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
    }

    private ActionResult createShowPageResultIfNoOngoingSession(
            List<FeedbackSessionAttributes> allOpenFeedbackSessionsList) {
        if (allOpenFeedbackSessionsList.isEmpty()) {
            isError = false;
            statusToUser.add(new StatusMessage("Currently No Ongoing Sessions", StatusMessageColor.WARNING));
            statusToAdmin = "Admin Sessions Page Load<br>"
                          + "<span class=\"bold\"> No Ongoing Sessions</span>";

            this.map = new HashMap<>();
            this.totalOngoingSessions = 0;
            this.totalOpenStatusSessions = 0;
            this.totalClosedStatusSessions = 0;
            this.totalWaitToOpenStatusSessions = 0;
            this.totalInstitutes = 0;
            return initializeDataAndCreateShowPageResult();
        }

        return null;
    }

    private ActionResult createAdminSessionPageResult(List<FeedbackSessionAttributes> allOpenFeedbackSessionsList) {
        HashMap<String, List<FeedbackSessionAttributes>> map = new HashMap<>();
        this.totalOngoingSessions = allOpenFeedbackSessionsList.size();
        this.totalOpenStatusSessions = getTotalNumOfOpenStatusSession(allOpenFeedbackSessionsList);
        this.totalClosedStatusSessions = getTotalNumOfCloseStatusSession(allOpenFeedbackSessionsList);
        this.totalWaitToOpenStatusSessions = getTotalNumOfWaitToOpenStatusSession(allOpenFeedbackSessionsList);

        for (FeedbackSessionAttributes fs : allOpenFeedbackSessionsList) {
            List<InstructorAttributes> instructors = logic.getInstructorsForCourse(fs.getCourseId());

            if (instructors.isEmpty()) {
                putIntoUnknownList(map, fs);
                continue;
            }

            AccountAttributes account = getRegisteredInstructorAccountFromInstructors(instructors);
            if (account == null) {
                putIntoUnknownList(map, fs);
                continue;
            }

            if (map.get(account.institute) == null) {
                List<FeedbackSessionAttributes> newList = new ArrayList<>();
                newList.add(fs);
                map.put(account.institute, newList);
            } else {
                map.get(account.institute).add(fs);
            }
        }

        this.map = map;
        this.totalInstitutes = getTotalInstitutes(map);
        statusToAdmin = "Admin Sessions Page Load<br>"
                      + "<span class=\"bold\">Total Ongoing Sessions:</span> "
                      + this.totalOngoingSessions
                      + "<span class=\"bold\">Total Opened Sessions:</span> "
                      + this.totalOpenStatusSessions;

        constructSessionToInstructorIdMap();
        return initializeDataAndCreateShowPageResult();
    }

    private void constructSessionToInstructorIdMap() {
        this.map.forEach((key, feedbackSessionAttributesList) -> {
            for (FeedbackSessionAttributes fs : feedbackSessionAttributesList) {
                String googleId = findAvailableInstructorGoogleIdForCourse(fs.getCourseId());
                this.sessionToInstructorIdMap.put(fs.getIdentificationString(), googleId);
            }
        });
    }

    /**
     * This method loops through all instructors for the given course until a registered Instructor is found.
     * It returns the google id of the found instructor.
     * @return empty string if no available instructor google id is found
     */
    private String findAvailableInstructorGoogleIdForCourse(String courseId) {
        for (InstructorAttributes instructor : logic.getInstructorsForCourse(courseId)) {
            if (instructor.googleId != null) {
                return instructor.googleId;
            }
        }

        return "";
    }

    private AccountAttributes getRegisteredInstructorAccountFromInstructors(List<InstructorAttributes> instructors) {
        for (InstructorAttributes instructor : instructors) {
            if (instructor.googleId != null) {
                return logic.getAccount(instructor.googleId);
            }
        }

        return null;
    }

    private long getTotalNumOfOpenStatusSession(List<FeedbackSessionAttributes> allOpenFeedbackSessionsList) {
        return allOpenFeedbackSessionsList.stream()
                .filter(sessionAttributes -> sessionAttributes.isOpened())
                .count();
    }

    private long getTotalNumOfCloseStatusSession(List<FeedbackSessionAttributes> allOpenFeedbackSessionsList) {
        return allOpenFeedbackSessionsList.stream()
                .filter(sessionAttributes -> sessionAttributes.isClosed())
                .count();
    }

    private long getTotalNumOfWaitToOpenStatusSession(List<FeedbackSessionAttributes> allOpenFeedbackSessionsList) {
        return allOpenFeedbackSessionsList.stream()
                .filter(sessionAttributes -> sessionAttributes.isWaitingToOpen())
                .count();
    }

    private long getTotalInstitutes(Map<String, List<FeedbackSessionAttributes>> map) {
        return map.keySet().stream()
                .filter(key -> !key.equals(UNKNOWN_INSTITUTION))
                .count();
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
            return startDate == null && endDate == null && startHour == null
                   && endHour == null && startMin == null && endMin == null && timeZone == null;
        }

        if (condition.contentEquals("notNull")) {
            return startDate != null && endDate != null && startHour != null
                   && endHour != null && startMin != null && endMin != null && timeZone != null
                   && !startDate.trim().isEmpty() && !endDate.trim().isEmpty() && !startHour.trim().isEmpty()
                   && !endHour.trim().isEmpty() && !startMin.trim().isEmpty()
                   && !endMin.trim().isEmpty() && !timeZone.trim().isEmpty();
        }

        return false;
    }
}
