package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
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
        return "student".equals(commentRecipient)
               || "team".equals(commentRecipient)
               || "section".equals(commentRecipient);
    }
}
