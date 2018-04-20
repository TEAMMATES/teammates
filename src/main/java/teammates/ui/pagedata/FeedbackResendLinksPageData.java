package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

public class FeedbackResendLinksPageData extends PageData {

    private boolean isValid;

    public FeedbackResendLinksPageData(AccountAttributes account, String sessionToken, boolean isValid) {
        super(account, sessionToken);
        this.isValid = isValid;
    }

    public boolean isValidEmail() {
        return isValid;
    }

}
