package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Sanitizer;
import teammates.ui.template.InstructorStudentRecordsFeedbackSession;
import teammates.ui.template.InstructorStudentRecordsMoreInfoModal;
import teammates.ui.template.InstructorStudentRecordsStudentProfile;

public class InstructorStudentRecordsPageData extends PageData {

    public String courseId;
    public InstructorAttributes currentInstructor;
    public List<CommentAttributes> comments;
    public String showCommentBox;
    public String studentName;
    public InstructorStudentRecordsStudentProfile studentProfile;
    public InstructorStudentRecordsMoreInfoModal moreInfoModal;
    public List<InstructorStudentRecordsFeedbackSession> sessions;

    public InstructorStudentRecordsPageData(AccountAttributes account, StudentProfileAttributes spa,
                                            StudentAttributes student, List<FeedbackSessionAttributes> sessions,
                                            String courseId) {
        super(account);
        String studentName = Sanitizer.sanitizeForHtml(student.name);
        this.studentName = studentName;
        this.courseId = courseId;
        if (spa == null) {
            this.studentProfile = null;
        } else {
            this.studentProfile = new InstructorStudentRecordsStudentProfile(spa, account);
        }
        this.moreInfoModal = new InstructorStudentRecordsMoreInfoModal(studentName, spa.moreInfo);
        this.sessions = new ArrayList<InstructorStudentRecordsFeedbackSession>();
        for (FeedbackSessionAttributes session: sessions) {
            this.sessions.add(new InstructorStudentRecordsFeedbackSession(courseId, student.email,
                                                                          account.googleId,
                                                                          session.feedbackSessionName));
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
    
    public InstructorStudentRecordsMoreInfoModal getMoreInfoModal() {
        return moreInfoModal;
    }
    
    public List<InstructorStudentRecordsFeedbackSession> getSessions() {
        return sessions;
    }

}
