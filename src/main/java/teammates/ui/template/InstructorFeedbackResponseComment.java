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
    private Map<FeedbackResponseAttributes, String> giverNames;
    private Map<FeedbackResponseAttributes, String> recipientNames;
    private Map<String, List<FeedbackResponseComment>> feedbackResponseCommentsLists;
    private Map<FeedbackQuestionDetails, String> responseEntryAnswerHtmls;
    private Map<FeedbackQuestionAttributes, String> showCommentToStrings;
    private Map<FeedbackQuestionAttributes, String> showGiverNameToStrings;
    private Map<FeedbackQuestionAttributes, Boolean> responseVisibleToGiver;
    private Map<FeedbackQuestionAttributes, Boolean> responseVisibleToRecipient;
    private Map<FeedbackQuestionAttributes, Boolean> responseVisibleToGiverTeam;
    private Map<FeedbackQuestionAttributes, Boolean> responseVisibleToRecipientTeam;
    private Map<FeedbackQuestionAttributes, Boolean> responseVisibleToStudents;
    private Map<FeedbackQuestionAttributes, Boolean> responseVisibleToInstructors;
    private boolean instructorAllowedToSubmit;
    private InstructorAttributes currentInstructor;
    private String instructorEmail;

    public InstructorFeedbackResponseComment(Map<String, FeedbackSessionResultsBundle> feedbackResultBundles,
                                             InstructorAttributes currentInstructor, String instructorEmail,
                                             InstructorFeedbackResponseCommentsLoadPageData ifrclpd) {
        this.feedbackResultBundles = feedbackResultBundles;
        this.currentInstructor = currentInstructor;
        this.giverNames = new HashMap<FeedbackResponseAttributes, String>();
        this.recipientNames = new HashMap<FeedbackResponseAttributes, String>();
        this.feedbackResponseCommentsLists = new HashMap<String, List<FeedbackResponseComment>>();
        this.responseEntryAnswerHtmls = new HashMap<FeedbackQuestionDetails, String>();
        this.showCommentToStrings = new HashMap<FeedbackQuestionAttributes, String>();
        this.showGiverNameToStrings = new HashMap<FeedbackQuestionAttributes, String>();
        this.responseVisibleToRecipient = new HashMap<FeedbackQuestionAttributes, Boolean>();
        this.responseVisibleToGiverTeam = new HashMap<FeedbackQuestionAttributes, Boolean>();
        this.responseVisibleToRecipientTeam = new HashMap<FeedbackQuestionAttributes, Boolean>();
        this.responseVisibleToStudents = new HashMap<FeedbackQuestionAttributes, Boolean>();
        this.responseVisibleToInstructors = new HashMap<FeedbackQuestionAttributes, Boolean>();
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

            for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries 
                 : bundle.getQuestionResponseMap().entrySet()) {
                FeedbackQuestionAttributes question = bundle.questions.get(responseEntries.getKey().getId());
                FeedbackQuestionDetails questionDetails = question.getQuestionDetails();

                for (FeedbackResponseAttributes responseEntry : responseEntries.getValue()) {
                    // giverNames and recipientNames are initialized here
                    String giverName = bundle.getGiverNameForResponse(responseEntry);
                    String giverTeamName = bundle.getTeamNameForEmail(responseEntry.giverEmail);

                    String recipientName = bundle.getRecipientNameForResponse(responseEntry);
                    String recipientTeamName = bundle.getTeamNameForEmail(responseEntry.recipientEmail);

                    String appendedGiverName = bundle.appendTeamNameToName(giverName, giverTeamName);
                    String appendedRecipientName = bundle.appendTeamNameToName(
                            recipientName, recipientTeamName);

                    giverNames.put(responseEntry, appendedGiverName);
                    recipientNames.put(responseEntry, appendedRecipientName);

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

                    List<FeedbackResponseCommentAttributes> feedbackResponseCommentsList =
                            bundle.responseComments.get(responseEntry.getId());

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

                        List<FeedbackParticipantType> showCommentTo = frca.showCommentTo;
                        List<FeedbackParticipantType> showGiverNameTo = frca.showGiverNameTo;

                        String showCommentToString = joinParticipantTypes(showCommentTo, ",");
                        String showGiverNameToString = joinParticipantTypes(showGiverNameTo, ",");

                        showCommentToStrings.put(question, showCommentToString);
                        showCommentToStrings.put(question, showGiverNameToString);

                        boolean isResponseVisibleToGiver =
                            question.isResponseVisibleTo(FeedbackParticipantType.GIVER);
                        boolean isResponseVisibleToRecipient =
                            question.recipientType != FeedbackParticipantType.SELF
                            && question.recipientType != FeedbackParticipantType.NONE
                            && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER);
                        boolean isResponseVisibleToGiverTeam =
                            question.giverType != FeedbackParticipantType.INSTRUCTORS
                            && question.giverType != FeedbackParticipantType.SELF
                            && question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS);
                        boolean isResponseVisibleToRecipientTeam =
                            question.recipientType != FeedbackParticipantType.INSTRUCTORS
                            && question.recipientType != FeedbackParticipantType.SELF
                            && question.recipientType != FeedbackParticipantType.NONE
                            && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
                        boolean isResponseVisibleToStudents =
                            question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS);
                        boolean isResponseVisibleToInstructors =
                            question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS);

                        responseVisibleToGiver.put(question, isResponseVisibleToGiver);
                        responseVisibleToRecipient.put(question, isResponseVisibleToRecipient);
                        responseVisibleToGiverTeam.put(question, isResponseVisibleToGiverTeam);
                        responseVisibleToRecipientTeam.put(question, isResponseVisibleToRecipientTeam);
                        responseVisibleToStudents.put(question, isResponseVisibleToStudents);
                        responseVisibleToInstructors.put(question, isResponseVisibleToInstructors);

                        FeedbackResponseComment frc = new FeedbackResponseComment(
                            frca, frca.giverEmail, instructorEmail, bundle.feedbackSession, question,
                            whoCanSeeComment, showCommentToString, showGiverNameToString,
                            allowedToEditAndDeleteComment, isResponseVisibleToRecipient,
                            isResponseVisibleToGiverTeam, isResponseVisibleToRecipientTeam,
                            isResponseVisibleToStudents, isResponseVisibleToInstructors);

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

    public Map<FeedbackQuestionAttributes, String> getShowCommentToStrings() {
        return showCommentToStrings;
    }

    public Map<FeedbackQuestionAttributes, String> getShowGiverNameToStrings() {
        return showGiverNameToStrings;
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

    public boolean isInstructorAllowedToSubmit() {
        return instructorAllowedToSubmit;
    }
}
