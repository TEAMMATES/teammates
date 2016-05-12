package teammates.ui.template;

public class InstructorStudentListFilterCourse {

    private String courseId;
    private String courseName;

    public InstructorStudentListFilterCourse(final String courseId, final String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

}
