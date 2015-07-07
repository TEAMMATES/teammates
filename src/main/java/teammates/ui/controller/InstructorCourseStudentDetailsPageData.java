package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.ui.template.StudentInfoTable;
import teammates.ui.template.StudentProfile;

public class InstructorCourseStudentDetailsPageData extends PageData {
    
    private StudentProfile studentProfile;
    private StudentInfoTable studentInfoTable;
    private String commentRecipient;

    public InstructorCourseStudentDetailsPageData(AccountAttributes account, StudentAttributes student,
            StudentProfileAttributes studentProfile, boolean isAbleToAddComment, boolean hasSection,
            String commentRecipient) {
        super(account);
        if (studentProfile != null) {
            String pictureUrl = getPictureUrl(studentProfile.pictureKey);
            this.studentProfile = new StudentProfile(student.name, studentProfile, pictureUrl);
        }
        this.studentInfoTable = new StudentInfoTable(student, isAbleToAddComment, hasSection);
        this.commentRecipient = commentRecipient;
    }
    
    public StudentProfile getStudentProfile() {
        return studentProfile;
    }
    
    public StudentInfoTable getStudentInfoTable() {
        return studentInfoTable;
    }
    
    public String getCommentRecipient() {
        return commentRecipient;
    }
    
    public boolean isCommentBoxShown() {
        return commentRecipient != null && (commentRecipient.equals("student")
                                            || commentRecipient.equals("team")
                                            || commentRecipient.equals("section"));
    }
}
