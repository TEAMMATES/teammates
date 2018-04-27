package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

public class FeedbackResendLinksPageData extends PageData {

    private String error;

    public FeedbackResendLinksPageData(AccountAttributes account, String sessionToken, String error) {
        super(account, sessionToken);
        this.error = error;
    }

    public String getError() {
        return error;
    }

}
