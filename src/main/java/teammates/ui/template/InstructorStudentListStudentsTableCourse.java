package teammates.ui.template;

public class InstructorStudentListStudentsTableCourse {

    private boolean isCourseArchived;
    private String courseId;
    private String courseName;
    private String googleId;
    private String instructorCourseEnrollLink;
    private boolean isInstructorAllowedToModify;

    public InstructorStudentListStudentsTableCourse(boolean isCourseArchived, String courseId, String courseName,
                                                    String googleId,
                                                    String instructorCourseEnrollLink,
                                                    boolean isInstructorAllowedToModify) {
        this.isCourseArchived = isCourseArchived;
        this.courseId = courseId;
        this.courseName = courseName;
        this.googleId = googleId;
        this.instructorCourseEnrollLink = instructorCourseEnrollLink;
        this.isInstructorAllowedToModify = isInstructorAllowedToModify;
    }

    public boolean isCourseArchived() {
        return isCourseArchived;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getInstructorCourseEnrollLink() {
        return instructorCourseEnrollLink;
    }

    public boolean isInstructorAllowedToModify() {
        return isInstructorAllowedToModify;
    }

}
