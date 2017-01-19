package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;

public class FeedbackSessionStatsPageData extends PageData {
    public FeedbackSessionDetailsBundle sessionDetails;
    
    public FeedbackSessionStatsPageData(AccountAttributes account) {
        super(account);
    }
}
