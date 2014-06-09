package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.StudentAttributes;

public class InstructorCommentsPageData extends PageData {
    public Boolean isViewingDraft;
    public String courseIdToView;
    public String courseNameToView;
    public List<String> courseIdList;
    public Map<String, List<CommentAttributes>> comments;
    public Map<String, StudentAttributes> students;
    public Boolean isDisplayArchive;
    public Map<String, List<FeedbackQuestionAttributes>> fsNameTofeedbackQuestionsMap;
    public Map<String, List<FeedbackResponseAttributes>> questionIdToFeedbackResponsesMap;
    public Map<String, List<FeedbackResponseCommentAttributes>> responseIdToFrCommentsMap;
    
    public InstructorCommentsPageData(AccountAttributes account) {
        super(account);
    }
}
