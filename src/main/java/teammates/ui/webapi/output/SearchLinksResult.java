package teammates.ui.webapi.output;

import java.util.List;

/**
 * Contains Links search results.
 */
public class SearchLinksResult extends ApiOutput {
    private final List<SearchLinksStudentData> students;
    private final List<SearchLinksInstructorData> instructors;

    public SearchLinksResult(
            List<SearchLinksStudentData> students,
            List<SearchLinksInstructorData> instructors
    ) {
        this.students = students;
        this.instructors = instructors;
    }

    public List<SearchLinksStudentData> getStudents() {
        return students;
    }

    public List<SearchLinksInstructorData> getInstructors() {
        return instructors;
    }
}
