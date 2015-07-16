package teammates.ui.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackResponseCommentsLoadPageData;
import teammates.ui.template.FeedbackResponseComment;

public class InstructorFeedbackResponseComment {
    private InstructorFeedbackResponseCommentsLoadPageData ifrclpd;
    private Map<String, FeedbackSessionResultsBundle> feedbackResultBundles;
    private Map<String, String> giverNames;
    private Map<String, String> recipientNames;
    private Map<String, List<FeedbackResponseComment>> feedbackResponseCommentsLists;
    private Map<FeedbackQuestionDetails, String> responseEntryAnswerHtmls;
    private InstructorAttributes currentInstructor;
    private boolean instructorAllowedToSubmit;
    private String instructorEmail;

    public InstructorFeedbackResponseComment(Map<String, FeedbackSessionResultsBundle> feedbackResultBundles,
                                             InstructorAttributes currentInstructor, String instructorEmail,
                                             InstructorFeedbackResponseCommentsLoadPageData ifrclpd) {
        this.feedbackResultBundles = feedbackResultBundles;
        this.currentInstructor = currentInstructor;
        this.giverNames = new HashMap<String, String>();
        this.recipientNames = new HashMap<String, String>();
        this.feedbackResponseCommentsLists = new HashMap<String, List<FeedbackResponseComment>>();
        this.responseEntryAnswerHtmls = new HashMap<FeedbackQuestionDetails, String>();
        this.instructorEmail = instructorEmail;
        this.ifrclpd = ifrclpd;

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
                        String whoCanSeeComment = ifrclpd.getTypeOfPeopleCanViewComment(frca, question);

                        boolean allowedToEditAndDeleteComment =
                                    frca.giverEmail.equals(instructorEmail)
                                    || (currentInstructor != null
                                        && currentInstructor.isAllowedForPrivilege(
                                            responseEntry.giverSection, responseEntry.feedbackSessionName,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                                        && currentInstructor.isAllowedForPrivilege(
                                            responseEntry.recipientSection, responseEntry.feedbackSessionName,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));

                        String showCommentToString = joinParticipantTypes(frca.showCommentTo, ",");
                        String showGiverNameToString = joinParticipantTypes(frca.showGiverNameTo, ",");

                        boolean responseVisibleToRecipient =
                                    question.recipientType != FeedbackParticipantType.SELF
                                    && question.recipientType != FeedbackParticipantType.NONE
                                    && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER);
                        boolean responseVisibleToGiverTeam =
                                    question.giverType != FeedbackParticipantType.INSTRUCTORS
                                    && question.giverType != FeedbackParticipantType.SELF
                                    && question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS);
                        boolean responseVisibleToRecipientTeam =
                                    question.recipientType != FeedbackParticipantType.INSTRUCTORS
                                    && question.recipientType != FeedbackParticipantType.SELF
                                    && question.recipientType != FeedbackParticipantType.NONE
                                    && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
                        boolean responseVisibleToStudents = 
                                    question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS);
                        boolean responseVisibleToInstructors = 
                                    question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS);

                        FeedbackResponseComment frc = new FeedbackResponseComment(
                            frca, frca.giverEmail, instructorEmail, bundle.feedbackSession, question,
                            whoCanSeeComment, showCommentToString, showGiverNameToString,
                            allowedToEditAndDeleteComment, responseVisibleToRecipient,
                            responseVisibleToGiverTeam, responseVisibleToRecipientTeam,
                            responseVisibleToStudents, responseVisibleToInstructors);

                        frcList.add(frc);
                    }

                    feedbackResponseCommentsLists.put(responseEntry.getId(), frcList);
                }
            }
        }
    }

    private String joinParticipantTypes(List<FeedbackParticipantType> participants, String joiner) {
        if (participants.isEmpty()) {
            return "";
        } else {
            String result = "";
            for (FeedbackParticipantType fpt: participants) {
                result += fpt + joiner;
            }
            return result.substring(0, result.length() - joiner.length());
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