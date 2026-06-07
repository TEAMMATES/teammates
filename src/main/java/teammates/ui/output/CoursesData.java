package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.storage.entity.Course;

/**
 * The API output for a list of courses.
 */
public class CoursesData implements ApiOutput {

    private final List<CourseViewData> courses;

    public CoursesData(List<Course> coursesList) {
        this.courses = coursesList.stream()
                .map(course -> new CourseViewData(new CourseData(course)))
                .collect(Collectors.toList());
    }

    public List<CourseViewData> getCourses() {
        return courses;
    }

}
