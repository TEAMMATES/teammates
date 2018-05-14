package teammates.ui.template;

import java.time.Instant;

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
        return TimeHelper.formatDateTimeForDisplay(TimeHelper.convertInstantToLocalDateTime(
                Instant.ofEpochMilli(emailEntry.getTime()), Const.SystemParams.ADMIN_TIME_ZONE));
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
