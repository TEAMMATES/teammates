package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;

public class InstructorFeedbackResultsByRGQSeeMorePageData extends PageData {
    public Map<String, Map<String, List<FeedbackResponseAttributes>>> responses;
    public Map<String, List<FeedbackResponseCommentAttributes>> comments;
    public Map<String, String> answer;
    public Map<String , Map<String, String>> questionsInfo;
    public Map<String, Map<String, String>> privilegesInfo;
    public Map<String, String> emailTeamNameTable;
    
    public InstructorFeedbackResultsByRGQSeeMorePageData(AccountAttributes account) {
        super(account);
        answer = new HashMap<String, String>();
        questionsInfo = new HashMap<>();
        privilegesInfo = new HashMap<>();
    }
}
