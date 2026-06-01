package teammates.ui.request;

/**
 * The create request for the course.
 */
public class CourseCreateRequest extends CourseBasicRequest {
    private String courseId;
    private String institute;

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        super.validate();
        assertTrue(courseId != null, "Course ID should not be null");
        assertTrue(institute != null, "Institute should not be null");
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }
}
