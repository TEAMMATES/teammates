package teammates.logic.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;

public class AccessControlUtil {
    private Logic logic = new Logic();
    private static AccessControlUtil instance;
    
    public static int COMMENT_PERMISSIONS_IS_DISPLAYED_INDEX = 0;
    public static int COMMENT_PERMISSIONS_GIVER_IS_DISPLAYED_INDEX = 1;
    public static int COMMENT_PERMISSIONS_RECIPIENT_IS_DISPLAYED_INDEX = 2;
    
    public static int COMMENT_PERMISSIONS_TOTAL_SIZE = 3;
    
    public static AccessControlUtil inst() {
        if (instance == null) {
            instance = new AccessControlUtil();
        }
        return instance;
    }
    
    public boolean isInstructorAllowedToViewComment(InstructorAttributes instructor, CommentAttributes comment) {
        if (instructor == null || comment == null) {
            return false;
        }
        
        // trivial case: instructor is comment giver
        if (comment.giverEmail.equals(instructor.email)) {
            return true;
        }
        
        return isInstructorAllowedForPrivilegeOnComment(
                instructor, comment, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS);
    }
    
    public boolean isInstructorAllowedToModifyComment(InstructorAttributes instructor, CommentAttributes comment) {
        if (instructor == null || comment == null) {
            return false;
        }
        
        // trivial case: instructor is comment giver
        if (comment.giverEmail.equals(instructor.email)) {
            return true;
        }
        
        return isInstructorAllowedForPrivilegeOnComment(
                instructor, comment, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS);
    }
    
    private boolean isInstructorAllowedForPrivilegeOnComment(InstructorAttributes instructor, CommentAttributes comment,
                                                             String privilegeName) {
        // TODO: remember to come back and change this if
        // CommentAttributes.recipients can have multiple recipients later
        
        String courseId = instructor.courseId;
        
        // unexpected case: comment has no recipients
        if (comment.recipients.isEmpty()) {
            return false;
        }
        Iterator<String> iterator = comment.recipients.iterator();
        String recipient = iterator.next();
        
        if (comment.recipientType == CommentParticipantType.COURSE) {
            return instructor.isAllowedForPrivilege(privilegeName);
        } else if (comment.recipientType == CommentParticipantType.SECTION) {
            return instructor.isAllowedForPrivilege(recipient, privilegeName);
        } else if (comment.recipientType == CommentParticipantType.TEAM) {
            String section = "";
            List<StudentAttributes> students = logic.getStudentsForTeam(recipient, courseId);
            if (!students.isEmpty()) {
                section = students.get(0).section;
            }
            return instructor.isAllowedForPrivilege(section, privilegeName);
        } else if (comment.recipientType == CommentParticipantType.PERSON) {
            String section = "";
            StudentAttributes student = logic.getStudentForEmail(courseId, recipient);
            if (student != null) {
                section = student.section;
            }
            return instructor.isAllowedForPrivilege(section, privilegeName);
        } else {
            // TODO: implement this if instructor is later allowed to be added
            // to recipients
            return false;
        }
    }
    
    public List<Boolean> getVisibilityPermissionsOnCommentForStudent(
            CommentAttributes comment, StudentAttributes student, List<String> teammatesEmails,
            List<String> sectionStudentsEmails, List<String> teamsInSection) {
        List<Boolean> permissions;
        
        // the following if-else if-else blocks rely on their ordering to properly classify the comment
        // eg. a comment directed at the student's team will not be classified as "in the same section as student"
        
        // individual student level
        if (isStudentRecipientOfComment(comment, student)) {
            permissions = getPermissionsForComment(comment, CommentParticipantType.PERSON, true);
            
        // team level
        } else if (isStudentTeamRecipientOfComment(comment, student)) {
            permissions = getPermissionsForComment(comment, CommentParticipantType.TEAM, true);
        } else if (isStudentInSameTeamAsRecipientOfComment(comment, student, teammatesEmails)) {
            permissions = getPermissionsForComment(comment, CommentParticipantType.TEAM, false);

        // section level
        } else if (isStudentSectionRecipientOfComment(comment, student)) {
            permissions = getPermissionsForComment(comment, CommentParticipantType.SECTION, true);
        } else if (isStudentInSameSectionAsRecipientOfComment(comment, student, sectionStudentsEmails, teamsInSection)) {
            permissions = getPermissionsForComment(comment, CommentParticipantType.SECTION, false);

        // course level
        } else if (isStudentCourseRecipientOfComment(comment, student)) {
            permissions = getPermissionsForComment(comment, CommentParticipantType.COURSE, true);
        } else if (isStudentInSameCourseAsRecipientOfComment(comment, student)) {
            permissions = getPermissionsForComment(comment, CommentParticipantType.COURSE, false);
            
        // comment not in same course as student, so student should not be able to view the comment
        } else {
            permissions = new ArrayList<Boolean>();
            for (int i = 0; i < COMMENT_PERMISSIONS_TOTAL_SIZE; i++) {
                permissions.add(false);
            }
        }
        return permissions;
    }
    
