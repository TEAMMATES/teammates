package teammates.ui.pagedata;

import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;

public class FeedbackSessionStatsPageData extends PageData {
    public FeedbackSessionDetailsBundle sessionDetails;

    public FeedbackSessionStatsPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }
}
