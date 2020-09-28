package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.CourseAttributes;

/**
 * The API output for a list of courses.
 */
public class CoursesData extends ApiOutput {
    private final List<CourseData> courses;

    public CoursesData(List<CourseAttributes> courseAttributesList) {
        courses = courseAttributesList.stream().map(CourseData::new).collect(Collectors.toList());
    }

    public List<CourseData> getCourses() {
        return courses;
    }
}
