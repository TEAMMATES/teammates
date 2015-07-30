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
    private Map<FeedbackQuestionAttributes, List<InstructorFeedbackResponseComment>> questionCommentsMap;
    
    public InstructorFeedbackResponseCommentsLoadPageData(AccountAttributes account) {
        super(account);
    }
    
    // Initializes giverNames
    // Initializes recipientNames
    // Initializes responseEntryAnswerHtml
    // Initializes instructorAllowedToSubmit
    // Initializes feedbackResponseCommentsLists
    public void init() {
        // no visible questions / responses with comments
        if (feedbackResultBundle == null) {
            return;
        }
        
        questionCommentsMap = new HashMap<>();
        
        FeedbackSessionResultsBundle bundle = feedbackResultBundle;
        
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries 
            : bundle.getQuestionResponseMap().entrySet()) {
           FeedbackQuestionAttributes question = bundle.questions.get(responseEntries.getKey().getId());
           FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
           
           Map<FeedbackParticipantType, Boolean> responseVisibilityMap = getResponseVisibilityMap(question);
           
           List<InstructorFeedbackResponseComment> responseCommentList = new ArrayList<>();

           for (FeedbackResponseAttributes responseEntry : responseEntries.getValue()) {
               // giverNames and recipientNames are initialized here
               String giverName = bundle.getGiverNameForResponse(responseEntry);
               String giverTeamName = bundle.getTeamNameForEmail(responseEntry.giverEmail);
               giverName = bundle.appendTeamNameToName(giverName, giverTeamName);

               String recipientName = bundle.getRecipientNameForResponse(responseEntry);
               String recipientTeamName = bundle.getTeamNameForEmail(responseEntry.recipientEmail);
               recipientName = bundle.appendTeamNameToName(recipientName, recipientTeamName);
               
               String responseEntryAnswerHtml = 
                       responseEntry.getResponseDetails().getAnswerHtml(questionDetails);
               
               boolean instructorAllowedToAddComment =
                       currentInstructor != null
                       && currentInstructor.isAllowedForPrivilege(
                               responseEntry.giverSection, responseEntry.feedbackSessionName,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)
                       && currentInstructor.isAllowedForPrivilege(
                               responseEntry.recipientSection, responseEntry.feedbackSessionName,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
               
               List<FeedbackResponseCommentAttributes> feedbackResponseComments =
                       bundle.responseComments.get(responseEntry.getId());

               List<FeedbackResponseComment> frcList = new ArrayList<>();
               for (FeedbackResponseCommentAttributes frca : feedbackResponseComments) {
                   String whoCanSeeComment = getTypeOfPeopleCanViewComment(frca, question);
                   
                   boolean isInstructorGiver = frca.giverEmail.equals(instructorEmail);
                   boolean isInstructorAllowedToModify =
                           currentInstructor != null
                           && currentInstructor.isAllowedForPrivilege(
                               responseEntry.giverSection, responseEntry.feedbackSessionName,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                           && currentInstructor.isAllowedForPrivilege(
                               responseEntry.recipientSection, responseEntry.feedbackSessionName,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
                   boolean allowedToEditAndDeleteComment = isInstructorGiver || isInstructorAllowedToModify;
                   
                   String showCommentToString = getResponseCommentVisibilityString(frca, question);
                   String showGiverNameToString = getResponseCommentGiverNameVisibilityString(frca, question);

                   boolean isResponseVisibleToRecipient = responseVisibilityMap.get(FeedbackParticipantType.RECEIVER);
                   boolean isResponseVisibleToGiverTeam = responseVisibilityMap.get(FeedbackParticipantType.OWN_TEAM_MEMBERS);
                   boolean isResponseVisibleToRecipientTeam = responseVisibilityMap.get(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
                   boolean isResponseVisibleToStudents = responseVisibilityMap.get(FeedbackParticipantType.STUDENTS);
                   boolean isResponseVisibleToInstructors = responseVisibilityMap.get(FeedbackParticipantType.INSTRUCTORS);
                   
                   boolean editDeleteEnabledOnlyOnHover = true;
                   
                   FeedbackResponseComment frc = new FeedbackResponseComment(
                       frca, frca.giverEmail, giverName, recipientName, instructorEmail,
                       bundle.feedbackSession, question, whoCanSeeComment, showCommentToString,
                       showGiverNameToString, allowedToEditAndDeleteComment, editDeleteEnabledOnlyOnHover,
                       isResponseVisibleToRecipient, isResponseVisibleToGiverTeam,
                       isResponseVisibleToRecipientTeam, isResponseVisibleToStudents,
                       isResponseVisibleToInstructors);

                   frcList.add(frc);
               }
               
               FeedbackResponseComment feedbackResponseCommentAdd =
                       setUpFeedbackResponseCommentAdd(question, responseEntry, responseVisibilityMap, giverName, recipientName);
               
               responseCommentList.add(new InstructorFeedbackResponseComment(
                       giverName, recipientName, frcList, responseEntryAnswerHtml,
                       instructorAllowedToAddComment, feedbackResponseCommentAdd));
           }
           
           questionCommentsMap.put(question, responseCommentList);
       }
       
       
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
            String giverName, String recipientName) {
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
        return new FeedbackResponseComment(frca, giverName, recipientName,
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
    
    public Map<FeedbackQuestionAttributes, List<InstructorFeedbackResponseComment>> getQuestionCommentsMap() {
        return questionCommentsMap;
    }
    
    public int getFeedbackSessionIndex() {
        return feedbackSessionIndex;
    }
}
