package teammates.ui.request;

import teammates.common.util.FieldValidator;
import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The basic request of modifying a course.
 */
public class CourseBasicRequest extends BasicRequest {
    private String courseName;
    private String timeZone;

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(courseName != null, "Course name should not be null");
        validateTrue(timeZone != null, "Time zone should not be null");

        String timeZoneErrorMessage = FieldValidator.getInvalidityInfoForTimeZone(timeZone);
        validateTrue(timeZoneErrorMessage.isEmpty(), timeZoneErrorMessage);
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
