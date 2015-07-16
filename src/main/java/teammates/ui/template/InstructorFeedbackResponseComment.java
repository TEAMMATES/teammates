package teammates.ui.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.template.FeedbackResponseComment;

public class InstructorFeedbackResponseComment {
    private Map<String, FeedbackSessionResultsBundle> feedbackResultBundles;
    private Map<String, String> giverNames;
    private Map<String, String> recipientNames;
    private Map<String, List<FeedbackResponseComment>> feedbackResponseCommentsLists;
    private Map<FeedbackQuestionDetails, String> responseEntryAnswerHtmls;
    private InstructorAttributes currentInstructor;
    private boolean instructorAllowedToSubmit;
    private String instructorEmail;

    public InstructorFeedbackResponseComment(Map<String, FeedbackSessionResultsBundle> feedbackResultBundles,
                                             InstructorAttributes currentInstructor, String instructorEmail) {
        this.feedbackResultBundles = feedbackResultBundles;
        this.currentInstructor = currentInstructor;
        this.giverNames = new HashMap<String, String>();
        this.recipientNames = new HashMap<String, String>();
        this.feedbackResponseCommentsLists = new HashMap<String, List<FeedbackResponseComment>>();
        this.responseEntryAnswerHtmls = new HashMap<FeedbackQuestionDetails, String>();
        this.instructorEmail = instructorEmail;

        initializeValues();
    }

    // Initializes giverNames
    // Initializes recipientNames
    // Initializes responseEntryAnswerHtml
    // Initializes instructorAllowedToSubmit
    // Initializes feedbackResponseCommentsLists
    private void initializeValues() {
        for (String bundleKey : feedbackResultBundles.keySet()) {
            FeedbackSessionResultsBundle bundle = feedbackResultBundles.get(bundleKey);
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntriesMap = 
                bundle.getQuestionResponseMap();

            for (FeedbackQuestionAttributes attributeKey : responseEntriesMap.keySet()) {
                List<FeedbackResponseAttributes> responseEntries = responseEntriesMap.get(attributeKey);
                FeedbackQuestionAttributes question = bundle.questions.get(attributeKey.getId());
                FeedbackQuestionDetails questionDetails = question.getQuestionDetails();

                for (FeedbackResponseAttributes responseEntry : responseEntries) {
                    // giverNames and recipientNames are initialized here
                    String giverEmail = responseEntry.giverEmail;
                    String giverName = bundle.emailNameTable.get(giverEmail);
                    String giverTeamName = bundle.emailTeamNameTable.get(giverEmail);

                    String recipientEmail = responseEntry.recipientEmail;
                    String recipientName = bundle.emailNameTable.get(recipientEmail);
                    String recipientTeamName = bundle.emailTeamNameTable.get(recipientEmail);

                    String appendedGiverName = bundle.appendTeamNameToName(giverName, giverTeamName);
                    String appendedRecipientName = bundle.appendTeamNameToName(
                            recipientName, recipientTeamName);

                    giverNames.put(giverEmail, appendedGiverName);
                    recipientNames.put(recipientEmail, appendedRecipientName);

                    // responseEntryAnswerHtml is initialized here
                    String responseEntryAnswerHtml = 
                            responseEntry.getResponseDetails().getAnswerHtml(questionDetails);

                    responseEntryAnswerHtmls.put(questionDetails, responseEntryAnswerHtml);

                    // instructorAllowedToSubmit is initialized here
                    if (currentInstructor == null
                        || !currentInstructor.isAllowedForPrivilege(
                               responseEntry.giverSection, responseEntry.feedbackSessionName,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)
                        || !currentInstructor.isAllowedForPrivilege(
                               responseEntry.recipientSection, responseEntry.feedbackSessionName,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {
                        instructorAllowedToSubmit = false;
                    } else {
                        instructorAllowedToSubmit = true;
                    }

                    // feedbackResponseCommentsLists is initialized here
                    Map<String, List<FeedbackResponseCommentAttributes>> responseComments =
                        bundle.getResponseComments();

                    List<FeedbackResponseCommentAttributes> feedbackResponseCommentsList =
                        responseComments.get(responseEntry.getId());

                    List<FeedbackResponseComment> frcList = new ArrayList<FeedbackResponseComment>();

                    for (FeedbackResponseCommentAttributes frca : feedbackResponseCommentsList) {
                        FeedbackResponseComment frc = new FeedbackResponseComment(
                            frca, frca.giverEmail, instructorEmail, bundle.feedbackSession);

                        frcList.add(frc);
                    }

                    feedbackResponseCommentsLists.put(responseEntry.getId(), frcList);
                }
            }
        }
    }

    public Map<String, String> getGiverNames() {
        return giverNames;
    }

    public Map<String, String> getRecipientNames() {
        return recipientNames;
    }

    public Map<FeedbackQuestionDetails, String> getResponseEntryAnswerHtmls() {
        return responseEntryAnswerHtmls;
    }

    public boolean isInstructorAllowedToSubmit() {
        return instructorAllowedToSubmit;
    }

    public Map<String, List<FeedbackResponseComment>> getFeedbackResponseCommentsList() {
        return feedbackResponseCommentsLists;
    }
}