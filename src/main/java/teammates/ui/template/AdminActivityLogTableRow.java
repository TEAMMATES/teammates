package teammates.ui.template;

import java.util.Calendar;
import java.util.TimeZone;

import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;

public class AdminActivityLogTableRow {

    private ActivityLogEntry activityLog;

    public AdminActivityLogTableRow(ActivityLogEntry entry) {
        activityLog = entry;
    }

    public ActivityLogEntry getLogEntry() {
        return activityLog;
    }

    // --------------- Additional generated fields ---------------

    public String getUserHomeLink() {
        switch (activityLog.getUserRole()) {
        case Const.ActivityLog.ROLE_STUDENT:
            return Url.addParamToUrl(Const.ActionURIs.STUDENT_HOME_PAGE,
                                     Const.ParamsNames.USER_ID, activityLog.getUserGoogleId());
        case Const.ActivityLog.ROLE_INSTRUCTOR:
            return Url.addParamToUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE,
                                     Const.ParamsNames.USER_ID, activityLog.getUserGoogleId());
        default:
            return null;
        }
    }

    public boolean getHasUserHomeLink() {
        return activityLog.getUserRole().contains(Const.ActivityLog.ROLE_STUDENT)
                || activityLog.getUserRole().contains(Const.ActivityLog.ROLE_INSTRUCTOR);
    }

    public String getUserIdentity() {
        String googleId = activityLog.getUserGoogleId();
        if (!googleId.contentEquals(Const.ActivityLog.AUTH_NOT_LOGIN)
                && !googleId.contentEquals(Const.ActivityLog.UNKNOWN)) {
            return googleId;
        }

        String email = activityLog.getUserEmail();
        if (email != null && !email.contentEquals(Const.ActivityLog.UNKNOWN)) {
            return email;
        }

        String name = activityLog.getUserName();
        if (name != null && !name.contentEquals(Const.ActivityLog.UNKNOWN)) {
            return name;
        }

        return "";
    }

    public boolean getHasUserEmail() {
        return !Const.ActivityLog.UNKNOWN.contentEquals(activityLog.getUserEmail());
    }

    // --------------- 'is' fields to determine CSS classes ---------------

    public boolean getIsUserAdmin() {
        return activityLog.getUserRole().contains(Const.ActivityLog.ROLE_ADMIN);
    }

    public boolean getIsUserInstructor() {
        return activityLog.getUserRole().contains(Const.ActivityLog.ROLE_INSTRUCTOR);
    }

    public boolean getIsUserStudent() {
        return activityLog.getUserRole().contains(Const.ActivityLog.ROLE_STUDENT);
    }

    public boolean getIsUserAuto() {
        return activityLog.getUserRole().contains(Const.ActivityLog.ROLE_AUTO);
    }

    public boolean getIsUserUnregistered() {
        return activityLog.getUserRole().contains(Const.ActivityLog.ROLE_UNREGISTERED);
    }

    public boolean getIsActionTimeTakenModerate() {
        return activityLog.getActionTimeTaken() >= Const.ActivityLog.TIME_TAKEN_EXPECTED
                && activityLog.getActionTimeTaken() <= Const.ActivityLog.TIME_TAKEN_MODERATE;
    }

    public boolean getIsActionTimeTakenSlow() {
        return activityLog.getActionTimeTaken() > Const.ActivityLog.TIME_TAKEN_MODERATE;
    }

    public boolean getIsActionFailure() {
        return activityLog.getActionResponse().contains(Const.ACTION_RESULT_FAILURE);
    }

    public boolean getIsActionErrorReport() {
        return activityLog.getActionName().contains(Const.ACTION_RESULT_SYSTEM_ERROR_REPORT);
    }

    // --------------- Enhancement to the fields ---------------

    public String getDisplayedActionUrl() {
        return SanitizationHelper.sanitizeForHtml(Url.addParamToUrl(activityLog.getActionUrl(),
                                                  Const.ParamsNames.USER_ID, activityLog.getUserGoogleId()));
    }

    public String getDisplayedLogTime() {
        Calendar appCal = Calendar.getInstance(TimeZone.getTimeZone(Const.DEFAULT_TIMEZONE));
        appCal.setTimeInMillis(activityLog.getLogTime());
        appCal = TimeHelper.convertToUserTimeZone(appCal, Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
        return TimeHelper.calendarToString(appCal);
    }

    public String getDisplayedRole() {
        return activityLog.getUserRole()
                + (activityLog.isMasqueradeUserRole() ? Const.ActivityLog.ROLE_MASQUERADE_POSTFIX : "");
    }

    public String getDisplayedLogTimeTaken() {
        return TimeHelper.convertToStandardDuration(activityLog.getActionTimeTaken());
    }

    public String getDisplayedActionName() {
        return SanitizationHelper.sanitizeForHtml(activityLog.getActionName());
    }

    public String getDisplayedMessage() {
        return SanitizationHelper.sanitizeForLogMessage(activityLog.getLogMessage());
    }

    // --------------- Forwarding activityLog methods ---------------

    public String getUserGoogleId() {
        return activityLog.getUserGoogleId();
    }

    public String getUserName() {
        return activityLog.getUserName();
    }

    public String getUserEmail() {
        return activityLog.getUserEmail();
    }

    public String getLogId() {
        return activityLog.getLogId();
    }

    public String getLogTime() {
        return String.valueOf(activityLog.getLogTime());
    }

    public boolean getIsMasqueradeUserRole() {
        return activityLog.isMasqueradeUserRole();
    }

}
