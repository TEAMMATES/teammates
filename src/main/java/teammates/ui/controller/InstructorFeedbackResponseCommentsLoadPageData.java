package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.template.FeedbackResponseComment;
import teammates.ui.template.InstructorFeedbackResponseComment;

public class InstructorFeedbackResponseCommentsLoadPageData extends PageData {

    private InstructorAttributes instructor;
    private int numberOfPendingComments = 0;
    private int feedbackSessionIndex = 0;
    private Map<FeedbackQuestionAttributes, List<InstructorFeedbackResponseComment>> questionCommentsMap;
    
    public InstructorFeedbackResponseCommentsLoadPageData(AccountAttributes account, int feedbackSessionIndex,
            int numberOfPendingComments, InstructorAttributes currentInstructor, FeedbackSessionResultsBundle bundle) {
        super(account);
        this.feedbackSessionIndex = feedbackSessionIndex;
        this.numberOfPendingComments = numberOfPendingComments;
        this.instructor = currentInstructor;
        init(bundle);
    }
    
    public void init(FeedbackSessionResultsBundle bundle) {
        // no visible questions / responses with comments
        if (bundle == null) {
            return;
        }
        
        questionCommentsMap = new LinkedHashMap<>();
        
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries 
                : bundle.getQuestionResponseMap().entrySet()) {
           FeedbackQuestionAttributes question = bundle.questions.get(responseEntries.getKey().getId());
           Map<FeedbackParticipantType, Boolean> responseVisibilityMap = getResponseVisibilityMap(question);
           
           List<InstructorFeedbackResponseComment> responseCommentList = buildInstructorFeedbackResponseComments(
                   responseEntries, bundle, question, responseVisibilityMap);

           questionCommentsMap.put(question, responseCommentList);
       }
    }
    
    private List<InstructorFeedbackResponseComment> buildInstructorFeedbackResponseComments(
            Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries,
            FeedbackSessionResultsBundle bundle, FeedbackQuestionAttributes question,
            Map<FeedbackParticipantType, Boolean> responseVisibilityMap) {
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
                    responseEntry.getResponseDetails().getAnswerHtml(question.getQuestionDetails());
            
            boolean instructorAllowedToAddComment = isInstructorAllowedForSectionalPrivilege(
                    responseEntry.giverSection, responseEntry.recipientSection, responseEntry.feedbackSessionName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            
            List<FeedbackResponseCommentAttributes> feedbackResponseCommentsAttributes =
                    bundle.responseComments.get(responseEntry.getId());

            List<FeedbackResponseComment> frcList = buildFeedbackResponseComments(
                    feedbackResponseCommentsAttributes, question, responseEntry, giverName, recipientName,
                    responseVisibilityMap, bundle.feedbackSession);
            
            FeedbackResponseComment feedbackResponseCommentAdd = buildFeedbackResponseCommentAdd(
                    question, responseEntry, responseVisibilityMap, giverName, recipientName);
            
            responseCommentList.add(new InstructorFeedbackResponseComment(
                    giverName, recipientName, frcList, responseEntryAnswerHtml,
                    instructorAllowedToAddComment, feedbackResponseCommentAdd));
        }
        
        return responseCommentList;
    }

    private List<FeedbackResponseComment> buildFeedbackResponseComments(
            List<FeedbackResponseCommentAttributes> feedbackResponseCommentsAttributes,
            FeedbackQuestionAttributes question, FeedbackResponseAttributes responseEntry,
            String giverName, String recipientName, Map<FeedbackParticipantType, Boolean> responseVisibilityMap,
            FeedbackSessionAttributes feedbackSession) {
        List<FeedbackResponseComment> comments = new ArrayList<>();
        
        for (FeedbackResponseCommentAttributes frca : feedbackResponseCommentsAttributes) {
            boolean isInstructorGiver = frca.giverEmail.equals(instructor.email);
            boolean isInstructorAllowedToModify = isInstructorAllowedForSectionalPrivilege(
                    responseEntry.giverSection, responseEntry.recipientSection, responseEntry.feedbackSessionName,
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
            
            String whoCanSeeComment = null;
            boolean isVisibilityIconShown = false;
            boolean isNotificationIconShown = false;
            if (feedbackSession.isPublished()) {
                boolean responseCommentPublicToRecipient = !frca.showCommentTo.isEmpty();
                isVisibilityIconShown = responseCommentPublicToRecipient;
                
                if (isVisibilityIconShown) {
                    whoCanSeeComment = getTypeOfPeopleCanViewComment(frca, question);
                }
                
                isNotificationIconShown = frca.sendingState == CommentSendingState.PENDING;
            }
            
            String extraClass = getExtraClass(frca.giverEmail, instructor.email, isVisibilityIconShown);
            
            FeedbackResponseComment frc = new FeedbackResponseComment(
                frca, frca.giverEmail, giverName, recipientName, extraClass,
                isVisibilityIconShown, isNotificationIconShown, whoCanSeeComment,
                showCommentToString, showGiverNameToString,
                allowedToEditAndDeleteComment, editDeleteEnabledOnlyOnHover,
                isResponseVisibleToRecipient, isResponseVisibleToGiverTeam,
                isResponseVisibleToRecipientTeam, isResponseVisibleToStudents,
                isResponseVisibleToInstructors);
            
            comments.add(frc);
        }
        
        return comments;
    }
    
    private boolean isInstructorAllowedForSectionalPrivilege(String giverSection, String recipientSection,
            String feedbackSessionName, String privilege) {
        return instructor != null
               && instructor.isAllowedForPrivilege(
                   giverSection, feedbackSessionName, privilege)
               && instructor.isAllowedForPrivilege(
                   recipientSection, feedbackSessionName, privilege);
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

    private FeedbackResponseComment buildFeedbackResponseCommentAdd(FeedbackQuestionAttributes question,
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
    


    private String getExtraClass(String giverEmail, String instructorEmail, boolean isPublic) {
        String extraClass = "";
        
        extraClass += " giver_display-by-";
        extraClass += giverEmail.equals(instructorEmail)? "you" : "others";
        
        extraClass += " status_display-";
        extraClass += isPublic ? "public" : "private";

        return extraClass;
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
