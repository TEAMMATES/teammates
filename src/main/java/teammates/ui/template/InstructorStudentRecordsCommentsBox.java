package teammates.ui.template;

import java.util.List;

public class InstructorStudentRecordsCommentsBox {

    private String studentName;
    private String section;
    private String courseId;
    private String email;
    private String googleId;
    private boolean instructorAllowedToGiveComment;
    private List<InstructorStudentRecordsComment> comments;

    public InstructorStudentRecordsCommentsBox(String studentName, String section, String courseId,
                                               String email, String googleId,
                                               List<InstructorStudentRecordsComment> comments,
                                               boolean isInstructorAllowedToGiveComment) {
        this.studentName = studentName;
        this.section = section;
        this.courseId = courseId;
        this.email = email;
        this.googleId = googleId;
        this.instructorAllowedToGiveComment = isInstructorAllowedToGiveComment;
        this.comments = comments;
    }
    
    public String getStudentName() {
        return studentName;
    }

    public String getSection() {
        return section;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getEmail() {
        return email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public boolean isInstructorAllowedToGiveComment() {
        return instructorAllowedToGiveComment;
    }

    public List<InstructorStudentRecordsComment> getComments() {
        return comments;
    }

}
