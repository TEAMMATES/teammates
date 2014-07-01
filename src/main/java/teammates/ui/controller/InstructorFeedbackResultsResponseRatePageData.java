package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionResponseStatus;

public class InstructorFeedbackResultsResponseRatePageData extends PageData {
    FeedbackSessionResponseStatus responseStatus;
    
    public InstructorFeedbackResultsResponseRatePageData(AccountAttributes account) {
        super(account);
    }
}
