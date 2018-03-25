package teammates.ui.template;

public class CourseInstructorCopyTableRow {
    private String courseId;
    private String instructorName;
    private String instrctorAccessLevel;
    private String instructorDisplayedName;
    private String instructorEmail;

    public CourseInstructorCopyTableRow(String courseId, String instructorName, String instructorAccessLevel,
                                        String instructorDisplayedName, String instructorEmail) {
        this.courseId = courseId;
        this.instructorName = instructorName;
        this.instrctorAccessLevel = instructorAccessLevel;
        this.instructorDisplayedName = instructorDisplayedName;
        this.instructorEmail = instructorEmail;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public String getInstructorAccessLevel() {
        return instrctorAccessLevel;
    }

    public String getInstructorDisplayedName() {
        return instructorDisplayedName;
    }

    public String getInstructorEmail() {
        return instructorEmail;
    }
}
