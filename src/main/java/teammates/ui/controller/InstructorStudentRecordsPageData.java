package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.SessionAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Sanitizer;
import teammates.ui.template.InstructorStudentRecordsMoreInfoModal;
import teammates.ui.template.InstructorStudentRecordsStudentProfile;

public class InstructorStudentRecordsPageData extends PageData {

    public String courseId;
    public InstructorAttributes currentInstructor;
    public List<CommentAttributes> comments;
    public List<SessionAttributes> sessions;
    public String showCommentBox;
    public String studentName;
    public InstructorStudentRecordsStudentProfile studentProfile;
    public InstructorStudentRecordsMoreInfoModal moreInfoModal;

    public InstructorStudentRecordsPageData(AccountAttributes account, StudentProfileAttributes spa,
                                            String studentName) {
        super(account);
        studentName = Sanitizer.sanitizeForHtml(studentName);
        this.studentName = studentName;
        if (spa == null) {
            this.studentProfile = null;
        } else {
            this.studentProfile = new InstructorStudentRecordsStudentProfile(spa, account);
        }
        this.moreInfoModal = new InstructorStudentRecordsMoreInfoModal(studentName, spa.moreInfo);
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
    
    public InstructorStudentRecordsMoreInfoModal getMoreInfoModal() {
        return moreInfoModal;
    }

}
