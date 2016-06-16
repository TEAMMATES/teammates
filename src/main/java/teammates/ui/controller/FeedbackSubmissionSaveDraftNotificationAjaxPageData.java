package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class FeedbackSubmissionSaveDraftNotificationAjaxPageData extends PageData {

    public String lastSavedAt;
    boolean isError;

    public FeedbackSubmissionSaveDraftNotificationAjaxPageData(AccountAttributes account, String lastSavedAt,
            boolean isError) {
        super(account);
        this.lastSavedAt = lastSavedAt;
        this.isError = isError;
    }
}
