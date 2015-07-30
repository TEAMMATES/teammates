package teammates.ui.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.ui.controller.InstructorFeedbackResponseCommentsLoadPageData;
import teammates.ui.template.FeedbackResponseComment;

public class InstructorFeedbackResponseComment {
    private Map<String, List<FeedbackResponseComment>> feedbackResponseCommentsLists;
    private Map<FeedbackQuestionDetails, String> responseEntryAnswerHtmls;
    private Map<FeedbackResponseAttributes, String> giverNames;
    private Map<FeedbackResponseAttributes, String> recipientNames;
    private Map<FeedbackQuestionAttributes, String> showResponseCommentToStrings;
    private Map<FeedbackQuestionAttributes, String> showResponseGiverNameToStrings;
    private Map<FeedbackQuestionAttributes, Boolean> responseVisibleToGiver;
    private Map<FeedbackQuestionAttributes, Boolean> responseVisibleToRecipient;
    private Map<FeedbackQuestionAttributes, Boolean> responseVisibleToGiverTeam;
    private Map<FeedbackQuestionAttributes, Boolean> responseVisibleToRecipientTeam;
    private Map<FeedbackQuestionAttributes, Boolean> responseVisibleToStudents;
    private Map<FeedbackQuestionAttributes, Boolean> responseVisibleToInstructors;
    private Map<FeedbackResponseAttributes, Boolean> instructorAllowedToSubmit;
    private HashMap<FeedbackQuestionAttributes, FeedbackResponseComment> feedbackResponseCommentAdd;

    public InstructorFeedbackResponseComment(FeedbackSessionResultsBundle feedbackResultBundle,
                                             InstructorAttributes currentInstructor, String instructorEmail,
                                             InstructorFeedbackResponseCommentsLoadPageData ifrclpd) {
        this.giverNames = new HashMap<FeedbackResponseAttributes, String>();
        this.recipientNames = new HashMap<FeedbackResponseAttributes, String>();
        this.feedbackResponseCommentsLists = new HashMap<String, List<FeedbackResponseComment>>();
        this.responseEntryAnswerHtmls = new HashMap<FeedbackQuestionDetails, String>();
        this.showResponseCommentToStrings = new HashMap<FeedbackQuestionAttributes, String>();
        this.showResponseGiverNameToStrings = new HashMap<FeedbackQuestionAttributes, String>();
        this.responseVisibleToGiver = new HashMap<FeedbackQuestionAttributes, Boolean>();
        this.responseVisibleToRecipient = new HashMap<FeedbackQuestionAttributes, Boolean>();
        this.responseVisibleToGiverTeam = new HashMap<FeedbackQuestionAttributes, Boolean>();
        this.responseVisibleToRecipientTeam = new HashMap<FeedbackQuestionAttributes, Boolean>();
        this.responseVisibleToStudents = new HashMap<FeedbackQuestionAttributes, Boolean>();
        this.responseVisibleToInstructors = new HashMap<FeedbackQuestionAttributes, Boolean>();
    }

    public InstructorFeedbackResponseComment(
                                    HashMap<FeedbackResponseAttributes, String> giverNames,
                                    HashMap<FeedbackResponseAttributes, String> recipientNames,
                                    HashMap<String, List<FeedbackResponseComment>> feedbackResponseCommentsLists,
                                    HashMap<FeedbackQuestionDetails, String> responseEntryAnswerHtmls,
                                    HashMap<FeedbackQuestionAttributes, String> showResponseCommentToStrings,
                                    HashMap<FeedbackQuestionAttributes, String> showResponseGiverNameToStrings,
                                    HashMap<FeedbackQuestionAttributes, Boolean> responseVisibleToGiver,
                                    HashMap<FeedbackQuestionAttributes, Boolean> responseVisibleToRecipient,
                                    HashMap<FeedbackQuestionAttributes, Boolean> responseVisibleToGiverTeam,
                                    HashMap<FeedbackQuestionAttributes, Boolean> responseVisibleToRecipientTeam,
                                    HashMap<FeedbackQuestionAttributes, Boolean> responseVisibleToStudents,
                                    HashMap<FeedbackQuestionAttributes, Boolean> responseVisibleToInstructors,
                                    HashMap<FeedbackResponseAttributes, Boolean> instructorAllowedToSubmitMap,
                                    HashMap<FeedbackQuestionAttributes, FeedbackResponseComment> feedbackResponseCommentAdd) {
        this.giverNames = giverNames;
        this.recipientNames = recipientNames;
        this.feedbackResponseCommentsLists = feedbackResponseCommentsLists;
        this.responseEntryAnswerHtmls = responseEntryAnswerHtmls;
        this.showResponseCommentToStrings = showResponseCommentToStrings;
        this.showResponseGiverNameToStrings = showResponseGiverNameToStrings;
        this.responseVisibleToGiver = responseVisibleToGiver;
        this.responseVisibleToRecipient = responseVisibleToRecipient;
        this.responseVisibleToGiverTeam = responseVisibleToGiverTeam;
        this.responseVisibleToRecipientTeam = responseVisibleToRecipientTeam;
        this.responseVisibleToStudents = responseVisibleToStudents;
        this.responseVisibleToInstructors = responseVisibleToInstructors;
        this.instructorAllowedToSubmit = instructorAllowedToSubmitMap;
        this.feedbackResponseCommentAdd = feedbackResponseCommentAdd;
    }

    public Map<FeedbackResponseAttributes, String> getGiverNames() {
        return giverNames;
    }

    public Map<FeedbackResponseAttributes, String> getRecipientNames() {
        return recipientNames;
    }

    public Map<FeedbackQuestionDetails, String> getResponseEntryAnswerHtmls() {
        return responseEntryAnswerHtmls;
    }

    public Map<String, List<FeedbackResponseComment>> getFeedbackResponseCommentsList() {
        return feedbackResponseCommentsLists;
    }

    public Map<FeedbackQuestionAttributes, String> getShowResponseCommentToStrings() {
        return showResponseCommentToStrings;
    }

    public Map<FeedbackQuestionAttributes, String> getShowResponseGiverNameToStrings() {
        return showResponseGiverNameToStrings;
    }

    public Map<FeedbackQuestionAttributes, Boolean> getResponseVisibleToGiver() {
        return responseVisibleToGiver;
    }

    public Map<FeedbackQuestionAttributes, Boolean> getResponseVisibleToRecipient() {
        return responseVisibleToRecipient;
    }

    public Map<FeedbackQuestionAttributes, Boolean> getResponseVisibleToGiverTeam() {
        return responseVisibleToGiverTeam;
    }

    public Map<FeedbackQuestionAttributes, Boolean> getResponseVisibleToRecipientTeam() {
        return responseVisibleToRecipientTeam;
    }

    public Map<FeedbackQuestionAttributes, Boolean> getResponseVisibleToStudents() {
        return responseVisibleToStudents;
    }

    public Map<FeedbackQuestionAttributes, Boolean> getResponseVisibleToInstructors() {
        return responseVisibleToInstructors;
    }

    public Map<FeedbackResponseAttributes, Boolean> getInstructorAllowedToSubmit() {
        return instructorAllowedToSubmit;
    }

    public HashMap<FeedbackQuestionAttributes, FeedbackResponseComment> getFeedbackResponseCommentAdd() {
        return feedbackResponseCommentAdd;
    }
}
