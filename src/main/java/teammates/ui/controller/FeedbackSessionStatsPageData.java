package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;

public class FeedbackSessionStatsPageData extends PageData {
    public FeedbackSessionDetailsBundle sessionDetails;
    
    public FeedbackSessionStatsPageData(AccountAttributes account) {
        super(account);
    }
}
