package teammates.ui.request;

import java.util.UUID;

import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The create request for the course.
 */
public class CourseCreateRequest extends CourseBasicRequest {
    private String courseId;
    private UUID instituteId;

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        super.validate();
        validateTrue(courseId != null, "Course ID should not be null");
        validateTrue(instituteId != null, "Institute should not be null");
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public UUID getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(UUID instituteId) {
        this.instituteId = instituteId;
    }
}
