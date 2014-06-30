package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;

public class InstructorFeedbackResultsByRQGSeeMorePageData extends PageData {
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responses;
    public Map<String, String> answer;
    public Map<String , Map<String, String>> questionsInfo;
    public Map<String, Map<String, String>> participantStats;
    public Map<String, String> emailTeamNameTable;
    public Map<String, String> emailNameTable;
    
    public InstructorFeedbackResultsByRQGSeeMorePageData(AccountAttributes account) {
        super(account);
        answer = new HashMap<String, String>();
        questionsInfo = new HashMap<String, Map<String, String>>();
        participantStats = new HashMap<String, Map<String, String>>();
    }
}
