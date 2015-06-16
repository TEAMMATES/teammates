package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.SessionAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.ui.template.InstructorStudentRecordsStudentProfile;

public class InstructorStudentRecordsPageData extends PageData {

    public String courseId;
    public InstructorAttributes currentInstructor;
    public List<CommentAttributes> comments;
    public List<SessionAttributes> sessions;
    public String showCommentBox;
    public String studentName;
    public InstructorStudentRecordsStudentProfile studentProfile;

    public InstructorStudentRecordsPageData(AccountAttributes account, StudentProfileAttributes spa) {
        super(account);
        if (spa == null) {
            this.studentProfile = null;
        } else {
            this.studentProfile = new InstructorStudentRecordsStudentProfile(spa, account);
        }
    }
    
    public String getShowCommentBox() {
        return showCommentBox;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public InstructorStudentRecordsStudentProfile getStudentProfile() {
        return studentProfile;
    }

}
