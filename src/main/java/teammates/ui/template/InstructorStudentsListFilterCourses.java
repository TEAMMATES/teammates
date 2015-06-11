package teammates.ui.template;

public class InstructorStudentsListFilterCourses {

    private String courseId;
    private String courseName;
    
    public InstructorStudentsListFilterCourses(String courseId, String courseName) {
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
