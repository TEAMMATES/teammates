package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.TimeHelper;
import teammates.ui.pagedata.AdminSessionsPageData;

public class AdminSessionsPageAction extends Action {

    private static final String UNKNOWN_INSTITUTION = "Unknown";

    private AdminSessionsPageData data;

    private Map<String, List<FeedbackSessionAttributes>> map;
    private Map<String, String> sessionToInstructorIdMap = new HashMap<>();
    private int totalOngoingSessions;
    private int totalOpenStatusSessions;
    private int totalClosedStatusSessions;
    private int totalWaitToOpenStatusSessions;
    private int totalInstitutes;
    private Date rangeStart;
    private Date rangeEnd;
    private double zone;
    private boolean isShowAll;

    @Override
    protected ActionResult execute() {

        gateKeeper.verifyAdminPrivileges(account);
        data = new AdminSessionsPageData(account, sessionToken);

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
            Map<String, List<FeedbackSessionAttributes>> map, FeedbackSessionAttributes fs) {
        if (map.get("Unknown") == null) {
            List<FeedbackSessionAttributes> newList = new ArrayList<>();
            newList.add(fs);
            map.put("Unknown", newList);
        } else {
            map.get("Unknown").add(fs);
        }
    }

    private void prepareDefaultPageData(Calendar calStart, Calendar calEnd) {
        this.map = new HashMap<>();
        this.totalOngoingSessions = 0;
        this.totalOpenStatusSessions = 0;
        this.totalClosedStatusSessions = 0;
        this.totalOpenStatusSessions = 0;
        this.totalInstitutes = 0;
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

            SanitizationHelper.sanitizeForHtml(startDate);
            SanitizationHelper.sanitizeForHtml(endDate);
            SanitizationHelper.sanitizeForHtml(startHour);
            SanitizationHelper.sanitizeForHtml(endHour);
            SanitizationHelper.sanitizeForHtml(startMin);
            SanitizationHelper.sanitizeForHtml(endMin);
            SanitizationHelper.sanitizeForHtml(timeZone);

            zone = Double.parseDouble(timeZone);

            start = TimeHelper.convertToDate(TimeHelper.convertToRequiredFormat(startDate, startHour, startMin));
            end = TimeHelper.convertToDate(TimeHelper.convertToRequiredFormat(endDate, endHour, endMin));

            if (start.after(end)) {
                isError = true;
                statusToUser.add(new StatusMessage("The filter range is not valid."
                                 + " End time should be after start time.", StatusMessageColor.DANGER));
                statusToAdmin = "Admin Sessions Page Load<br>"
                              + "<span class=\"bold\"> Error: invalid filter range</span>";

                prepareDefaultPageData(calStart, calEnd);
                data.init(this.map, this.sessionToInstructorIdMap, this.totalOngoingSessions,
                          this.totalOpenStatusSessions, this.totalClosedStatusSessions,
                          this.totalWaitToOpenStatusSessions, this.totalInstitutes, this.rangeStart,
                          this.rangeEnd, this.zone, this.isShowAll);
                return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
            }

        } else {

            isError = true;
            statusToUser.add(new StatusMessage("Error: Missing Parameters", StatusMessageColor.DANGER));
            statusToAdmin = "Admin Sessions Page Load<br>"
                          + "<span class=\"bold\"> Error: Missing Parameters</span>";

            prepareDefaultPageData(calStart, calEnd);
            data.init(this.map, this.sessionToInstructorIdMap, this.totalOngoingSessions,
                      this.totalOpenStatusSessions, this.totalClosedStatusSessions, this.totalWaitToOpenStatusSessions,
                      this.totalInstitutes, this.rangeStart, this.rangeEnd, this.zone, this.isShowAll);
            return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);

        }

        this.rangeStart = start;
        this.rangeEnd = end;
        this.zone = zone;

        return null;
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
            data.init(this.map, this.sessionToInstructorIdMap, this.totalOngoingSessions,
                      this.totalOpenStatusSessions, this.totalClosedStatusSessions, this.totalWaitToOpenStatusSessions,
                      this.totalInstitutes, this.rangeStart, this.rangeEnd, this.zone, this.isShowAll);
            return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
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
            } else {
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
        }
        this.map = map;
        this.totalInstitutes = getTotalInstitutes(map);
        statusToAdmin = "Admin Sessions Page Load<br>"
                      + "<span class=\"bold\">Total Ongoing Sessions:</span> "
                      + this.totalOngoingSessions
                      + "<span class=\"bold\">Total Opened Sessions:</span> "
                      + this.totalOpenStatusSessions;

        constructSessionToInstructorIdMap();
        data.init(this.map, this.sessionToInstructorIdMap, this.totalOngoingSessions,
                  this.totalOpenStatusSessions, this.totalClosedStatusSessions, this.totalWaitToOpenStatusSessions,
                  this.totalInstitutes, this.rangeStart, this.rangeEnd, this.zone, this.isShowAll);
        return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
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

    private int getTotalNumOfOpenStatusSession(List<FeedbackSessionAttributes> allOpenFeedbackSessionsList) {

        int numOfTotal = 0;
        for (FeedbackSessionAttributes sessionAttributes : allOpenFeedbackSessionsList) {
            if (sessionAttributes.isOpened()) {
                numOfTotal += 1;
            }
        }

        return numOfTotal;
    }

    private int getTotalNumOfCloseStatusSession(List<FeedbackSessionAttributes> allOpenFeedbackSessionsList) {

        int numOfTotal = 0;
        for (FeedbackSessionAttributes sessionAttributes : allOpenFeedbackSessionsList) {
            if (sessionAttributes.isClosed()) {
                numOfTotal += 1;
            }
        }

        return numOfTotal;
    }

    private int getTotalNumOfWaitToOpenStatusSession(List<FeedbackSessionAttributes> allOpenFeedbackSessionsList) {

        int numOfTotal = 0;
        for (FeedbackSessionAttributes sessionAttributes : allOpenFeedbackSessionsList) {
            if (sessionAttributes.isWaitingToOpen()) {
                numOfTotal += 1;
            }
        }

        return numOfTotal;
    }

    private int getTotalInstitutes(Map<String, List<FeedbackSessionAttributes>> map) {

        int numOfTotal = 0;
        for (String key : map.keySet()) {
            if (!key.equals(UNKNOWN_INSTITUTION)) {
                numOfTotal += 1;
            }
        }
        return numOfTotal;
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

        } else if (condition.contentEquals("notNull")) {

            return startDate != null && endDate != null && startHour != null
                   && endHour != null && startMin != null && endMin != null && timeZone != null
                   && !startDate.trim().isEmpty() && !endDate.trim().isEmpty() && !startHour.trim().isEmpty()
                   && !endHour.trim().isEmpty() && !startMin.trim().isEmpty()
                   && !endMin.trim().isEmpty() && !timeZone.trim().isEmpty();

        } else {
            return false;
        }

    }

}
