package teammates.ui.webapi.output;

import java.util.List;

/**
 * Data when searching for courses.
 */
public class SearchCoursesData extends ApiOutput {
    private List<SearchCoursesCommonData> students;
    private List<SearchCoursesCommonData> instructors;

    public SearchCoursesData(List<SearchCoursesCommonData> students,
            List<SearchCoursesCommonData> instructors) {
        this.students = students;
        this.instructors = instructors;
    }

    public List<SearchCoursesCommonData> getStudents() {
        return students;
    }

    public List<SearchCoursesCommonData> getInstructors() {
        return instructors;
    }
}
