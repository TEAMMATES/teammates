package teammates.ui.template;

public class ArchivedCoursesTableRow {
    private String courseId;
    private String courseName;
    private String actions;
    
    public ArchivedCoursesTableRow(String courseIdParam, String courseNameParam, String actionsParam) {
        this.courseId = courseIdParam;
        this.courseName = courseNameParam;
        this.actions = actionsParam;
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public String getActions() {
        return actions;
    }
}
