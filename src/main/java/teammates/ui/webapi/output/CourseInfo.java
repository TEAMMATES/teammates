package teammates.ui.webapi.action;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Data transfer objects for {@link CourseAttributes} between controller and HTTP.
 */
public class CourseInfo {

    /**
     * The output format of a course.
     */
    public static class CourseResponse extends ApiOutput {
        private final String courseId;
        private final String courseName;
        private final String creationDate;
        private final String timeZone;

        public CourseResponse(CourseAttributes courseAttributes) {
            this.courseId = courseAttributes.getId();
            this.courseName = courseAttributes.getName();
            this.creationDate = courseAttributes.getCreatedAtDateString();
            this.timeZone = courseAttributes.getTimeZone().getId();
        }

        public String getCourseId() {
            return courseId;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getCreationDate() {
            return creationDate;
        }

        public String getTimeZone() {
            return timeZone;
        }
    }

    /**
     * Response of a list of courses.
     */
    public static class CoursesResponse extends ApiOutput {
        private final List<CourseResponse> courses;

        public CoursesResponse(List<CourseAttributes> courseAttributesList) {
            courses = courseAttributesList.stream().map(CourseResponse::new).collect(Collectors.toList());
        }

        public List<CourseResponse> getCourses() {
            return courses;
        }
    }
}
