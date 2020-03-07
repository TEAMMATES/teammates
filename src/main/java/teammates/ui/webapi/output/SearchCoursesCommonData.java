package teammates.ui.webapi.output;

/**
 * SearchCourse data for both instructors and students.
 */
public class SearchCoursesCommonData extends ApiOutput {
    private String email;
    private String courseId;
    private String courseName;
    private String institute;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getEmail() {
        return email;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getInstitute() {
        return institute;
    }
}
