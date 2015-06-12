package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.ui.template.StudentInfoTable;
import teammates.ui.template.StudentProfile;

public class InstructorCourseStudentDetailsPageData extends PageData {
    
    StudentProfile studentProfile;
    StudentInfoTable studentInfoTable;
    private String commentRecipient;

    public InstructorCourseStudentDetailsPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(StudentAttributes student, StudentProfileAttributes studentProfile, boolean isAbleToAddComment, boolean hasSection, String commentRecipient) {
        String pictureUrl = studentProfile == null || studentProfile.pictureKey == null
                            || studentProfile.pictureKey.isEmpty()
                          ? Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH
                          : Const.ActionURIs.STUDENT_PROFILE_PICTURE + "?"
                                + Const.ParamsNames.BLOB_KEY + "=" + studentProfile.pictureKey
                                + "&user=" + account.googleId;
        this.studentProfile = new StudentProfile(student.name, studentProfile, pictureUrl);
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
}
