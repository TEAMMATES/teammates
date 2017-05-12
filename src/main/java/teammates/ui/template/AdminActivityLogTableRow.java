package teammates.ui.template;

import java.util.Calendar;
import java.util.TimeZone;

import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Const;
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
        switch (activityLog.getRoleWithoutMasquerade()) {
        case Const.ActivityLog.ROLE_STUDENT:
            return Url.addParamToUrl(Const.ActionURIs.STUDENT_HOME_PAGE,
                                     Const.ParamsNames.USER_ID, activityLog.getGoogleId());
        case Const.ActivityLog.ROLE_INSTRUCTOR:
            return Url.addParamToUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE,
                                     Const.ParamsNames.USER_ID, activityLog.getGoogleId());
        default:
            return null;
        }
    }

    public boolean getHasUserHomeLink() {
        return activityLog.getRoleWithoutMasquerade().contains(Const.ActivityLog.ROLE_STUDENT)
                || activityLog.getRoleWithoutMasquerade().contains(Const.ActivityLog.ROLE_INSTRUCTOR);
    }

    public String getUserIdentity() {
        String googleId = activityLog.getGoogleId();
        if (!googleId.contentEquals(Const.ActivityLog.AUTH_NOT_LOGIN)
                && !googleId.contentEquals(Const.ActivityLog.UNKNOWN)) {
            return googleId;
        }

        String email = activityLog.getEmail();
        if (email != null && !email.contentEquals(Const.ActivityLog.UNKNOWN)) {
            return email;
        }

        String name = activityLog.getName();
        if (name != null && !name.contentEquals(Const.ActivityLog.UNKNOWN)) {
            return name;
        }

        return "";
    }

    public boolean getHasUserEmail() {
        return !Const.ActivityLog.UNKNOWN.contentEquals(activityLog.getEmail());
    }

    // --------------- 'is' fields to determine CSS classes ---------------

    public boolean getIsUserAdmin() {
        return activityLog.getRole().contains(Const.ActivityLog.ROLE_ADMIN);
    }

    public boolean getIsUserInstructor() {
        return activityLog.getRole().contains(Const.ActivityLog.ROLE_INSTRUCTOR);
    }

    public boolean getIsUserStudent() {
        return activityLog.getRole().contains(Const.ActivityLog.ROLE_STUDENT);
    }

    public boolean getIsUserAuto() {
        return activityLog.getRole().contains(Const.ActivityLog.ROLE_AUTO);
    }

    public boolean getIsUserUnregistered() {
        return activityLog.getRole().contains(Const.ActivityLog.ROLE_UNREGISTERED);
    }

    public boolean getIsActionTimeTakenModerate() {
        return activityLog.getTimeTaken() >= Const.ActivityLog.TIME_TAKEN_EXPECTED
                && activityLog.getTimeTaken() <= Const.ActivityLog.TIME_TAKEN_MODERATE;
    }

    public boolean getIsActionTimeTakenSlow() {
        return activityLog.getTimeTaken() > Const.ActivityLog.TIME_TAKEN_MODERATE;
    }

    public boolean getIsActionFailure() {
        return activityLog.getAction().contains(Const.ACTION_RESULT_FAILURE);
    }

    public boolean getIsActionErrorReport() {
        return activityLog.getAction().contains(Const.ACTION_RESULT_SYSTEM_ERROR_REPORT);
    }

    // --------------- Enhancement to the fields ---------------

    public String getDisplayedActionUrl() {
        return Url.addParamToUrl(activityLog.getUrl(),
                                 Const.ParamsNames.USER_ID, activityLog.getGoogleId());
    }

    public String getDisplayedLogTime() {
        Calendar appCal = Calendar.getInstance(TimeZone.getTimeZone(Const.DEFAULT_TIMEZONE));
        appCal.setTimeInMillis(activityLog.getTime());
        appCal = TimeHelper.convertToUserTimeZone(appCal, Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
        return TimeHelper.calendarToString(appCal);
    }

    public String getDisplayedRole() {
        return activityLog.getRole();
    }

    public String getDisplayedLogTimeTaken() {
        return TimeHelper.convertToStandardDuration(activityLog.getTimeTaken());
    }

    // --------------- Forwarding activityLog methods ---------------

    public String getUserGoogleId() {
        return activityLog.getGoogleId();
    }

    public String getUserName() {
        return activityLog.getName();
    }

    public String getUserEmail() {
        return activityLog.getEmail();
    }

    public String getLogId() {
        return activityLog.getId();
    }

    public String getActionName() {
        return activityLog.getAction();
    }

    public String getLogTime() {
        return String.valueOf(activityLog.getTime());
    }

    public boolean getIsMasqueradeUserRole() {
        return activityLog.isMasqueradeUserRole();
    }

    public String getMessage() {
        return activityLog.getMessage();
    }

}
