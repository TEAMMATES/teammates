package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;

public class InstructorFeedbackResultsByRQGSeeMorePageData extends PageData {
    Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responses;
    Map<String, String> answer;
    Map<String , Map<String, String>> questionsInfo;
    Map<String, Map<String, String>> participantStats;
    Map<String, String> emailTeamNameTable;
    Map<String, String> emailNameTable;
    
    public InstructorFeedbackResultsByRQGSeeMorePageData(AccountAttributes account) {
        super(account);
        answer = new HashMap<String, String>();
        questionsInfo = new HashMap<String, Map<String, String>>();
        participantStats = new HashMap<String, Map<String, String>>();
    }
}
