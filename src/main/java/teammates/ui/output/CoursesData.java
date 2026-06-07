package teammates.ui.output;

import java.util.List;

import teammates.storage.entity.Course;

/**
 * The API output for a list of courses.
 */
public class CoursesData implements ApiOutput {

    private final List<CourseViewData> courses;

    public CoursesData(List<Course> coursesList) {
        this.courses = coursesList.stream()
                .map(course -> new CourseViewData(new CourseData(course)))
                .toList();
    }

    public List<CourseViewData> getCourses() {
        return courses;
    }

}
