package teammates.ui.webapi.output;

import java.util.List;

/**
 * Contains links.
 */
public class SearchLinksData extends ApiOutput {
    private final List<SearchLinksStudentData> students;
    private final List<SearchLinksInstructorData> instructors;

    public SearchLinksData(
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

