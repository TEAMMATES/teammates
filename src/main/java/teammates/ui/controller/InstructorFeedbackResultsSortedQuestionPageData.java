package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;

public class InstructorFeedbackResultsSortedQuestionPageData extends PageData {
    public List<FeedbackResponseAttributes> responses;
    public Map<String, String> answerTable;
    public String questionStats;
    public Map<String, String> emailNameTable;
    public Map<String, String> emailTeamTable;
    
    public InstructorFeedbackResultsSortedQuestionPageData(AccountAttributes account) {
        super(account);
        answerTable = new HashMap<String, String>();
    }
}
