package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.template.FeedbackResponseComment;
import teammates.ui.template.InstructorFeedbackResponseComment;

public class InstructorFeedbackResponseCommentsLoadPageData extends PageData {

    public FeedbackSessionResultsBundle feedbackResultBundle;
    public InstructorAttributes currentInstructor = null;
    public String instructorEmail = "";
    public CourseRoster roster = null;
    public int numberOfPendingComments = 0;
    public int feedbackSessionIndex = 0;
    private InstructorFeedbackResponseComment instructorFeedbackResponseComment;
    
    public InstructorFeedbackResponseCommentsLoadPageData(AccountAttributes account) {
        super(account);
    }
    
    // Initializes giverNames
    // Initializes recipientNames
    // Initializes responseEntryAnswerHtml
    // Initializes instructorAllowedToSubmit
    // Initializes feedbackResponseCommentsLists
    public void init() {
        FeedbackSessionResultsBundle bundle = feedbackResultBundle;
        HashMap<FeedbackResponseAttributes, String> giverNames = new HashMap<>();
        HashMap<FeedbackResponseAttributes, String> recipientNames = new HashMap<>();
        HashMap<FeedbackResponseAttributes, Boolean> instructorAllowedToSubmitMap = new HashMap<>();
        HashMap<String, List<FeedbackResponseComment>> feedbackResponseCommentsLists = new HashMap<>();
        HashMap<FeedbackQuestionDetails, String> responseEntryAnswerHtmls = new HashMap<>();
        HashMap<FeedbackQuestionAttributes, String> showResponseCommentToStrings = new HashMap<>();
        HashMap<FeedbackQuestionAttributes, String> showResponseGiverNameToStrings = new HashMap<>();
        HashMap<FeedbackQuestionAttributes, Boolean> responseVisibleToGiver = new HashMap<>();
        HashMap<FeedbackQuestionAttributes, Boolean> responseVisibleToRecipient = new HashMap<>();
        HashMap<FeedbackQuestionAttributes, Boolean> responseVisibleToGiverTeam = new HashMap<>();
        HashMap<FeedbackQuestionAttributes, Boolean> responseVisibleToRecipientTeam = new HashMap<>();
        HashMap<FeedbackQuestionAttributes, Boolean> responseVisibleToStudents = new HashMap<>();
        HashMap<FeedbackQuestionAttributes, Boolean> responseVisibleToInstructors = new HashMap<>();
        HashMap<FeedbackQuestionAttributes, FeedbackResponseComment> feedbackResponseCommentAddMap = new HashMap<>();
        
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries 
             : bundle.getQuestionResponseMap().entrySet()) {
            FeedbackQuestionAttributes question = bundle.questions.get(responseEntries.getKey().getId());
            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
            
            Map<FeedbackParticipantType, Boolean> responseVisibilityMap = getResponseVisibilityMap(question);

            for (FeedbackResponseAttributes responseEntry : responseEntries.getValue()) {
                // giverNames and recipientNames are initialized here
                String giverName = bundle.getGiverNameForResponse(responseEntry);
                String giverTeamName = bundle.getTeamNameForEmail(responseEntry.giverEmail);

                String recipientName = bundle.getRecipientNameForResponse(responseEntry);
                String recipientTeamName = bundle.getTeamNameForEmail(responseEntry.recipientEmail);

                String appendedGiverName = bundle.appendTeamNameToName(giverName, giverTeamName);
                String appendedRecipientName = bundle.appendTeamNameToName(recipientName, recipientTeamName);

                giverNames.put(responseEntry, appendedGiverName);
                recipientNames.put(responseEntry, appendedRecipientName);

                // responseEntryAnswerHtml is initialized here
                String responseEntryAnswerHtml = 
                        responseEntry.getResponseDetails().getAnswerHtml(questionDetails);

                responseEntryAnswerHtmls.put(questionDetails, responseEntryAnswerHtml);
                
                boolean instructorAllowedToSubmit = !(currentInstructor == null
                    || !currentInstructor.isAllowedForPrivilege(
                           responseEntry.giverSection, responseEntry.feedbackSessionName,
                           Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)
                    || !currentInstructor.isAllowedForPrivilege(
                           responseEntry.recipientSection, responseEntry.feedbackSessionName,
                           Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
                instructorAllowedToSubmitMap.put(responseEntry, instructorAllowedToSubmit);
                
                // feedbackResponseCommentsLists is initialized here

                List<FeedbackResponseCommentAttributes> feedbackResponseCommentsList =
                        bundle.responseComments.get(responseEntry.getId());

                List<FeedbackResponseComment> frcList = new ArrayList<FeedbackResponseComment>();

                for (FeedbackResponseCommentAttributes frca : feedbackResponseCommentsList) {
                    String whoCanSeeComment = getTypeOfPeopleCanViewComment(frca, question);

                    boolean allowedToEditAndDeleteComment =
                                frca.giverEmail.equals(instructorEmail)
                                || (currentInstructor != null
                                    && currentInstructor.isAllowedForPrivilege(
                                        responseEntry.giverSection, responseEntry.feedbackSessionName,
                                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                                    && currentInstructor.isAllowedForPrivilege(
                                        responseEntry.recipientSection, responseEntry.feedbackSessionName,
                                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
                    
                    String showCommentToString = getResponseCommentVisibilityString(frca, question);
                    String showGiverNameToString = getResponseCommentGiverNameVisibilityString(frca, question);

                    showResponseCommentToStrings.put(question, 
                                                     getResponseCommentVisibilityString(question));
                    showResponseGiverNameToStrings.put(question, 
                                                       getResponseCommentGiverNameVisibilityString(question));

                    boolean isResponseVisibleToGiver = responseVisibilityMap.get(FeedbackParticipantType.GIVER);
                    boolean isResponseVisibleToRecipient = responseVisibilityMap.get(FeedbackParticipantType.RECEIVER);
                    boolean isResponseVisibleToGiverTeam = responseVisibilityMap.get(FeedbackParticipantType.OWN_TEAM_MEMBERS);
                    boolean isResponseVisibleToRecipientTeam = responseVisibilityMap.get(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
                    boolean isResponseVisibleToStudents = responseVisibilityMap.get(FeedbackParticipantType.STUDENTS);
                    boolean isResponseVisibleToInstructors = responseVisibilityMap.get(FeedbackParticipantType.INSTRUCTORS);
                    
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
                
                FeedbackResponseComment feedbackResponseCommentAdd =
                        setUpFeedbackResponseCommentAdd(question, responseEntry, responseVisibilityMap, giverNames, recipientNames);
                feedbackResponseCommentAddMap.put(question, feedbackResponseCommentAdd);
                
            }
        }
        
        instructorFeedbackResponseComment = new InstructorFeedbackResponseComment(giverNames, recipientNames,
                feedbackResponseCommentsLists, responseEntryAnswerHtmls, showResponseCommentToStrings,
                showResponseGiverNameToStrings, responseVisibleToGiver, responseVisibleToRecipient,
                responseVisibleToGiverTeam, responseVisibleToRecipientTeam, responseVisibleToStudents,
                responseVisibleToInstructors, instructorAllowedToSubmitMap, feedbackResponseCommentAddMap);
    }
    
    private Map<FeedbackParticipantType, Boolean> getResponseVisibilityMap(FeedbackQuestionAttributes question) {
        Map<FeedbackParticipantType, Boolean> responseVisibilityMap = new HashMap<>();
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
        
        responseVisibilityMap.put(FeedbackParticipantType.GIVER, isResponseVisibleToGiver);
        responseVisibilityMap.put(FeedbackParticipantType.RECEIVER, isResponseVisibleToRecipient);
        responseVisibilityMap.put(FeedbackParticipantType.OWN_TEAM_MEMBERS, isResponseVisibleToGiverTeam);
        responseVisibilityMap.put(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS, isResponseVisibleToRecipientTeam);
        responseVisibilityMap.put(FeedbackParticipantType.STUDENTS, isResponseVisibleToStudents);
        responseVisibilityMap.put(FeedbackParticipantType.INSTRUCTORS, isResponseVisibleToInstructors);
        
        return responseVisibilityMap;
    }

    private FeedbackResponseComment setUpFeedbackResponseCommentAdd(FeedbackQuestionAttributes question,
            FeedbackResponseAttributes response, Map<FeedbackParticipantType, Boolean> responseVisibilityMap,
            HashMap<FeedbackResponseAttributes,String> giverNames, HashMap<FeedbackResponseAttributes,String> recipientNames) {
        FeedbackParticipantType[] relevantTypes = {
                FeedbackParticipantType.GIVER,
                FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS,
                FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.INSTRUCTORS
        };
        
        List<FeedbackParticipantType> showCommentTo = new ArrayList<>();
        List<FeedbackParticipantType> showGiverNameTo = new ArrayList<>();
        for (FeedbackParticipantType type : relevantTypes) {
            if (isResponseCommentVisibleTo(question, type)) {
                showCommentTo.add(type);
            }
            if (isResponseCommentGiverNameVisibleTo(question, type)) {
                showGiverNameTo.add(type);
            }
        }
        
        FeedbackResponseCommentAttributes frca = new FeedbackResponseCommentAttributes(
                    question.courseId, question.feedbackSessionName, question.getFeedbackQuestionId(), response.getId());
        return new FeedbackResponseComment(frca, giverNames.get(response), recipientNames.get(response),
                getResponseCommentVisibilityString(question), getResponseCommentGiverNameVisibilityString(question),
                responseVisibilityMap.get(FeedbackParticipantType.RECEIVER),
                responseVisibilityMap.get(FeedbackParticipantType.OWN_TEAM_MEMBERS),
                responseVisibilityMap.get(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS),
                responseVisibilityMap.get(FeedbackParticipantType.STUDENTS),
                responseVisibilityMap.get(FeedbackParticipantType.INSTRUCTORS),
                showCommentTo, showGiverNameTo, true);
    }

    public FeedbackSessionResultsBundle getFeedbackResultsBundle() {
        return feedbackResultBundle;
    }

    public int getNumberOfPendingComments() {
        return numberOfPendingComments;
    }
    
    public InstructorFeedbackResponseComment getInstructorFeedbackResponseComment() {
        return instructorFeedbackResponseComment;
    }

    public void setInstructorFeedbackResponseComment(
            InstructorFeedbackResponseComment instructorFeedbackResponseComment) {
        this.instructorFeedbackResponseComment = instructorFeedbackResponseComment;
    }
    
    public int getFeedbackSessionIndex() {
        return feedbackSessionIndex;
    }
}
