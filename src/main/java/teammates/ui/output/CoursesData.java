package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.storage.sqlentity.Course;

/**
 * The API output for a list of courses.
 */
public class CoursesData extends ApiOutput {

    private List<CourseData> courses;

    public CoursesData(List<Course> coursesList) {
        this.courses = coursesList.stream().map(CourseData::new).collect(Collectors.toList());
    }

    public List<CourseData> getCourses() {
        return courses;
    }

}
