package teammates.ui.template;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;

public class AdminActivityLogTableRow {
    
    private static final int TIME_TAKEN_WARNING_LOWER_RANGE = 10000;
    private static final int TIME_TAKEN_WARNING_UPPER_RANGE = 20000;
    private static final int TIME_TAKEN_DANGER_UPPER_RANGE = 60000;
    
    private static final String MASQUERADE_ROLE_ICON_CLASS = "glyphicon glyphicon-eye-open text-danger";
    
    private static final String ACTION_UNSCCUSSFUL_HIGHLIGHTER_FRONT = "<span class=\"text-danger\"><strong>";
    private static final String ACTION_UNSCCUSSFUL_HIGHLIGHTER_BACK = "</strong><span>";
    
    private static final String KEYWORDS_HIGHLIGHTER_FRONT = "<mark>";
    private static final String KEYWORDS_HIGHLIGHTER_BACK = "</mark>";
    
    private enum TimeTakenCssClass {
        NORMAL("", ""),
        WARNING("warning", "text-warning"),
        DANGER("danger", "text-danger");
        
        private String cellClass;
        private String textClass;
        
        TimeTakenCssClass(String cClass, String tClass) {
            cellClass = cClass;
            textClass = tClass;
        }
          
        public String toHtmlTableCellClass() {
            return cellClass;
        }
        
        public String toHtmlTextClass() {
            return textClass;
        }
        
        public static TimeTakenCssClass generateTimeTakenEnum(long timeTaken) {
            if (timeTaken >= TIME_TAKEN_WARNING_LOWER_RANGE && timeTaken <= TIME_TAKEN_WARNING_UPPER_RANGE) {
                return WARNING;
            } else if (timeTaken > TIME_TAKEN_WARNING_UPPER_RANGE && timeTaken <= TIME_TAKEN_DANGER_UPPER_RANGE) {
                return DANGER;
            } else {
                return NORMAL;
            }
        }
    }
    
    private enum ActionTypeCssClass {
        NORMAL("btn-info", "text-success bold"),
        WARNING("btn-warning", "text-danger"),
        DANGER("btn-danger", "text-dnager");
        
        private String buttonClass;
        private String textClass;
        
        ActionTypeCssClass(String cClass, String tClass) {
            buttonClass = cClass;
            textClass = tClass;
        }
          
        public String toHtmlButtonClass() {
            return buttonClass;
        }
        
        public String toHtmlTextClass() {
            return textClass;
        }
        
        public static ActionTypeCssClass generateActionTypeEnum(String action) {
            switch(action) {
            case Const.ACTION_RESULT_FAILURE:
                return WARNING;
            case Const.ACTION_RESULT_SYSTEM_ERROR_REPORT: // fall through
                return DANGER;
            default:
                return NORMAL;
            }
        }
    }
    
    private enum UserRoleCssClass {
        ADMIN("glyphicon glyphicon-user text-danger"),
        INSTRUCTOR("glyphicon glyphicon-user text-primary"),
        STUDENT("glyphicon glyphicon-user text-warning"),
        AUTO("glyphicon glyphicon-cog"),
        UNREGISTERED("glyphicon glyphicon-user"),
        UNKNOWN("");
        
        private String iconCssClass;
        
        UserRoleCssClass(String className) {
            iconCssClass = className;
        }
        
        public static UserRoleCssClass generateRoleCssClassHelper(String role) {
            if (role.equals(Const.ActivityLog.ROLE_ADMIN)) {
                return ADMIN;
            }
            
            if (role.equals(Const.ActivityLog.ROLE_INSTRUCTOR)) {
                return INSTRUCTOR;
            }
            
            if (role.equals(Const.ActivityLog.ROLE_STUDENT)) {
                return STUDENT;
            }
            
            if (role.equals(Const.ActivityLog.ROLE_AUTO)) {
                return AUTO;
            }
            
            if (role.contains(Const.ActivityLog.ROLE_UNREGISTERED)) {
                return UNREGISTERED;
            }
            
            return UNKNOWN;
        }
        
        public String getIconCssClass() {
            return iconCssClass;
        }
    }
    
    private ActivityLogEntry activityLog;
    
    private String[] keyStringsToHighlight;
    
    public AdminActivityLogTableRow(ActivityLogEntry entry) {
        activityLog = entry;
    }
    
    public void setKeyStringsToHighlight(String[] strs) {
        keyStringsToHighlight = strs;
    }
    
    // --------------- Additional generated fields ---------------
    
