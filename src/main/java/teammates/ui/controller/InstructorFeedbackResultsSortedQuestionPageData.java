package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;

public class InstructorFeedbackResultsSortedQuestionPageData extends PageData {
    public Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionResponseMap;
    
    public InstructorFeedbackResultsSortedQuestionPageData(AccountAttributes account) {
        super(account);
    }
}
