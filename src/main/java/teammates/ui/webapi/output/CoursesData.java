package teammates.ui.webapi.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.CourseAttributes;

/**
 * The API output format of a list of {@link CourseAttributes}.
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
