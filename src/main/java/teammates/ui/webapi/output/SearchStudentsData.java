package teammates.ui.webapi.output;

import java.util.List;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Output format for student search results.
 */
public class SearchStudentsData extends ApiOutput {
    private final List<StudentAttributes> students;

    public SearchStudentsData(List<StudentAttributes> students) {
        this.students = students;
    }

    public List<StudentAttributes> getStudents() {
        return students;
    }
}
