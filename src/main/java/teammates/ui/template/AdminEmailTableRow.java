package teammates.ui.template;

import java.util.Calendar;
import java.util.TimeZone;

import teammates.common.util.Const;
import teammates.common.util.EmailLogEntry;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;

public class AdminEmailTableRow {

    private EmailLogEntry emailEntry;

    public AdminEmailTableRow(EmailLogEntry entry) {
        emailEntry = entry;
    }

    public EmailLogEntry getLogEntry() {
        return emailEntry;
    }

    // -------- Enhancement to fields in EmailLogEntry --------

    public String getTimeForDisplay() {
        Calendar appCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        appCal.setTimeInMillis(emailEntry.getTime());
        appCal = TimeHelper.convertToUserTimeZone(appCal, Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
        return TimeHelper.formatTime12H(appCal.getTime());
    }

    public String getUnsanitizedContent() {
        return SanitizationHelper.desanitizeFromHtml(emailEntry.getContent());
    }

    public String getSanitizedContent() {
        return SanitizationHelper.sanitizeForHtml(emailEntry.getContent());
    }

    // -------- Forwarding fields in EmailLogEntry --------

    public String getReceiver() {
        return emailEntry.getReceiver();
    }

    public String getSubject() {
        return emailEntry.getSubject();
    }
}
