package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionResponseStatus;

public class InstructorFeedbackRemindParticularStudentsPageData extends PageData {
    public FeedbackSessionResponseStatus responseStatus;
    
    public InstructorFeedbackRemindParticularStudentsPageData(AccountAttributes account) {
        super(account);
    }
}
