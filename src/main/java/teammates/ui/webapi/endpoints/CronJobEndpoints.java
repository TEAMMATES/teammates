package teammates.ui.webapi.endpoints;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.Const.CronJobURIs;

/**
 * API endpoints for cron jobs.
 */
public enum CronJobEndpoints {
    //CHECKSTYLE.OFF:JavadocVariable
    AUTOMATED_EXCEPTION_TEST(CronJobURIs.AUTOMATED_EXCEPTION_TEST),
    AUTOMATED_LOG_COMPILATION(CronJobURIs.AUTOMATED_LOG_COMPILATION),
    AUTOMATED_DATASTORE_BACKUP(CronJobURIs.AUTOMATED_DATASTORE_BACKUP),
    AUTOMATED_FEEDBACK_OPENING_REMINDERS(CronJobURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS),
    AUTOMATED_FEEDBACK_CLOSED_REMINDERS(CronJobURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS),
    AUTOMATED_FEEDBACK_CLOSING_REMINDERS(CronJobURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS),
    AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS(CronJobURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS);
    //CHECKSTYLE.ON:JavadocVariable

    private final String url;

    CronJobEndpoints(String s) {
        this.url = s;
    }

    @JsonValue
    public String getUrl() {
        return url;
    }
}
