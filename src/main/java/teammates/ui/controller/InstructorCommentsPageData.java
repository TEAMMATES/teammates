package teammates.ui.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.logic.api.Logic;

public class InstructorCommentsPageData extends PageData {
    public static final String COMMENT_GIVER_NAME_THAT_COMES_FIRST = "0you";
    
    public Boolean isViewingDraft;
    public Boolean isDisplayArchive;
    public String courseId;
    public String courseName;
    public List<String> coursePaginationList;
    public Map<String, List<CommentAttributes>> comments;
    // TODO: remove this field
    public String instructorEmail;
    public InstructorAttributes currentInstructor;
    public CourseRoster roster;
    public Map<String, FeedbackSessionResultsBundle> feedbackResultBundles;
    public String previousPageLink;
    public String nextPageLink;
    public int numberOfPendingComments = 0;
    
    public InstructorCommentsPageData(AccountAttributes account) {
        super(account);
    }
    
    public String removeBracketsForArrayString(String arrayString){
        return arrayString.substring(1, arrayString.length() - 1).trim();
    }
    
    public String getGiverName(String giverEmail){
        InstructorAttributes instructor = roster.getInstructorForEmail(giverEmail);
        String giverDisplay = giverEmail;
        if(giverEmail.equals(COMMENT_GIVER_NAME_THAT_COMES_FIRST)){
            giverDisplay = "You";
        } else if(instructor != null){
            String title = instructor.displayedName;
            giverDisplay = title + " " + instructor.name;
        }
        return giverDisplay;
    }
    
    public String getRecipientNames(Set<String> recipients){
        StringBuilder namesStringBuilder = new StringBuilder();
        int i = 0;
        for(String recipient : recipients){
            if(i == recipients.size() - 1 && recipients.size() > 1){
                namesStringBuilder.append("and ");
            }
            StudentAttributes student = roster.getStudentForEmail(recipient);
            if(courseId.equals(recipient)){ 
                namesStringBuilder.append("All students in this course, ");
            } else if(student != null){
                if(recipients.size() == 1){
                    namesStringBuilder.append(student.name 
                            + " (" + student.team + ", <a href=\"mailto:" + student.email + "\">" + student.email + "</a>), ");
                } else {
                    namesStringBuilder.append(student.name + ", ");
                }
            } else {
                namesStringBuilder.append(recipient + ", ");
            }
            i++;
        }
        String namesString = namesStringBuilder.toString();
        return removeEndComma(namesString);
    }
    
    public boolean isResponseCommentVisibleTo(FeedbackQuestionAttributes qn,
            FeedbackParticipantType viewerType){
        if(viewerType == FeedbackParticipantType.GIVER) {
            return true;
        } else {
            return qn.isResponseVisibleTo(viewerType);
        }
    }
    
    public boolean isResponseCommentGiverNameVisibleTo(FeedbackQuestionAttributes qn,
            FeedbackParticipantType viewerType){
        return true;
    }
    
    public boolean isResponseCommentVisibleTo(FeedbackResponseCommentAttributes frComment, FeedbackQuestionAttributes qn,
            FeedbackParticipantType viewerType){
        if(frComment.isVisibilityFollowingFeedbackQuestion
                && viewerType == FeedbackParticipantType.GIVER) {
            return true;
        } else if(frComment.isVisibilityFollowingFeedbackQuestion){
            return qn.isResponseVisibleTo(viewerType);
        } else {
            return frComment.isVisibleTo(viewerType);
        }
    }
    
    public boolean isResponseCommentGiverNameVisibleTo(FeedbackResponseCommentAttributes frComment, FeedbackQuestionAttributes qn,
            FeedbackParticipantType viewerType){
        if(frComment.isVisibilityFollowingFeedbackQuestion){
            return true;
        } else {
            return frComment.showGiverNameTo.contains(viewerType);
        }
    }
    
    public String getResponseCommentVisibilityString(FeedbackQuestionAttributes qn){
        return "GIVER," + removeBracketsForArrayString(qn.showResponsesTo.toString());
    }
    
    public String getResponseCommentVisibilityString(FeedbackResponseCommentAttributes frComment, FeedbackQuestionAttributes qn){
        if(frComment.isVisibilityFollowingFeedbackQuestion){
            return getResponseCommentVisibilityString(qn);
        } else {
            return removeBracketsForArrayString(frComment.showCommentTo.toString());
        }
    }
    
    public String getResponseCommentGiverNameVisibilityString(FeedbackQuestionAttributes qn){
        return "GIVER,RECEIVER,OWN_TEAM_MEMBERS,RECEIVER_TEAM_MEMBERS,STUDENTS,INSTRUCTORS";
    }
    
    public String getResponseCommentGiverNameVisibilityString(FeedbackResponseCommentAttributes frComment, FeedbackQuestionAttributes qn){
        if(frComment.isVisibilityFollowingFeedbackQuestion){
            return getResponseCommentGiverNameVisibilityString(qn);
        } else {
            return removeBracketsForArrayString(frComment.showGiverNameTo.toString());
        }
    }
    
    public boolean isResponseCommentPublicToRecipient(FeedbackQuestionAttributes question) {
        return (question.giverType == FeedbackParticipantType.STUDENTS
                || question.giverType == FeedbackParticipantType.TEAMS) 
                    || (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                            || question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                            || question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                            || question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS));
    }
        
    public boolean isInstructorAllowedForPrivilegeOnComment(CommentAttributes comment, String privilegeName) {
        // TODO: remember to come back and change this if later CommentAttributes.recipients can have multiple later!!!
        Logic logic = new Logic();
        if (this.currentInstructor == null) {
            return false;
        }
        if (comment.recipientType == CommentRecipientType.COURSE) {
            return this.currentInstructor.isAllowedForPrivilege(privilegeName);          
        } else if (comment.recipientType == CommentRecipientType.SECTION) {
            String section = "";
            if (!comment.recipients.isEmpty()) {
                Iterator<String> iterator = comment.recipients.iterator();
                section = iterator.next();
            }
            return this.currentInstructor.isAllowedForPrivilege(section, privilegeName);
        } else if (comment.recipientType == CommentRecipientType.TEAM) {
            String team = "";
            String section = "";
            if (!comment.recipients.isEmpty()) {
                Iterator<String> iterator = comment.recipients.iterator();
                team = iterator.next();
            }
            List<StudentAttributes> students = logic.getStudentsForTeam(team, courseId);
            if (!students.isEmpty()) {
                section = students.get(0).section;
            }
            return this.currentInstructor.isAllowedForPrivilege(section, privilegeName);
        } else if (comment.recipientType == CommentRecipientType.PERSON) {
            String studentEmail = "";
            String section = "";
            if (!comment.recipients.isEmpty()) {
                Iterator<String> iterator = comment.recipients.iterator();
                studentEmail = iterator.next();
            }
            StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
            if (student != null) {
                section = student.section;
            }
            return this.currentInstructor.isAllowedForPrivilege(section, privilegeName);
        } else {
            // TODO: implement this if instructor is later allowed to be added to recipients
            return false;
        }
    }
}