    private boolean isStudentRecipientOfComment(CommentAttributes comment, StudentAttributes student) {
        return CommentParticipantType.PERSON.equals(comment.recipientType) && comment.recipients.contains(student.email);
    }
    
    private boolean isStudentTeamRecipientOfComment(CommentAttributes comment, StudentAttributes student) {
        return CommentParticipantType.TEAM.equals(comment.recipientType)
                && comment.recipients.contains(Sanitizer.sanitizeForHtml(student.team));
    }
    
    private boolean isStudentInSameTeamAsRecipientOfComment(
            CommentAttributes comment, StudentAttributes student, List<String> teammatesEmails) {
        // if recipient type is not a student, this comment is not directed at a teammate
        if (!CommentParticipantType.PERSON.equals(comment.recipientType)) {
            return false;
        }
        
        return isCommentRecipientPresentInList(comment, teammatesEmails);
    }
    
    private boolean isStudentSectionRecipientOfComment(CommentAttributes comment, StudentAttributes student) {
        return CommentParticipantType.SECTION.equals(comment.recipientType) && comment.recipients.contains(student.section);
    }
    
    private boolean isStudentInSameSectionAsRecipientOfComment(
            CommentAttributes comment, StudentAttributes student,
            List<String> sectionStudentsEmails, List<String> teamsInSection) {
        // if recipient type is not a student or team, this comment is not directed at a recipient in the
        // same section as the student
        if (!CommentParticipantType.PERSON.equals(comment.recipientType)
                && !CommentParticipantType.TEAM.equals(comment.recipientType)) {
            return false;
        }
        
        // check if student is in same section
        if (CommentParticipantType.PERSON.equals(comment.recipientType)
                && isCommentRecipientPresentInList(comment, sectionStudentsEmails)) {
            return true;
            
        // check if team is in same section
        } else if (isCommentRecipientPresentInList(comment, teamsInSection)) {
            return true;
        }
        
        return false;
    }
    
    private boolean isStudentCourseRecipientOfComment(CommentAttributes comment, StudentAttributes student) {
        return CommentParticipantType.COURSE.equals(comment.recipientType) && comment.courseId.equals(student.course);
    }
    
    private boolean isStudentInSameCourseAsRecipientOfComment(CommentAttributes comment, StudentAttributes student) {
        return comment.courseId.equals(student.course);
    }
    
    private boolean isCommentRecipientPresentInList(CommentAttributes comment, List<String> candidateRecipients) {
        if (!comment.recipients.isEmpty()) {
            String commentRecipient = comment.recipients.iterator().next();
            return candidateRecipients.contains(commentRecipient);
        }
        return false;
    }
    
    private List<Boolean> getPermissionsForComment(
            CommentAttributes comment, CommentParticipantType participantType, boolean isRecipientOfComment) {
        List<Boolean> permissions = new ArrayList<Boolean>();
        for (int i = 0; i < COMMENT_PERMISSIONS_TOTAL_SIZE; i++) {
            permissions.add(false);
        }
        
        permissions.set(COMMENT_PERMISSIONS_IS_DISPLAYED_INDEX, comment.showCommentTo.contains(participantType));
        permissions.set(COMMENT_PERMISSIONS_GIVER_IS_DISPLAYED_INDEX, comment.showGiverNameTo.contains(participantType));
        // if the person is the recipient of the comment, do not hide the recipient name
        permissions.set(COMMENT_PERMISSIONS_RECIPIENT_IS_DISPLAYED_INDEX,
                        isRecipientOfComment || comment.showRecipientNameTo.contains(participantType));
        return permissions;
    }
}
