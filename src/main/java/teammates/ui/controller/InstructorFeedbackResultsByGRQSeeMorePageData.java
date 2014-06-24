package teammates.ui.controller;

import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;

public class InstructorFeedbackResultsByGRQSeeMorePageData extends PageData {
    Map<String, Map<String, List<FeedbackResponseAttributes>>> responses;
    Map<String, List<FeedbackResponseCommentAttributes>> comments;
    
    public InstructorFeedbackResultsByGRQSeeMorePageData(AccountAttributes account) {
        super(account);
    }
}
