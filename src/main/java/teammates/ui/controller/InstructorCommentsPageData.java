package teammates.ui.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.logic.api.Logic;

/**
 * PageData: the data to be used in the InstructorCommentsPage
 */
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
    public List<FeedbackSessionAttributes> feedbackSessions;
    public String previousPageLink;
    public String nextPageLink;
    public int numberOfPendingComments = 0;
    
    public InstructorCommentsPageData(AccountAttributes account) {
        super(account);
    }
    
    public Map<String, List<CommentAttributes>> getComments() {
        return comments;
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public List<String> getCoursePaginationList() {
        return coursePaginationList;
    }
    
    public List<FeedbackSessionAttributes> getFeedbackSessions() {
        return feedbackSessions;
    }
    
    public String getGiverName(String giverEmail) {
        InstructorAttributes instructor = roster.getInstructorForEmail(giverEmail);
        String giverDisplay = giverEmail;
        if (giverEmail.equals(COMMENT_GIVER_NAME_THAT_COMES_FIRST)) {
            giverDisplay = "You";
        } else if (instructor != null) {
            String title = instructor.displayedName;
            giverDisplay = title + " " + instructor.name;
        }
        return giverDisplay;
    }
    
    public String getNextPageLink() {
        return nextPageLink;
    }
    
    public int getNumberOfPendingComments() {
        return numberOfPendingComments;
    }
    
    public String getPreviousPageLink() {
        return previousPageLink;
    }
    
    public String getRecipientNames(Set<String> recipients) {
        StringBuilder namesStringBuilder = new StringBuilder();
        int i = 0;
        for (String recipient : recipients) {
            if (i == recipients.size() - 1 && recipients.size() > 1) {
                namesStringBuilder.append("and ");
            }
            StudentAttributes student = roster.getStudentForEmail(recipient);
            if (courseId.equals(recipient)) { 
                namesStringBuilder.append("<b>All students in this course</b>, ");
            } else if (student != null) {
                if (recipients.size() == 1) {
                    namesStringBuilder.append("<b>" + student.name + "</b>" 
                            + " (" + student.team + ", <a href=\"mailto:" + student.email + "\">" + student.email + "</a>), ");
                } else {
                    namesStringBuilder.append("<b>" + student.name + "</b>" + ", ");
                }
            } else {
                namesStringBuilder.append("<b>" + recipient + "</b>" + ", ");
            }
            i++;
        }
        String namesString = namesStringBuilder.toString();
        return removeEndComma(namesString);
    }
    
    public boolean isDisplayArchive() {
        return isDisplayArchive;
    }
    
    public boolean isResponseCommentPublicToRecipient(FeedbackResponseCommentAttributes comment) {
        return comment.showCommentTo.size() > 0;
    }
    
    public boolean isViewingDraft() {
        return isViewingDraft;
    }
        
    public boolean isInstructorAllowedForPrivilegeOnComment(CommentAttributes comment, String privilegeName) {
        // TODO: remember to come back and change this if later CommentAttributes.recipients can have multiple later!!!
        Logic logic = new Logic();
        if (this.currentInstructor == null) {
            return false;
        }
        if (comment.recipientType == CommentParticipantType.COURSE) {
            return this.currentInstructor.isAllowedForPrivilege(privilegeName);          
        } else if (comment.recipientType == CommentParticipantType.SECTION) {
            String section = "";
            if (!comment.recipients.isEmpty()) {
                Iterator<String> iterator = comment.recipients.iterator();
                section = iterator.next();
            }
            return this.currentInstructor.isAllowedForPrivilege(section, privilegeName);
        } else if (comment.recipientType == CommentParticipantType.TEAM) {
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
        } else if (comment.recipientType == CommentParticipantType.PERSON) {
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