    public String getUserHomeLink() {
        switch(activityLog.getUserRole()) {
        case Const.ActivityLog.ROLE_STUDENT:
            return Url.addParamToUrl(Const.ActionURIs.STUDENT_HOME_PAGE,
                    Const.ParamsNames.USER_ID, activityLog.getUserGoogleId());
        case Const.ActivityLog.ROLE_INSTRUCTOR:
            return Url.addParamToUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE,
                    Const.ParamsNames.USER_ID, activityLog.getUserGoogleId());
        default:
            return activityLog.getUserGoogleId();
        }
    }
    
    public boolean getHasUserHomeLink() {
        return activityLog.getUserRole().contains(Const.ActivityLog.ROLE_STUDENT)
                || activityLog.getUserRole().contains(Const.ActivityLog.ROLE_INSTRUCTOR);
    }
    
    public String getUserIdentity() {
        String googleId = activityLog.getUserGoogleId();
        if (!googleId.contentEquals(Const.ActivityLog.AUTH_UNLOGIN)
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
        return !activityLog.getUserEmail().contains(Const.ActivityLog.UNKNOWN);
    }
    
    // --------------- Css Classes of elements ---------------
    
    public String getTableCellClass() {
        return TimeTakenCssClass.generateTimeTakenEnum(activityLog.getActionTimeTaken()).toHtmlTableCellClass();
    }

    public String getTimeTakenClass() {
        return TimeTakenCssClass.generateTimeTakenEnum(activityLog.getActionTimeTaken()).toHtmlTextClass();
    }

    public String getUserRoleIconClass() {
        return UserRoleCssClass.generateRoleCssClassHelper(activityLog.getUserRole()).getIconCssClass();
    }

    public String getMasqueradeUserRoleIconClass() {
        if (activityLog.isMasqueradeUserRole()) {
            return MASQUERADE_ROLE_ICON_CLASS;
        } else {
            return "";
        }
    }

    public String getActionTextClass() {
        return ActionTypeCssClass.generateActionTypeEnum(activityLog.getActionName()).toHtmlTextClass();
    }

    public String getActionButtonClass() {
        return ActionTypeCssClass.generateActionTypeEnum(activityLog.getActionName()).toHtmlButtonClass();
    }
    
    // --------------- Enhancement to the fields ---------------

    public String getDisplayedActionUrl() {
        if (activityLog.isMasqueradeUserRole()) {
            return activityLog.getUserGoogleId();
        } else {
            return Url.addParamToUrl(activityLog.getActionUrl(),
                    Const.ParamsNames.USER_ID, activityLog.getUserGoogleId());
        }
    }

    // TODO find a way to make use of TimeHelper
    public String getDisplayedLogTime() {
        Calendar appCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(Const.SystemParams.ADMIN_TIME_ZONE));
        appCal.setTimeInMillis(activityLog.getLogTime());
    
        return sdf.format(appCal.getTime());
    }

    public String getDisplayedRole() {
        return activityLog.getUserRole() + (activityLog.isMasqueradeUserRole()
                ? Const.ActivityLog.MASQUERADE_ROLE_POSTFIX : "");
    }

    public String getDisplayedLogTimeTaken() {
        return TimeHelper.convertToStandardDuration(activityLog.getActionTimeTaken());
    }

    public String getDisplayedMessage() {
        String displayedMessage = activityLog.getLogMessage();
        String[] keywords = {
                Const.ACTION_RESULT_FAILURE,
                Const.ACTION_RESULT_SYSTEM_ERROR_REPORT
        };
        displayedMessage = highlightKeyword(displayedMessage, keywords,
                ACTION_UNSCCUSSFUL_HIGHLIGHTER_FRONT, ACTION_UNSCCUSSFUL_HIGHLIGHTER_BACK);
        displayedMessage = highlightKeyword(displayedMessage, keyStringsToHighlight,
                KEYWORDS_HIGHLIGHTER_FRONT, KEYWORDS_HIGHLIGHTER_BACK);
        return displayedMessage;
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

    public String getActionName() {
        return activityLog.getActionName();
    }

    public String getLogTime() {
        return String.valueOf(activityLog.getLogTime());
    }
    
    // --------------- Helper methods ---------------
    
    // TODO: can be generalized to helper method
    private String highlightKeyword(String original, String[] keywords,
                                    String wrapperTagFront, String wrapperTagEnd) {
        
        if (keywords == null) {
            return original;
        }
        
        String highlightedString = original;
        
        for (String stringToHighlight : keywords) {
            if (highlightedString.toLowerCase().contains(stringToHighlight.toLowerCase())) {
                
                int startIndex = original.toLowerCase().indexOf(stringToHighlight.toLowerCase());
                int endIndex = startIndex + stringToHighlight.length();
                String realStringToHighlight = original.substring(startIndex, endIndex);
                highlightedString = highlightedString.replace(realStringToHighlight,
                        wrapperTagFront + realStringToHighlight + wrapperTagEnd);
            }
        }
        
        return highlightedString;
        
    }
       
}
