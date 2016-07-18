package teammates.logic.api;

import java.util.Iterator;
import java.util.List;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;

public class AccessControlUtil {
    private Logic logic = new Logic();
    private static AccessControlUtil instance;
    
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
        String recipient = null;
        if (!comment.recipients.isEmpty()) {
            Iterator<String> iterator = comment.recipients.iterator();
            recipient = iterator.next();
        }
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
}
